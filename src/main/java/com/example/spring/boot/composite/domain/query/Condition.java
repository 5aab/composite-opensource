package com.example.spring.boot.composite.domain.query;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        //visible = true,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MatchAll.class, name = "matchAll"),
        @JsonSubTypes.Type(value = MatchAny.class, name = "matchAny")
})
public abstract class Condition {
}
