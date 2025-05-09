package org.example.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@Data
@NoArgsConstructor
public class Category {
    @JsonProperty("id")
    private int id;
    @JsonProperty("slug")
    private String slug;
    @JsonProperty("name")
    private String name;
}
