package com.example.spring.boot.composite.domain.datasource;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Locale;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"leftSource","rightSource"})
public class Join {
    @JacksonXmlProperty(isAttribute = true)
    private String rightSource;
    @JacksonXmlProperty(isAttribute = true)
    private String leftSource;
    @JacksonXmlProperty(isAttribute = true)
    private JoinType type;
    @JacksonXmlProperty(isAttribute = true)
    private Set<JoinColumn> joinColumn;

    public String getLeftSource() {
        return leftSource.toUpperCase(Locale.ROOT);
    }

    public String getRightSource() {
        return rightSource.toUpperCase(Locale.ROOT);
    }
}
