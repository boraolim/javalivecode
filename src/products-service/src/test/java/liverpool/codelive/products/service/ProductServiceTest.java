package liverpool.codelive.products.service;

import java.util.List;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import liverpool.codelive.products.dto.InventoryDto;
import liverpool.codelive.products.entity.ProductEntity;
import liverpool.codelive.products.mapper.ProductMapper;
import liverpool.codelive.products.enums.InventoryStatus;
import liverpool.codelive.products.client.InventoryClient;
import liverpool.codelive.products.repository.ProductRepository;
import liverpool.codelive.products.external.SoapCalculatorClient;
import liverpool.codelive.products.response.EnrichedProductResponse;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService - Unit Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private InventoryClient inventoryClient;

    @Mock
    private SoapCalculatorClient soapCalculatorClient;

    @InjectMocks
    private ProductService productService;

    private ProductEntity mockProduct;
    private EnrichedProductResponse mockResponse;

    @BeforeEach
    void setUp() {
        mockProduct = ProductEntity.builder()
                .id("PROD-100")
                .name("Xbox Series X")
                .description("Next-gen console")
                .price(new BigDecimal("499.99"))
                .build();

        mockResponse = new EnrichedProductResponse();
        mockResponse.setId("PROD-100");
        mockResponse.setName("Xbox Series X");
        mockResponse.setDescription("Next-gen console");
        mockResponse.setPrice(new BigDecimal("499.99"));
    }

    @Test
    @DisplayName("search - Should return enriched products with IN_STOCK status when stock is greater than 0")
    void shouldReturnEnrichedProductsWithInStockStatus() {
        // Arrange
        String query = "Xbox";
        InventoryDto inventoryDto = new InventoryDto("PROD-100", 5);

        when(productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query))
                .thenReturn(List.of(mockProduct));
        when(inventoryClient.getStock("PROD-100"))
                .thenReturn(CompletableFuture.completedFuture(inventoryDto));
        when(productMapper.toEnrichedResponse(mockProduct))
                .thenReturn(mockResponse);

        // Act
        List<EnrichedProductResponse> result = productService.search(query);

        // Assert
        assertThat(result).hasSize(1);
        EnrichedProductResponse response = result.get(0);
        assertThat(response.getId()).isEqualTo("PROD-100");
        assertThat(response.getStock()).isEqualTo(5);
        assertThat(response.getInventoryStatus()).isEqualTo(InventoryStatus.IN_STOCK);
    }

    @Test
    @DisplayName("search - Should return enriched products with OUT_OF_STOCK status when stock is 0")
    void shouldReturnEnrichedProductsWithOutOfStockStatus() {
        // Arrange
        String query = "Xbox";
        InventoryDto inventoryDto = new InventoryDto("PROD-100", 0);

        when(productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query))
                .thenReturn(List.of(mockProduct));
        when(inventoryClient.getStock("PROD-100"))
                .thenReturn(CompletableFuture.completedFuture(inventoryDto));
        when(productMapper.toEnrichedResponse(mockProduct))
                .thenReturn(mockResponse);

        // Act
        List<EnrichedProductResponse> result = productService.search(query);

        // Assert
        assertThat(result).hasSize(1);
        EnrichedProductResponse response = result.get(0);
        assertThat(response.getStock()).isZero();
        assertThat(response.getInventoryStatus()).isEqualTo(InventoryStatus.OUT_OF_STOCK);
    }

    @Test
    @DisplayName("search - Should return enriched products with UNAVAILABLE status when inventory client fails")
    void shouldReturnEnrichedProductsWithUnavailableStatusWhenClientFails() {
        // Arrange
        String query = "Xbox";

        when(productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query))
                .thenReturn(List.of(mockProduct));
        // Simulamos un error asíncrono controlado del cliente HTTP/gRPC externo
        when(inventoryClient.getStock("PROD-100"))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Connection timed out")));
        when(productMapper.toEnrichedResponse(mockProduct))
                .thenReturn(mockResponse);

        // Act
        List<EnrichedProductResponse> result = productService.search(query);

        // Assert
        assertThat(result).hasSize(1);
        EnrichedProductResponse response = result.get(0);
        assertThat(response.getStock()).isNull();
        assertThat(response.getInventoryStatus()).isEqualTo(InventoryStatus.UNAVAILABLE);
    }

    @Test
    @DisplayName("search - Should return enriched products with UNAVAILABLE status when inventory response is null")
    void shouldReturnEnrichedProductsWithUnavailableStatusWhenResponseIsNull() {
        // Arrange
        String query = "Xbox";

        when(productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query))
                .thenReturn(List.of(mockProduct));
        when(inventoryClient.getStock("PROD-100"))
                .thenReturn(CompletableFuture.completedFuture(null));
        when(productMapper.toEnrichedResponse(mockProduct))
                .thenReturn(mockResponse);

        // Act
        List<EnrichedProductResponse> result = productService.search(query);

        // Assert
        assertThat(result).hasSize(1);
        EnrichedProductResponse response = result.get(0);
        assertThat(response.getStock()).isNull();
        assertThat(response.getInventoryStatus()).isEqualTo(InventoryStatus.UNAVAILABLE);
    }

    @Test
    @DisplayName("search - Should return empty list when no products match the query")
    void shouldReturnEmptyListWhenNoProductsMatch() {
        // Arrange
        String query = "NonExistent";
        when(productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query))
                .thenReturn(Collections.emptyList());

        // Act
        List<EnrichedProductResponse> result = productService.search(query);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void getAddValue_ShouldReturnExpectedSum_WhenSoapClientSucceeds() {
        // Arrange
        int stockA = 40;
        int stockB = 17;
        int expectedSum = 57;
        
        // Simulamos que el cliente asíncrono responde exitosamente
        when(soapCalculatorClient.ejecutarSuma(stockA, stockB))
                .thenReturn(CompletableFuture.completedFuture(expectedSum));

        // Act
        int actualResult = productService.getAddValue(stockA, stockB);

        // Assert
        assertEquals(expectedSum, actualResult, "Should return the correct sum from the SOAP web service");
        verify(soapCalculatorClient).ejecutarSuma(stockA, stockB);
    }

    @Test
    void getAddValue_ShouldReturnFallbackSum_WhenSoapClientTriggersFallback() {
        // Arrange
        int stockA = 10;
        int stockB = 20;
        int fallbackSum = 30; // Resilience4j ejecuta la contingencia local a + b
        
        // Simulamos que ante una falla de red, el método fallback del cliente ya resolvió el problema
        when(soapCalculatorClient.ejecutarSuma(stockA, stockB))
                .thenReturn(CompletableFuture.completedFuture(fallbackSum));

        // Act
        int actualResult = productService.getAddValue(stockA, stockB);

        // Assert
        assertEquals(fallbackSum, actualResult, "ProductService should seamlessly receive the fallback calculation from the client");
        verify(soapCalculatorClient).ejecutarSuma(stockA, stockB);
    }

    @Test
    void getAddValue_ShouldPropagateException_WhenFutureFailsWithoutFallback() {
        // Arrange
        int stockA = 5;
        int stockB = 5;
        CompletableFuture<Integer> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Fatal SOAP infrastructure error"));

        when(soapCalculatorClient.ejecutarSuma(stockA, stockB)).thenReturn(failedFuture);

        // Act & Assert
        // Validamos que si ocurriera un error catastrófico no controlado, .join() propaga un CompletionException
        assertThrows(CompletionException.class, () -> {
            productService.getAddValue(stockA, stockB);
        }, "Should propagate CompletionException when the internal future completes exceptionally");
        
        verify(soapCalculatorClient).ejecutarSuma(stockA, stockB);
    }
}