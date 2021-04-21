package com.example.spring.boot.composite.rest;

import com.example.spring.boot.composite.application.compose.ComposeService;
import com.example.spring.boot.composite.domain.query.Query;
import lombok.AllArgsConstructor;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("compose")
@Controller
@AllArgsConstructor
public class ComposeController {

    private ComposeService composeService;

    @ResponseBody
    @PostMapping("create")
    public String createTable() {
       return composeService.create();
    }

    @ResponseBody
    @PostMapping(value = "query")
    public Result<Record> queryData(@RequestBody Query query) {
        return composeService.queryData(query);
    }


}
