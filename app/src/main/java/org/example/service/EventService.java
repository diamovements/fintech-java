package org.example.service;

import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.EventEntity;
import org.example.entity.EventFilter;
import org.example.entity.PlaceEntity;
import org.example.entity.request.EventRequest;
import org.example.repository.EventRepository;
import org.example.repository.PlaceRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {
    private final EventRepository eventRepository;
    private final PlaceRepository placeRepository;
    public List<EventEntity> getAllEvents() {
        log.info("Getting all events: {}", eventRepository.findAll());
        return eventRepository.findAll();
    }

    public EventEntity getEventById(Long id) {
        log.info("Getting event by id: {}", eventRepository.findById(id));
        return eventRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Event " + id));
    }

    public EventEntity createEvent(EventRequest request) {
        PlaceEntity place = placeRepository.findById(request.getPlaceId()).orElseThrow(() ->
                new IllegalArgumentException("Place not found"));
        log.info("Getting place: {}", place.toString());
        EventEntity event = new EventEntity();
        event.setTitle(request.getName());
        event.setFromDate(request.getDateFrom());
        event.setPlace_id(place);
        event.setToDate(request.getDateTo());
        log.info("Saving event: {}", event.toString());

        return eventRepository.save(event);
    }

    public EventEntity updateEvent(Long id, EventRequest request) {
        EventEntity existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event " + id));
        log.info("Existing event: {}", existingEvent.toString());

        if (request.getPlaceId() != null) {
            PlaceEntity place = placeRepository.findById(request.getPlaceId())
                    .orElseThrow(() -> new IllegalArgumentException("Place " + request.getPlaceId()));
            log.info("Place of existing event: {}", place.toString());
            existingEvent.setPlace_id(place);
        }

        existingEvent.setTitle(request.getName());
        existingEvent.setFromDate(request.getDateFrom());
        existingEvent.setToDate(request.getDateTo());
        log.info("Updated event: {}", existingEvent.toString());

        return eventRepository.save(existingEvent);
    }

    public void deleteEvent(Long id) {
        EventEntity event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event " + id));
        log.info("Event to delete: {}", event.toString());
        eventRepository.delete(event);
    }

    public Optional<List<EventEntity>> findByFilter(EventFilter filter) {
        Specification<EventEntity> spec = Specification.where(null);

        if (filter.getName() != null && !filter.getName().isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + filter.getName().toLowerCase() + "%")
            );
        }
        if (filter.getPlace_id() != null) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                root.fetch("place", JoinType.LEFT);
                return criteriaBuilder.equal(root.get("place").get("id"), filter.getPlace_id());
            });
        }
        if (filter.getFromDate() != null && filter.getToDate() != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.between(root.get("fromDate"), filter.getFromDate(), filter.getToDate())
            );
        } else if (filter.getFromDate() != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("fromDate"), filter.getFromDate())
            );
        } else if (filter.getToDate() != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("toDate"), filter.getToDate())
            );
        }
        log.info("Spec events: {}", eventRepository.findAll(spec));
        return eventRepository.findAll(spec);
    }
}
