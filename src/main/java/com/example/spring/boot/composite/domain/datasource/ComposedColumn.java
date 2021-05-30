package com.example.spring.boot.composite.domain.datasource;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Locale;

@Data
@EqualsAndHashCode(of = {"sourceName", "label"})
public class ComposedColumn {
    @JacksonXmlProperty(isAttribute = true)
    private String name;
    @JacksonXmlProperty(isAttribute = true)
    private String sourceName;
    @JacksonXmlProperty(isAttribute = true)
    private String label;
    @JacksonXmlProperty(isAttribute = true)
    private String type;

    public String getSourceName(){
        return sourceName.toUpperCase(Locale.ROOT);
    }
}
