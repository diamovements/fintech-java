package org.example;

import org.example.dao.CategoryDatabase;
import org.example.dao.LocationDatabase;
import org.example.dao.UniversalDatabase;
import org.example.entity.Category;
import org.example.entity.Location;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.wiremock.integrations.testcontainers.WireMockContainer;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class AppIntegrationTest {

    @Container
    public static WireMockContainer wireMockContainer = new WireMockContainer(DockerImageName.parse("wiremock/wiremock:latest"))
            .withMappingFromResource("wiremock/categories.json")
            .withMappingFromResource("wiremock/locations.json");

    @Autowired
    private RestTemplate restTemplate;

    private static final UniversalDatabase<Integer, Category> categoryDb = new CategoryDatabase();
    private static final UniversalDatabase<String, Location> locationDb = new LocationDatabase();

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        String wireMockUrl = String.format("http://%s:%d",
                wireMockContainer.getHost(),
                wireMockContainer.getMappedPort(8080));
        registry.add("api.category", () -> wireMockUrl + "/public-api/v1.4/place-categories");
        registry.add("api.location", () -> wireMockUrl + "/public-api/v1.4/locations");
    }

    @Value("${api.category}")
    private String category_url;

    @Value("${api.location}")
    private String location_url;

    @BeforeAll
    public static void setUp() {
        wireMockContainer.start();
    }

    @AfterAll
    public static void tearDown() {
        wireMockContainer.stop();
    }

    @Test
    public void testGetPlaceCategories() {
        Optional<Category[]> categories = Optional.ofNullable(restTemplate.getForObject(category_url, Category[].class));
        for (Category c : categories.get()) {
            categoryDb.put(c.getId(), c);
        }

        assertThat(categoryDb.getAll()).hasSize(3);
        assertThat(categoryDb.getAll()).extracting("name").containsExactlyInAnyOrder("Питомники", "Развлечения", "Аэропорты");
    }

    @Test
    public void testGetLocations() {
        Optional<Location[]> locations = Optional.ofNullable(restTemplate.getForObject(location_url, Location[].class));
        for (Location l : locations.get()) {
            locationDb.put(l.getSlug(), l);
        }

        assertThat(locationDb.getAll()).hasSize(3);
        assertThat(locationDb.getAll()).extracting("name").containsExactlyInAnyOrder("Москва", "Екатеринбург", "Казань");
    }
}
