package com.example.spring.boot.composite.domain.datasource;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.Set;

@Data
public class Source {

    @JacksonXmlProperty(isAttribute = true)
    private String name;
    private Connection connection;
    @JacksonXmlProperty(isAttribute = true)
    private String type;
    @XmlElement(name="column")
    private Set<Column> column;


}
