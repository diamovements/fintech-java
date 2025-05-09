package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entity.PlaceEntity;
import org.example.entity.request.PlaceRequest;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class PlaceIntegrationTest {
    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("user")
            .withPassword("password");

    @Autowired
    private MockMvc mockMvc;

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
    public void createPlace_shouldCreatePlace() throws Exception {
        PlaceRequest request = new PlaceRequest();
        request.setName("Екатеринбург");
        request.setSlug("ekb");
        String placeJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/v1/places")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(placeJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Екатеринбург"));

        List<PlaceEntity> places = placeRepository.findAll();
        assertEquals(1, places.size());
        assertEquals("Екатеринбург", places.get(0).getName());
    }

    @Test
    public void updatePlace_shouldUpdatePlace() throws Exception {
        PlaceEntity place = new PlaceEntity();
        place.setName("Москва");
        place.setSlug("msk");
        place = placeRepository.save(place);

        PlaceRequest updateRequest = new PlaceRequest();
        updateRequest.setName("Екатеринбург");
        updateRequest.setSlug("ekb");
        String updateJson = objectMapper.writeValueAsString(updateRequest);

        mockMvc.perform(put("/api/v1/places/" + place.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Екатеринбург"));

        PlaceEntity updatedPlace = placeRepository.findById(place.getId()).orElseThrow();
        assertEquals("Екатеринбург", updatedPlace.getName());
        assertEquals("ekb", updatedPlace.getSlug());
    }

    @Test
    public void getByIdTest_shouldGetPlaceById() throws Exception {
        PlaceEntity place = new PlaceEntity();
        place.setName("Москва");
        place.setSlug("msk");
        place = placeRepository.save(place);

        mockMvc.perform(get("/api/v1/places/" + place.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Москва"));
    }

    @Test
    public void getByIdTest_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/places/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deletePlace_shouldDeletePlace() throws Exception {
        PlaceEntity place = new PlaceEntity();
        place.setName("Москва");
        place.setSlug("msk");
        place = placeRepository.save(place);

        mockMvc.perform(delete("/api/v1/places/" + place.getId()))
                .andExpect(status().isOk());
    }

}
