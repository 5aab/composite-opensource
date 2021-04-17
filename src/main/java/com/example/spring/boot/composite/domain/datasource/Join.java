package com.example.spring.boot.composite.domain.datasource;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class Join {
    @JacksonXmlProperty(isAttribute = true)
    private String toSource;
    @JacksonXmlProperty(isAttribute = true)
    private String fromColumn;
    @JacksonXmlProperty(isAttribute = true)
    private String fromSource;
    @JacksonXmlProperty(isAttribute = true)
    private String toColumn;
}
