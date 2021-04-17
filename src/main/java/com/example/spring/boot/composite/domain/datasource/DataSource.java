package com.example.spring.boot.composite.domain.datasource;

import lombok.Data;

import java.util.Set;

@Data
public class DataSource {

    private Compose compose;

    private Set<Source> source;

    private Joins joins;
}
