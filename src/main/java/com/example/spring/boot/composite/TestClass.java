package com.example.spring.boot.composite;

import com.example.spring.boot.composite.domain.datasource.*;
import com.example.spring.boot.composite.domain.query.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.collect.Sets;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class TestClass {
    public static void main3(String[] args) throws Exception {
        JacksonXmlModule xmlModule = new JacksonXmlModule();
        xmlModule.setDefaultUseWrapper(false);
        XmlMapper xmlMapper = new XmlMapper(xmlModule);
        DataSource dataSource = xmlMapper.readValue(new File("C:\\FAST\\ws\\composite-opensource\\src\\main\\resources\\DataSource.xml"), DataSource.class);
        System.out.println(dataSource);
    }

    public static void main2(String[] args) throws Exception {

        JacksonXmlModule xmlModule = new JacksonXmlModule();
        xmlModule.setDefaultUseWrapper(false);
        XmlMapper xmlMapper = new XmlMapper(xmlModule);
        DataSource dataSource = new DataSource();
        Compose compose = new Compose();
        Set<Source> sources = new HashSet<>();
        Joins joins = new Joins();
        Source source = new Source();
        source.setName("abc");
        source.setType("da-connection");
        Column column = new Column();
        column.setLabel("testLabel");
        column.setName("abcCol");
        column.setType("String");
        source.setColumn(Sets.newHashSet(column));
        sources.add(source);
        dataSource.setSource(sources);
        dataSource.setJoins(joins);
        dataSource.setCompose(compose);
        DatabaseConnection databaseConnection = new DatabaseConnection();
        databaseConnection.setPassword("xcfjfgfj");
        databaseConnection.setUserId("userId");
        databaseConnection.setJdbcUrl("jdbc//:oracle:9876");
        source.setConnection(databaseConnection);
        System.out.println(xmlMapper.writeValueAsString(dataSource));
    }

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper =new ObjectMapper();
        HashSet<WhereCondition> whereConditions = Sets.newHashSet(
                WhereCondition.create("expiry", ">=", Sets.newHashSet("2020-01-01")),
                WhereCondition.create("expiry", "<=", Sets.newHashSet("2020-01-05")),
                WhereCondition.create("cost", "<=", Sets.newHashSet("1000000")),
                WhereCondition.create("power", ">", Sets.newHashSet("786 hp")),
                WhereCondition.create("seats", "=", Sets.newHashSet("2"))
        );
        Set<Condition> condition = Sets.newHashSet(MatchAll.create(whereConditions));
        Criteria criteria = Criteria.create(whereConditions);
        Query query = Query.create(criteria,"request_909876","mahajan", Sets.newHashSet("car_name","id","color","seats","capacity","engine","power","insurance_name","cost"));
        String queryJson = mapper.writeValueAsString(query);
        System.out.println(queryJson);
    }
}
