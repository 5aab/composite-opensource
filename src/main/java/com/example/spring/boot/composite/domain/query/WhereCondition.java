package com.example.spring.boot.composite.domain.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
public class WhereCondition {

    private String property;
    private String operator;
    private Set<String> value;
}
