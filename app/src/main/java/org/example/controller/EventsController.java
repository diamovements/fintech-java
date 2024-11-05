package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.entity.EventEntity;
import org.example.entity.EventFilter;
import org.example.entity.request.EventRequest;
import org.example.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
public class EventsController {
    private final EventService eventService;

    @GetMapping
    public List<EventEntity> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventEntity> getEventById(@PathVariable("id") Long id) {
        EventEntity event = eventService.getEventById(id);
        return event != null ? ResponseEntity.ok(event) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<EventEntity> createEvent(@Valid @RequestBody EventRequest request) {
        EventEntity event = eventService.createEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventEntity> updateEvent(@PathVariable("id") Long id, @Valid @RequestBody EventRequest request) {
        EventEntity updatedEvent = eventService.updateEvent(id, request);
        return updatedEvent != null ? ResponseEntity.ok(updatedEvent) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<EventEntity> deleteEvent(@PathVariable("id") Long id) {
        EventEntity toDelete = eventService.getEventById(id);
        if (toDelete != null) {
            eventService.deleteEvent(id);
        }
        return ResponseEntity.ok(toDelete);
    }

    @GetMapping("/filter")
    public List<EventEntity> findByFilter(@RequestBody EventFilter filter) {
        Optional<List<EventEntity>> events = eventService.findByFilter(filter);
        return events.orElseGet(List::of);
    }

}
