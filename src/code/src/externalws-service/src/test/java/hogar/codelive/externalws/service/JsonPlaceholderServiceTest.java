package hogar.codelive.externalws.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import hogar.codelive.externalws.response.PostDataResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("JsonPlaceholderService - Unit Tests")
class JsonPlaceholderServiceTest {
    private MockWebServer mockWebServer;
    private JsonPlaceholderService jsonPlaceholderService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = mockWebServer.url("/").toString();

        WebClient.Builder webClientBuilder = WebClient.builder();
        jsonPlaceholderService = new JsonPlaceholderService(webClientBuilder, baseUrl);
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("EXITO: Debería retornar la lista de posts correctamente de forma asíncrona")
    void getAllPosts_debeRetornarListaExitosaAsync() throws Exception {
        // Arrange
        PostDataResponse mockPost = new PostDataResponse();
        mockPost.setId(1);
        mockPost.setUserId(1);
        mockPost.setTitle("Test Title");
        mockPost.setBody("Test Body");

        List<PostDataResponse> expectedPosts = List.of(mockPost);
        String jsonResponse = objectMapper.writeValueAsString(expectedPosts);

        // Configuramos el servidor mock para responder con 200 OK y el JSON
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(jsonResponse));

        // Act
        CompletableFuture<List<PostDataResponse>> futureResult = jsonPlaceholderService.getAllPosts();
        List<PostDataResponse> actualPosts = futureResult.get(); // Esperamos el resultado del CompletableFuture

        // Assert
        assertNotNull(actualPosts);
        assertEquals(1, actualPosts.size());
        assertEquals(expectedPosts.get(0).getTitle(), actualPosts.get(0).getTitle());
    }

    @Test
    @DisplayName("ERROR: Debería propagar excepción cuando el servidor externo falla (500 Internal Server Error)")
    void getAllPosts_debeRetornarErrorCuandoServidorFallaAsync() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500));

        // Como el .block() lanza la excepción directamente de forma síncrona dentro del método @Async invocado,
        // esperamos la excepción de WebClientResponseException en lugar de ExecutionException.
        WebClientResponseException exception = 
            assertThrows(
                WebClientResponseException.InternalServerError.class, 
                () -> jsonPlaceholderService.getAllPosts()
            );

        assertNotNull(exception);
    }
}