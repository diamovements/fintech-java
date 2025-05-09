package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entity.EventEntity;
import org.example.entity.PlaceEntity;
import org.example.entity.request.EventRequest;
import org.example.repository.EventRepository;
import org.example.repository.PlaceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.text.SimpleDateFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class EventIntegrationTest {

    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("user")
            .withPassword("password");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createEventTest_shouldCreateEvent() throws Exception {
        PlaceEntity place = new PlaceEntity();
        place.setSlug("msk");
        place.setName("Москва");
        place = placeRepository.save(place);

        EventRequest eventRequest = new EventRequest();
        eventRequest.setName("Концерт");
        eventRequest.setDateFrom(new SimpleDateFormat("yyyy-MM-dd").parse("2024-10-25"));
        eventRequest.setDateTo(new SimpleDateFormat("yyyy-MM-dd").parse("2024-10-26"));
        eventRequest.setPlaceId(place.getId());

        String eventJson = objectMapper.writeValueAsString(eventRequest);

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Концерт"));

        List<EventEntity> events = eventRepository.findAll();
        assertEquals(1, events.size());
        assertEquals("Концерт", events.get(0).getTitle());
    }
    @Test
    public void updateEventTest_shouldUpdateEvent() throws Exception {
        PlaceEntity place = new PlaceEntity();
        place.setSlug("msk");
        place.setName("Москва");
        place = placeRepository.save(place);

        EventEntity event = new EventEntity();
        event.setTitle("Концерт");
        event.setFromDate(new SimpleDateFormat("yyyy-MM-dd").parse("2024-10-25"));
        event.setToDate(new SimpleDateFormat("yyyy-MM-dd").parse("2024-10-26"));
        event.setPlace_id(place);
        event = eventRepository.save(event);

        EventRequest updateRequest = new EventRequest();
        updateRequest.setName("Фестиваль");
        updateRequest.setDateFrom(new SimpleDateFormat("yyyy-MM-dd").parse("2024-10-25"));
        updateRequest.setDateTo(new SimpleDateFormat("yyyy-MM-dd").parse("2024-10-26"));
        updateRequest.setPlaceId(place.getId());

        String updateJson = objectMapper.writeValueAsString(updateRequest);

        mockMvc.perform(put("/api/v1/events/" + event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Фестиваль"));

        EventEntity updatedEvent = eventRepository.findById(event.getId()).orElseThrow();
        assertEquals("Фестиваль", updatedEvent.getTitle());
    }

    @Test
    public void getByIdTest_shouldGetEventById() throws Exception {
        PlaceEntity place = new PlaceEntity();
        place.setSlug("msk");
        place.setName("Москва");
        place = placeRepository.save(place);

        EventEntity event = new EventEntity();
        event.setTitle("Концерт");
        event.setFromDate(new SimpleDateFormat("yyyy-MM-dd").parse("2024-10-25"));
        event.setToDate(new SimpleDateFormat("yyyy-MM-dd").parse("2024-10-26"));
        event.setPlace_id(place);
        event = eventRepository.save(event);

        mockMvc.perform(get("/api/v1/events/" + event.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Концерт"));
    }
    @Test
    public void getByIdTest_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/events/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteEvent_shouldDeleteEvent() throws Exception {
        PlaceEntity place = new PlaceEntity();
        place.setSlug("msk");
        place.setName("Москва");
        place = placeRepository.save(place);

        EventEntity event = new EventEntity();
        event.setTitle("Концерт");
        event.setFromDate(new SimpleDateFormat("yyyy-MM-dd").parse("2024-10-25"));
        event.setToDate(new SimpleDateFormat("yyyy-MM-dd").parse("2024-10-26"));
        event.setPlace_id(place);
        event = eventRepository.save(event);

        mockMvc.perform(delete("/api/v1/events/" + event.getId()))
                .andExpect(status().isOk());
    }
}
