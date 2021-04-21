package com.example.spring.boot.composite.application.compose;

import com.example.spring.boot.composite.domain.datasource.ComposedColumn;
import com.example.spring.boot.composite.domain.datasource.DataSource;
import com.example.spring.boot.composite.domain.datasource.Joins;
import com.example.spring.boot.composite.domain.datasource.RestConnection;
import com.example.spring.boot.composite.domain.query.Query;
import com.example.spring.boot.composite.domain.query.WhereCondition;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static org.jooq.impl.SQLDataType.INTEGER;

@Slf4j
@Service
@AllArgsConstructor
public class ComposeService {

    private DSLContext create;
    private RestTemplate restTemplate;
    private XmlMapper xmlMapper;
    //private ObjectMapper mapper;


    public String create() {
        int execute = create.createTemporaryTable("book_archive")
                .column("column1", INTEGER)
                .execute();
        int execute1 = create.createGlobalTemporaryTable("g_book_archive")
                .column("column1", INTEGER)
                .execute();

        create.dropTemporaryTableIfExists("PUBLIC.g_book_archive");
        List<Table<?>> tables = create.meta().getTables("g_book_archive");
        System.out.println(tables);
        return "temporary table created";
    }

    public Result<Record> queryData(Query query) {
        try {
            DataSource dataSource = xmlMapper.readValue(new File("C:\\FAST\\ws\\composite-opensource\\src\\main\\resources\\DataSource.xml"), DataSource.class);
            Set<String> allColumnsInQuery = findAllColumnsInQuery(query);
            return findMatchColumnInRestSource(allColumnsInQuery, dataSource);
        } catch (IOException e) {
            log.error("Error while reading datasource", e);
            throw new RuntimeException(e);
        }

    }

    private Result<Record> findMatchColumnInRestSource(Set<String> allColumnsInQuery, DataSource dataSource) {
        Set<ComposedColumn> composeColumns = findComposeColumns(allColumnsInQuery, dataSource);
        Map<String, List<ComposedColumn>> composeColumnsRestType = composeColumns.stream().filter(c -> dataSource.isRestType(c.getSourceName())).collect(groupingBy(ComposedColumn::getSourceName));
        Map<String, List<ComposedColumn>> composeColumnsDatabaseType = composeColumns.stream().filter(Predicate.not(c -> dataSource.isRestType(c.getSourceName()))).collect(groupingBy(ComposedColumn::getSourceName));
        Set<String> tempTables = createTempTables(composeColumnsRestType);
        fillTablesWithData(tempTables, composeColumnsRestType, dataSource);
        return queryDataFromTables(dataSource, tempTables);
    }

    private Result<Record> queryDataFromTables(DataSource dataSource, Set<String> tempTables) {
        Joins joins = dataSource.getJoins();
        SelectSelectStep<Record> select = create.select();
        joins.getJoin().forEach(j -> select.from(resolve(j.getFromSource(), tempTables)).naturalJoin(resolve(j.getToSource(), tempTables)));
        Result<Record> result = select.fetch();
        log.info("Records {}", result);
        return result;
    }

    private String resolve(String fromSource, Set<String> tempTables) {
        Map<String, String> map = tempTables.stream().collect(toMap(this::extractTableKey, Function.identity()));
        return map.containsKey(fromSource) ? map.get(fromSource) : fromSource;
    }

    private void fillTablesWithData(Set<String> tempTables, Map<String, List<ComposedColumn>> composeColumnsRestType, DataSource dataSource) {
        for (String tempTable : tempTables) {
            String tableKey = tempTable.substring(0, tempTable.indexOf("-"));
            List<ComposedColumn> composedColumns = composeColumnsRestType.get(tableKey);
            RestConnection connection = (RestConnection) dataSource.getConnectionDetails(tableKey);

            ResponseEntity<String> response = restTemplate.exchange(connection.getUrl(), HttpMethod.GET, null, new ParameterizedTypeReference<String>() {
            });
            Set<Map<String, Object>> data;
            try {
                ObjectMapper mapper = new ObjectMapper();
                data = mapper.readValue(response.getBody(), new TypeReference<Set<Map<String, Object>>>() {
                });
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            insertData(tempTable, data, composeColumnsRestType);
            log.info("here it is{}", response);

        }
    }

    private void insertData(String tempTable, Set<Map<String, Object>> data, Map<String, List<ComposedColumn>> composeColumnsRestType) {
        Optional<Table<?>> table = create.meta().getTables(tempTable).stream().findFirst();
        List<ComposedColumn> composedColumns = composeColumnsRestType.get(extractTableKey(tempTable));
        if (table.isPresent()) {
            Collection<? extends Field<?>> fields = composedColumns.stream()
                    .map(c -> table.get().field(c.getLabel())).collect(Collectors.toCollection(ArrayList::new));

            List<List<Object>> values = new ArrayList<>();
            for (Map<String, Object> record : data) {
                List<Object> value = new ArrayList<>();
                for (ComposedColumn composedColumn : composedColumns) {
                    value.add(String.valueOf(record.get(composedColumn.getName())));
                }
                values.add(value);
            }
            InsertValuesStepN<?> insertValuesStepN = create.insertInto(table.get(), fields);
            values.forEach(domino -> insertValuesStepN.values(domino));
            insertValuesStepN.execute();
        } else {
            throw new RuntimeException("Table not found :" + table);
        }
    }

    private String extractTableKey(String tempTable) {
        return tempTable.substring(0, tempTable.indexOf("-"));
    }

    private Set<String> createTempTables(Map<String, List<ComposedColumn>> composeColumnsRestType) {
        Set<String> tables = Sets.newHashSet();
        for (Map.Entry<String, List<ComposedColumn>> entry : composeColumnsRestType.entrySet()) {
            String tableName = entry.getKey() + "-" + UUID.randomUUID().getMostSignificantBits();
            tables.add(tableName);
            CreateTableColumnStep step = create.createGlobalTemporaryTable(tableName);
            entry.getValue().forEach(composedColumn -> step.column(composedColumn.getLabel(), INTEGER));
            step.execute();
        }
        return tables;
    }

    private Set<ComposedColumn> findComposeColumns(Set<String> allColumnsInQuery, DataSource dataSource) {
        return dataSource.getCompose().getComposedColumns().stream().filter(c -> allColumnsInQuery.contains(c.getLabel())).collect(toSet());
    }

    /*private boolean retainMatch(Source source, Set<String> allColumnsInQuery) {
        for(Column column : source.getColumn()){
            column.getName()
        }
        allColumnsInQuery.retainAll(s.)
        return false;
    }*/

    private Set<String> findAllColumnsInQuery(Query query) {
        Set<String> selectClauseColumns = query.getProperties().stream().map(String::toLowerCase).collect(toSet());
        Set<String> whereClauseColumns = query.getCriteria().getMatchAll().stream().map(WhereCondition::getProperty).map(String::toLowerCase).collect(toSet());
        selectClauseColumns.addAll(whereClauseColumns);
        return selectClauseColumns;
    }
}
