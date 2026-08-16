package com.lms.gateway;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiGatewayApplicationTests {

    @LocalServerPort
    private int port;

    private WebTestClient webClient;

    @org.junit.jupiter.api.BeforeEach
    void setupWebClient() {
        this.webClient = WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    private static WireMockServer wireMockServer;

    @BeforeAll
    static void startWireMock() {
        // Khởi tạo WireMock server chạy trên một port ngẫu nhiên
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
    }

    @AfterAll
    static void stopWireMock() {
        wireMockServer.stop();
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        // Ghi đè toàn bộ URL của các downstream service trỏ về WireMock thay vì port thật
        String wireMockUrl = "http://localhost:" + wireMockServer.port();
        registry.add("COURSE_SERVICE_URL", () -> wireMockUrl);
        registry.add("ENROLLMENT_SERVICE_URL", () -> wireMockUrl);
        registry.add("USER_SERVICE_URL", () -> wireMockUrl);
        registry.add("APP_CORS_ALLOWED_ORIGINS", () -> "http://localhost:3000");
    }

    // =====================================================================
    // TEST 1 — Gateway starts
    // =====================================================================
    @Test
    void test1_contextLoads() {
        // Chỉ cần context khởi tạo thành công (không crash) là PASS
    }

    // =====================================================================
    // TEST 2 — Course routing
    // =====================================================================
    @Test
    void test2_courseRouting() {
        stubFor(get(urlEqualTo("/api/categories/root"))
                .willReturn(aResponse().withStatus(200).withBody("Course Service: Root Categories")));

        webClient.get().uri("/api/categories/root")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Course Service: Root Categories");
    }

    // =====================================================================
    // TEST 3 — Enrollment routing
    // =====================================================================
    @Test
    void test3_enrollmentRouting() {
        stubFor(get(urlEqualTo("/api/certificates/me"))
                .willReturn(aResponse().withStatus(200).withBody("Enrollment Service: My Certificates")));

        webClient.get().uri("/api/certificates/me")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Enrollment Service: My Certificates");
    }

    // =====================================================================
    // TEST 4 — User routing
    // =====================================================================
    @Test
    void test4_userRouting() {
        stubFor(post(urlEqualTo("/api/users/sync"))
                .willReturn(aResponse().withStatus(200).withBody("User Service: Sync Success")));

        webClient.post().uri("/api/users/sync")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("User Service: Sync Success");
    }

    // =====================================================================
    // TEST 5 — Authorization header relay
    // =====================================================================
    @Test
    void test5_authorizationHeaderRelay() {
        stubFor(post(urlEqualTo("/api/users/sync"))
                .willReturn(aResponse().withStatus(200)));

        String fakeJwtToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.fake.token";

        webClient.post().uri("/api/users/sync")
                .header("Authorization", fakeJwtToken)
                .exchange()
                .expectStatus().isOk();

        // Xác minh rằng WireMock (đóng vai downstream service) đã nhận được request
        // với header Authorization hoàn toàn nguyên vẹn
        verify(postRequestedFor(urlEqualTo("/api/users/sync"))
                .withHeader("Authorization", equalTo(fakeJwtToken)));
    }

    // =====================================================================
    // TEST 6 — Internal API blocking (User Profile)
    // =====================================================================
    @Test
    void test6_internalApiBlocking_User() {
        webClient.get().uri("/api/internal/users/123/profile")
                .exchange()
                .expectStatus().isForbidden(); // Mong đợi HTTP 403 từ SecurityFilter

        // Xác minh rằng request không bao giờ lọt tới hệ thống downstream
        verify(0, getRequestedFor(urlEqualTo("/api/internal/users/123/profile")));
    }

    // =====================================================================
    // TEST 7 — Dev API blocking (Dev Certificates)
    // =====================================================================
    @Test
    void test7_internalApiBlocking_DevCertificates() {
        webClient.delete().uri("/api/internal/dev/certificates/enrollment/99")
                .exchange()
                .expectStatus().isForbidden(); // Mong đợi HTTP 403

        verify(0, deleteRequestedFor(urlEqualTo("/api/internal/dev/certificates/enrollment/99")));
    }

    // =====================================================================
    // TEST 8 — CORS
    // =====================================================================
    @Test
    void test8_corsOptionsRequest() {
        webClient.options().uri("/api/courses")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "GET")
                .header("Access-Control-Request-Headers", "Authorization,Content-Type")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Access-Control-Allow-Origin", "http://localhost:3000")
                .expectHeader().exists("Access-Control-Allow-Methods")
                .expectHeader().exists("Access-Control-Allow-Headers");
    }

    // =====================================================================
    // TEST 9 — Invalid route
    // =====================================================================
    @Test
    void test9_invalidRoute() {
        webClient.get().uri("/api/unknown-endpoint")
                .exchange()
                .expectStatus().isNotFound(); // HTTP 404 Not Found từ Gateway
    }
}