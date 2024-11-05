package org.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "events")
@Getter
@Setter
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "start_date")
    private Date fromDate;

    @Column(name = "end_date")
    private Date toDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", referencedColumnName = "id")
    @JsonIgnore
    private PlaceEntity place_id;
}
