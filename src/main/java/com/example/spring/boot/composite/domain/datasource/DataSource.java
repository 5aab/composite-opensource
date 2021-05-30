package com.example.spring.boot.composite.domain.datasource;

import lombok.Data;

import java.util.Optional;
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
        return getRestTypeSources().stream().anyMatch(s -> sourceName.equalsIgnoreCase(s.getName()));
    }

    public Connection getConnectionDetails(String sourceName){
        Optional<Source> matchingSource = source.stream().filter(s->s.getName().equalsIgnoreCase(sourceName)).findFirst();
        if(matchingSource.isPresent()){
            return matchingSource.get().getConnection();
        }else{
            throw new RuntimeException("Source Not found :"+sourceName);
        }
    }
}
