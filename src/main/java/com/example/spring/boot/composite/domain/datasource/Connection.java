package com.example.spring.boot.composite.domain.datasource;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        //visible = true,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = DatabaseConnection.class, name = "databaseConnection"),
        @JsonSubTypes.Type(value = RestConnection.class, name = "restConnection")
})
public abstract class Connection {
}
