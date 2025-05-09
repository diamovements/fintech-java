package org.example;

import org.example.entity.Location;
import org.example.service.LocationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


@SpringBootTest
@AutoConfigureMockMvc
public class LocationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LocationService locationService;

    @Test
    void getLocationByIdTest_shouldReturnLocation() throws Exception {
        Location location = new Location("msk", "Москва");
        Mockito.when(locationService.getLocation(anyString())).thenReturn(location);

        mockMvc.perform(get("/api/v1/locations/msk"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value(location.getSlug()))
                .andExpect(jsonPath("$.name").value(location.getName()));
    }

    @Test
    void getAllLocationsTest_shouldReturnAllLocations() throws Exception {
        Location location1 = new Location("msk", "Москва");
        Location location2 = new Location("ekb", "Екатеринбург");
        Mockito.when(locationService.getAllLocations()).thenReturn(List.of(new Location[]{location1, location2}));

        mockMvc.perform(get("/api/v1/locations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slug").value(location1.getSlug()))
                .andExpect(jsonPath("$[0].name").value(location1.getName()))
                .andExpect(jsonPath("$[1].slug").value(location2.getSlug()))
                .andExpect(jsonPath("$[1].name").value(location2.getName()));
    }

    @Test
    void addLocationTest_shouldAddLocation() throws Exception {
        Mockito.doNothing().when(locationService).addLocation(anyString(), any(Location.class));

        mockMvc.perform(post("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"slug\":\"msk\",\"name\":\"Москва\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Location has been added successfully"));
    }

    @Test
    void updateLocationTest_shouldUpdateLocation() throws Exception {
        Mockito.doNothing().when(locationService).updateLocation(anyString(), any(Location.class));

        mockMvc.perform(put("/api/v1/locations/msk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"slug\":\"msk\",\"name\":\"Москва\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Location has been updated successfully"));
    }

    @Test
    void deleteLocationTest_shouldDeleteLocation() throws Exception {
        Mockito.doNothing().when(locationService).deleteLocation(anyString());

        mockMvc.perform(delete("/api/v1/locations/msk"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteLocationTest_shouldThrowException() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("Location not found")).when(locationService).deleteLocation(anyString());

        mockMvc.perform(delete("/api/v1/locations/msk"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getLocationByIdTest_shouldThrowException() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("Location not found")).when(locationService).getLocation(anyString());

        mockMvc.perform(get("/api/v1/locations/msk"))
                .andExpect(status().isBadRequest());
    }
}
