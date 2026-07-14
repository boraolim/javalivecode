package liverpool.codelive.products.client;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import liverpool.codelive.products.dto.InventoryDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class) // Test unitario puro, carga en milisegundos ⚡
@DisplayName("InventoryClient - Pure Unit Tests with Mockito")
class InventoryClientTest {

    private MockWebServer mockWebServer;
    private InventoryClient inventoryClient;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        // Creamos un WebClient.Builder real manualmente sin necesidad de Spring
        WebClient.Builder webClientBuilder = WebClient.builder();
        String baseUrl = mockWebServer.url("/").toString();

        // Instanciamos el cliente pasándole la URL dinámica de MockWebServer
        inventoryClient = new InventoryClient(webClientBuilder, baseUrl);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("getStock - Should return inventory details when API responds successfully")
    void shouldReturnInventoryWhenApiRespondsSuccessfully() throws Exception {
        // Arrange
        String productId = "PROD-123";
        String mockJsonResponse = "{\"productId\":\"PROD-123\",\"stock\":45}";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(mockJsonResponse));

        // Act
        CompletableFuture<InventoryDto> future = inventoryClient.getStock(productId);
        InventoryDto result = future.get();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getProductId()).isEqualTo(productId);
        assertThat(result.getStock()).isEqualTo(45);
    }

    @Test
    @DisplayName("getStock - Should return empty stock when API returns 404 Not Found")
    void shouldReturnEmptyStockWhenApiReturns404() throws Exception {
        // Arrange
        String productId = "PROD-404";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        // Act
        CompletableFuture<InventoryDto> future = inventoryClient.getStock(productId);
        InventoryDto result = future.get();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getProductId()).isEqualTo(productId);
        assertThat(result.getStock()).isNull();
    }

    @Test
    @DisplayName("getStock - Should throw Exception when API returns 500 (No Spring AOP for Automatic Fallback)")
    void shouldThrowExceptionWhenApiReturns500() {
        // Arrange
        String productId = "PROD-500";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        // Act & Assert
        // Como no hay Spring AOP levantado, el WebClient lanzará la excepción directamente al hacer .get()
        CompletableFuture<InventoryDto> future = inventoryClient.getStock(productId);
        
        assertThatThrownBy(future::get)
                .isInstanceOf(ExecutionException.class)
                .hasCauseInstanceOf(WebClientResponseException.InternalServerError.class);
    }

    @Test
    @DisplayName("fallbackInventory - Should log warning and return default InventoryDto")
    void shouldReturnDefaultInventoryDtoOnFallback() throws Exception {
        // Arrange
        String productId = "PROD-FALLBACK";
        Throwable exception = new RuntimeException("Simulated network issue");

        // Act (Invocamos directamente el comportamiento del fallback para asegurar su lógica)
        CompletableFuture<InventoryDto> future = inventoryClient.fallbackInventory(productId, exception);
        InventoryDto result = future.get();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getProductId()).isEqualTo(productId);
        assertThat(result.getStock()).isNull();
    }
}