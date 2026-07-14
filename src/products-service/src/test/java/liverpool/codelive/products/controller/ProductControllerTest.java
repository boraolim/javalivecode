package liverpool.codelive.products.controller;

import java.util.List;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.InjectMocks;

import org.springframework.http.MediaType;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import liverpool.codelive.products.dto.InventoryDto;
import liverpool.codelive.products.entity.ProductEntity;
import liverpool.codelive.products.mapper.ProductMapper;
import liverpool.codelive.products.enums.InventoryStatus;
import liverpool.codelive.products.client.InventoryClient;
import liverpool.codelive.products.service.ProductService;
import liverpool.codelive.products.request.CalculationRequest;
import liverpool.codelive.products.repository.ProductRepository;
import liverpool.codelive.products.external.SoapCalculatorClient;
import liverpool.codelive.products.response.EnrichedProductResponse;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doReturn;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductController - Standalone Integration Tests")
class ProductControllerTest {
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryClient inventoryClient;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private SoapCalculatorClient soapCalculatorClient;
    
    @InjectMocks
    private ProductService productService;

    private ProductEntity mockEntity;
    private InventoryDto mockInventoryDto;
    private EnrichedProductResponse mockResponse;

    @BeforeEach
    void setUp() {
        // Inicializamos MockMvc asociándolo al controlador que consume el productService real
        ProductController productController = new ProductController(productService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(productController).build();

        // Entidad simulada de la base de datos
        mockEntity = ProductEntity.builder()
                .id("PROD-100")
                .name("Nintendo Switch")
                .description("Portable console")
                .price(new BigDecimal("299.99"))
                .build();

        // Respuesta simulada del servicio asíncrono de inventario
        mockInventoryDto = new InventoryDto("PROD-100", 10);

        // Estructura base enriquecida (sin stock asignado aún para que el servicio lo inyecte dinámicamente)
        mockResponse = EnrichedProductResponse.builder()
                .id("PROD-100")
                .name("Nintendo Switch")
                .description("Portable console")
                .price(new BigDecimal("299.99"))
                .inventoryStatus(InventoryStatus.IN_STOCK)
                .build();
    }

    @Test
    @DisplayName("search - Should return 200 OK and a list of enriched products when match is found")
    void shouldReturnOkAndProductListWhenMatchIsFound() throws Exception {
        // Arrange
        String query = "Nintendo";
        
        // Mockeamos el repositorio para que devuelva la entidad simulada
        when(productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query))
                .thenReturn(List.of(mockEntity));

        // Mockeamos el mapper con tu método real "toEnrichedResponse"
        when(productMapper.toEnrichedResponse(any(ProductEntity.class)))
                .thenReturn(mockResponse);

        // Mockeamos la llamada asíncrona al cliente de inventarios
        when(inventoryClient.getStock(any()))
                .thenReturn(CompletableFuture.completedFuture(mockInventoryDto));

        // Act & Assert
        mockMvc.perform(get("/api/v1/products/search")
                        .param("query", query)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value("PROD-100"))
                .andExpect(jsonPath("$[0].name").value("Nintendo Switch"))
                .andExpect(jsonPath("$[0].description").value("Portable console"))
                .andExpect(jsonPath("$[0].price").value(299.99))
                .andExpect(jsonPath("$[0].stock").value(10))
                .andExpect(jsonPath("$[0].inventoryStatus").value("IN_STOCK"));
    }

    @Test
    @DisplayName("search - Should return 200 OK and empty list when no matches are found")
    void shouldReturnOkAndEmptyListWhenNoMatchesFound() throws Exception {
        // Arrange
        String query = "NonExistentProduct";
        
        when(productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/v1/products/search")
                        .param("query", query)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("search - Should return 400 Bad Request when 'query' request parameter is missing")
    void shouldReturnBadRequestWhenQueryParamIsMissing() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/products/search")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

        @Test
    void calculateStock_ShouldReturnOkAndTotalStock_WhenRequestBodyIsValid() throws Exception {
        // Arrange
        CalculationRequest request = new CalculationRequest();
        request.setValorA(45);
        request.setValorB(12);
        int expectedResult = 57;

        ProductService productServiceSpy = spy(productService);

        ProductController productController = new ProductController(productServiceSpy);
        this.mockMvc = MockMvcBuilders.standaloneSetup(productController).build();

        doReturn(expectedResult).when(productServiceSpy).getAddValue(request.getValorA(), request.getValorB());

        // Act & Assert
        mockMvc.perform(post("/api/v1/products/calculate-stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(expectedResult)));

        verify(productServiceSpy).getAddValue(request.getValorA(), request.getValorB());
    }

    @Test
    void calculateStock_ShouldReturnBadRequest_WhenRequestBodyIsMissing() throws Exception {
        // Act & Assert
        // Enviamos un POST con el body completamente vacío para disparar el (required = true)
        mockMvc.perform(post("/api/v1/products/calculate-stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest()); // Valida HTTP 400 Bad Request automático de Spring
    }
}