package com.example.spring.boot.composite.domain.datasource;

import lombok.Data;

@Data
public class RestConnection extends Connection {

    private String url;
    private String userId;
    private String password;
    private String authType;
}
