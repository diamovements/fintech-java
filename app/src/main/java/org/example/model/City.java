package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class City {
    @JacksonXmlProperty(localName = "slug")
    public String slug;
    @JacksonXmlProperty(localName = "coords")
    public Coords coords;

    @Override
    public String toString() {
        return "City{" +
                "slug='" + slug + '\'' +
                ", coords=" + coords +
                '}';
    }
}
