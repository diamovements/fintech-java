package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.example.entity.request.SignInRequest;
import org.example.entity.request.SignUpRequest;
import org.example.entity.response.AuthenticationResponse;
import org.example.security.AuthenticationService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
public class AuthIntegrationTest {
    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("user")
            .withPassword("password");

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    private static WireMockServer wireMockServer;


    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @BeforeAll
    public static void setUp() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
    }

    @AfterAll
    public static void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void testSignUp() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest("a@b.com", "123456");
        authenticationService.signUp(signUpRequest);

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(signUpRequest)))
                .andExpect(status().isOk());
    }

    @Test
    public void testSignIn() throws Exception {
        SignInRequest signInRequest = new SignInRequest("a@b.com", "123456", true);
        when(authenticationService.signIn(signInRequest)).thenReturn(new AuthenticationResponse("my-token"));

        mockMvc.perform(post("/api/v1/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(signInRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    public void testLogout() {

        wireMockServer.stubFor(WireMock.post(urlEqualTo("/api/v1/auth/logout"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"token\":\"my-token\"}")));

        given()
                .header("Authorization", "Bearer my-token")
                .when()
                .post("/api/v1/auth/logout")
                .then()
                .statusCode(200);
    }

    @Test
    public void testRequestResetPassword() {

        wireMockServer.stubFor(WireMock.post(urlEqualTo("/api/v1/auth/reset-password/request"))
                        .withRequestBody(equalToJson("{\"email\":\"a@b.com\"}"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"token\":\"my-token\"}")));

        given()
                .header("Authorization", "Bearer my-token")
                .contentType("application/json")
                .body("{\"email\":\"a@b.com\"}")
                .when()
                .post("/api/v1/auth/reset-password/request")
                .then()
                .contentType("application/json")
                .statusCode(200);
    }

    @Test
    public void testConfirmResetPassword() {
        wireMockServer.stubFor(WireMock.post(urlEqualTo("/api/v1/auth/reset-password/confirm"))
                        .withRequestBody(equalToJson("{\"email\":\"a@b.com\",\"code\":\"111\",\"newPassword\":\"1234567\"}"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"token\":\"my-token\"}")));

        given()
                .header("Authorization", "Bearer my-token")
                .contentType("application/json")
                .body("{\"email\":\"a@b.com\",\"code\":\"111\",\"newPassword\":\"1234567\"}")
                .when()
                .post("/api/v1/auth/reset-password/confirm")
                .then()
                .contentType("application/json")
                .statusCode(200);
    }
}
