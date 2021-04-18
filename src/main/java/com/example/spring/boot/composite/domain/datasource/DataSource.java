package com.example.spring.boot.composite.domain.datasource;

import lombok.Data;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Data
public class DataSource {

    private Compose compose;

    private Set<Source> source;

    private Joins joins;

    public Set<Source> getRestTypeSources() {
        return source.stream().filter(s -> s.getType().equals("rest")).collect(toSet());
    }

    public Set<Source> getDbTypeSources() {
        return source.stream().filter(s -> s.getType().equals("database")).collect(toSet());
    }

    public boolean isRestType(String sourceName) {
        return getRestTypeSources().stream().anyMatch(s -> sourceName.equals(s.getName()));
    }
}
