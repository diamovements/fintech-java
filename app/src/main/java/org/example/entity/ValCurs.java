package org.example.entity;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

public record ValCurs(@JacksonXmlProperty(isAttribute = true, localName = "Date")
                      String date,
                      @JacksonXmlProperty(isAttribute = true, localName = "name")
                      String name,
                      @JacksonXmlElementWrapper(useWrapping = false)
                      @JacksonXmlProperty(localName = "Valute")
                      List<Valute> valuteList) {}