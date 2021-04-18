package com.example.spring.boot.composite.rest;

import com.example.spring.boot.composite.domain.query.Query;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.jooq.impl.SQLDataType.INTEGER;

@ResponseBody
@Controller
@AllArgsConstructor
public class ComposeController {

    private DSLContext create;

    @PostMapping("create")
    public String createTable() {
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

    @PostMapping("query")
    public String queryDate(Query query) {
        return "temporary table created";
    }


}
