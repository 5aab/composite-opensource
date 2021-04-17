package com.example.spring.boot.composite.domain.datasource;

import lombok.Data;

@Data
public class DatabaseConnection extends Connection {

    private String jdbcUrl;
    private String userId;
    private String password;
}
