package org.example;

import org.example.entity.Event;
import org.example.entity.request.EventRequest;
import org.example.service.EventService;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EventServiceTest {

    @Test
    void getPopularEventsTest_shouldReturnEvents() {
        EventService eventService = mock(EventService.class);
        Event event1 = new Event(1, "title1", "slug1", "200 рублей");
        Event event2 = new Event(2, "title2", "slug2", "500 рублей");

        when(eventService.getPopularEvents(any())).thenReturn(Mono.just(List.of(new Event[]{event1, event2})));
        Mono<List<Event>> result = eventService.getPopularEvents(new EventRequest());

        StepVerifier.create(result)
                .expectNext(List.of(new Event[]{event1, event2}))
                .verifyComplete();
        verify(eventService, times(1)).getPopularEvents(any());
    }

    @Test
    void getPopularEventsTest_shouldThrowException() {
        EventService eventService = mock(EventService.class);
        when(eventService.getPopularEvents(any())).thenReturn(Mono.error(new RuntimeException("Events not found")));

        Mono<List<Event>> result = eventService.getPopularEvents(new EventRequest());

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
        verify(eventService, times(1)).getPopularEvents(any());
    }
}
