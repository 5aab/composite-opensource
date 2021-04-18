package com.example.spring.boot.composite.domain.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
public class Query implements Serializable {
    private Criteria criteria;
    private String requestTag;
    private String onBehalfOf;
    private Set<String> properties;
}
