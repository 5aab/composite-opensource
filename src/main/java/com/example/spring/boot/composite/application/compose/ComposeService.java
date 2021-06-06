package com.example.spring.boot.composite.application.compose;

import com.example.spring.boot.composite.domain.datasource.JoinType;
import com.example.spring.boot.composite.domain.datasource.*;
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
import org.jooq.tools.StringUtils;
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
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.name;
import static org.jooq.impl.SQLDataType.INTEGER;

@Slf4j
@Service
@AllArgsConstructor
public class ComposeService {

    private DSLContext create;
    private RestTemplate restTemplate;
    private XmlMapper xmlMapper;
    private RandomNumberGenerator randomNumberGenerator;


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

    public String queryData(Query query) {
        try {
            DataSource dataSource = xmlMapper.readValue(new File("C:\\FAST\\ws\\composite-opensource\\src\\main\\resources\\DataSource.xml"), DataSource.class);
            Set<String> allColumnsInQuery = findAllColumnsInQuery(query);
            return findMatchColumnInRestSource(query, allColumnsInQuery, dataSource);
        } catch (IOException e) {
            log.error("Error while reading datasource", e);
            throw new RuntimeException(e);
        }

    }

    private String findMatchColumnInRestSource(Query query, Set<String> allColumnsInQuery, DataSource dataSource) {
        Set<ComposedColumn> composeColumns = findComposeColumns(allColumnsInQuery, dataSource);
        Map<String, List<ComposedColumn>> composeColumnsRestType = composeColumns.stream().filter(c -> dataSource.isRestType(c.getSourceName())).collect(groupingBy(ComposedColumn::getSourceName));
        Map<String, List<ComposedColumn>> composeColumnsDatabaseType = composeColumns.stream().filter(Predicate.not(c -> dataSource.isRestType(c.getSourceName()))).collect(groupingBy(ComposedColumn::getSourceName));
        Set<String> tempTables = createTempTables(composeColumnsRestType);
        fillTablesWithData(tempTables, composeColumnsRestType, dataSource);
        return queryDataFromTables(query, dataSource, tempTables);
    }

    private String queryDataFromTables(Query query, DataSource dataSource, Set<String> tempTables) {
        Joins joins = dataSource.getJoins();
        final List<Field<?>> fields = getSelectClauseColumns(query);
        SelectSelectStep<Record> select = create.select(fields);
        //select.from(table(name("BOOK")));
        joinTables(tempTables, joins, select);
        Result<Record> result = select.fetch();
        log.info("Records {}", result);
        return result.formatJSON();
    }

    private void joinTables(Set<String> tempTables, Joins joins, SelectSelectStep<Record> select) {
        joins.getJoin().forEach(join -> joinTables(join, tempTables, select));
    }

    private void joinTables(Join join, Set<String> tempTables, SelectSelectStep<Record> select) {
        if (JoinType.CARTESIAN.equals(join.getType())) {
            select.from(resolve(join.getLeftSource(), tempTables)).naturalJoin(resolve(join.getRightSource(), tempTables));
        } else if (JoinType.INNER_LEFT.equals(join.getType())) {
            join.getJoinColumn().forEach(jc -> {
                select.from(resolve(join.getLeftSource(), tempTables)).leftJoin(resolve(join.getRightSource(), tempTables)).on(joinClause(join, tempTables));
            });
        } else if (JoinType.INNER_RIGHT.equals(join.getType())) {
            join.getJoinColumn().forEach(jc -> {
                select.from(resolve(join.getLeftSource(), tempTables)).rightJoin(resolve(join.getRightSource(), tempTables)).on(joinClause(join, tempTables));
            });
        } else {
            join.getJoinColumn().forEach(jc -> {
                select.from(resolve(join.getLeftSource(), tempTables)).innerJoin(resolve(join.getRightSource(), tempTables)).on(joinClause(join, tempTables));
            });
        }
    }

    private String joinClause(Join join, Set<String> tempTables) {
        return StringUtils.join(join.getJoinColumn().stream()
                .map(jc -> resolve(join.getLeftSource(), tempTables) + "." + jc.getLeftColumn() + "=" + resolve(join.getRightSource(), tempTables) + "." + jc.getRightColumn())
                .toArray(String[]::new)
                , " and ").toUpperCase(Locale.ROOT);
    }

    private String resolve(String fromSource, Set<String> tempTables) {
        Map<String, String> map = tempTables.stream().collect(toMap(this::extractTableKey, Function.identity()));
        return map.containsKey(fromSource) ? map.get(fromSource) : fromSource;
    }

    private void fillTablesWithData(Set<String> tempTables, Map<String, List<ComposedColumn>> composeColumnsRestType, DataSource dataSource) {
        for (String tempTable : tempTables) {
            String tableKey = tempTable.substring(0, tempTable.indexOf("_"));
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
        return tempTable.substring(0, tempTable.indexOf("_"));
    }

    private Set<String> createTempTables(Map<String, List<ComposedColumn>> composeColumnsRestType) {
        Set<String> tables = Sets.newHashSet();
        for (Map.Entry<String, List<ComposedColumn>> entry : composeColumnsRestType.entrySet()) {
            String tableName = entry.getKey().toUpperCase(Locale.ROOT) + "_" + randomNumberGenerator.nextNonNegative();
            tables.add(tableName);
            CreateTableColumnStep step = create.createGlobalTemporaryTable(tableName);
            entry.getValue().forEach(composedColumn -> step.column(composedColumn.getLabel().toUpperCase(Locale.ROOT), INTEGER));
            step.execute();
        }
        return tables;
    }

    private Set<ComposedColumn> findComposeColumns(Set<String> allColumnsInQuery, DataSource dataSource) {
        return dataSource.getCompose().getComposedColumns().stream().filter(c -> allColumnsInQuery.contains(c.getLabel())).collect(toSet());
    }

    private Set<String> findAllColumnsInQuery(Query query) {
        Set<String> selectClauseColumns = getSelectClauseColumns(query, toSet());
        Set<String> whereClauseColumns = query.getCriteria().getMatchAll().stream().map(WhereCondition::getProperty).map(String::toLowerCase).collect(toSet());
        selectClauseColumns.addAll(whereClauseColumns);
        return selectClauseColumns;
    }

    private <T, R> R getSelectClauseColumns(Query query, Collector<String, ?, R> collector) {
        return query.getProperties().stream().map(String::toLowerCase).collect(collector);
    }

    private List<Field<?>> getSelectClauseColumns(Query query) {
        return query.getProperties().stream().map(String::toUpperCase).map(column -> field(name(column), String.class)).collect(toList());
    }
}
