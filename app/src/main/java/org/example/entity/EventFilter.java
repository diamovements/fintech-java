package org.example.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class EventFilter {
    private String name;
    private Date fromDate;
    private Date toDate;
    private Long place_id;
}
