package org.example;

import org.example.entity.Event;
import org.example.entity.request.EventRequest;
import org.example.service.EventService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Mono;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @Test
    void getEventsTest_shouldReturnEvents() throws Exception {
        Event event1 = new Event(1, "title1", "slug1", "200 рублей");
        Event event2 = new Event(2, "title2", "slug2", "500 рублей");
        Mockito.when(eventService.getPopularEvents(any())).thenReturn(Mono.just(List.of(new Event[]{event1, event2})));
        mockMvc.perform(get("/api/v1/events")
                                .param("dateFrom", "10/10/2024")
                                .param("dateTo", "13/10/2024")
                                .param("budget", "7.99")
                                .param("currency", "USD"))
                .andExpect(status().isOk());
    }

    @Test
    void getEventsTest_shouldThrowException() {
        Mockito.when(eventService.getPopularEvents(any()))
                .thenThrow(new RuntimeException("Events not found"));

        assertThrows(RuntimeException.class, () -> {
            eventService.getPopularEvents(new EventRequest());
        });
    }
}
