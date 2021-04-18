package com.example.spring.boot.composite.domain.datasource;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "label")
public class ComposedColumn {
    @JacksonXmlProperty(isAttribute = true)
    private String name;
    @JacksonXmlProperty(isAttribute = true)
    private String sourceName;
    @JacksonXmlProperty(isAttribute = true)
    private String label;
    @JacksonXmlProperty(isAttribute = true)
    private String type;
}
