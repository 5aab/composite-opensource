package com.example.spring.boot.composite.domain.datasource;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class JoinColumn {
    @JacksonXmlProperty(isAttribute = true)
    private String leftColumn;
    @JacksonXmlProperty(isAttribute = true)
    private String rightColumn;
}
