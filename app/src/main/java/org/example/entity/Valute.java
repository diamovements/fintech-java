package org.example.entity;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public record Valute(
        @JacksonXmlProperty(isAttribute = true, localName = "ID")
        String id,
        @JacksonXmlProperty(localName = "NumCode")
        String numCode,
        @JacksonXmlProperty(localName = "CharCode")
        String charCode,
        @JacksonXmlProperty(localName = "Nominal")
        int nominal,
        @JacksonXmlProperty(localName = "Name")
        String name,
        @JacksonXmlProperty(localName = "Value")
        String value,
        @JacksonXmlProperty(localName = "VunitRate")
        String vunitRate
) {}