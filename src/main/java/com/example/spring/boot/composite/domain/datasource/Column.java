package com.example.spring.boot.composite.domain.datasource;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class Column {

    @JacksonXmlProperty(isAttribute = true)
    private String name;
    @JacksonXmlProperty(isAttribute = true)
    private String sourceName;
    @JacksonXmlProperty(isAttribute = true)
    private String label;
    @JacksonXmlProperty(isAttribute = true)
    private String type;
}
