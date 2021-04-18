package com.example.spring.boot.composite.domain.query;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor(staticName = "create")
public class Query {
    private Criteria criteria;
    private String requestTag;
    private String onBehalfOf;
    private Set<String> properties;
}
