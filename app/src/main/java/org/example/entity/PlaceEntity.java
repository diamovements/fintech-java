package org.example.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "places")
@Getter
@Setter
public class PlaceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "slug")
    @JsonProperty("slug")
    private String slug;

    @Column(name = "name")
    @JsonProperty("name")
    private String name;

    @OneToMany(mappedBy = "place_id", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<EventEntity> events;
}
