package com.example.spring.boot.composite.domain.datasource;

import lombok.Data;

import java.util.Set;

@Data
public class Compose {
    private Set<ComposedColumn> composedColumns;
}
