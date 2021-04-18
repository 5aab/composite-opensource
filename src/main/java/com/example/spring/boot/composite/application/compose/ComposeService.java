package com.example.spring.boot.composite.application.compose;

import com.example.spring.boot.composite.domain.datasource.DataSource;
import com.example.spring.boot.composite.domain.query.Query;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;

import static org.jooq.impl.SQLDataType.INTEGER;

@Slf4j
@Service
@AllArgsConstructor
public class ComposeService {

    private DSLContext create;
    private RestTemplate restTemplate;
    private XmlMapper xmlMapper;


    public String create() {
        int execute = create.createTemporaryTable("book_archive")
                .column("column1", INTEGER)
                .execute();
        int execute1 = create.createGlobalTemporaryTable("g_book_archive")
                .column("column1", INTEGER)
                .execute();

        create.dropTemporaryTableIfExists("PUBLIC.g_book_archive");
        System.out.println(create.meta().getTables());
        return "temporary table created";
    }

    public void queryData(Query query) {

        try {
            DataSource dataSource = xmlMapper.readValue(new File("C:\\FAST\\ws\\composite-opensource\\src\\main\\resources\\DataSource.xml"), DataSource.class);
        } catch (IOException e) {
            log.error("Error while reading datasource",e);
        }
    }
}
