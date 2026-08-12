package hogar.codelive.inventory.controller;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import jakarta.persistence.EntityNotFoundException;

import hogar.codelive.inventory.service.InventoryService;
import hogar.codelive.inventory.response.InventoryResponse;
import hogar.codelive.inventory.constants.AppTestConstants;
import hogar.codelive.inventory.request.InventoryBatchRequest;
import hogar.codelive.inventory.request.InventoryExistentRequest;
import hogar.codelive.inventory.middleware.HttpLoggingInterceptor;
import hogar.codelive.inventory.request.InventoryNewProductRequest;
import hogar.codelive.inventory.exception.GlobalExceptionHandler;
import hogar.codelive.inventory.exception.InventoryNotFoundException;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;

@ExtendWith(MockitoExtension.class)
@DisplayName("InventoryController - Pruebas unitarias")
class InventoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebTestClient webTestClient;

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private InventoryController inventoryController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private InventoryNewProductRequest defaultNewProductRequest;
    private InventoryExistentRequest defaultExistentRequest;


    @BeforeEach
    void setUp() {
        defaultNewProductRequest = new InventoryNewProductRequest();
        defaultNewProductRequest.setIdProduct(AppTestConstants.PRODUCT_SECOND_ID);
        defaultNewProductRequest.setProductStock(40);

        defaultExistentRequest = new InventoryExistentRequest();
        defaultExistentRequest.setProductStock(60);

        this.mockMvc = MockMvcBuilders.standaloneSetup(inventoryController)
            .addInterceptors(new HttpLoggingInterceptor())
            .build();

        this.webTestClient = MockMvcWebTestClient
            .bindToController(inventoryController)
            .controllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    @Test
    @DisplayName("getStock - EXITO: retorna 200 OK con el response del servicio")
    void getStock_conProductoExistente_debeRetornar200ConResponse() {
        InventoryResponse expected = new InventoryResponse(AppTestConstants.PRODUCT_FIRST_ID, 30);

        when(inventoryService.getStockByProductIdAsync(AppTestConstants.PRODUCT_FIRST_ID))
                .thenReturn(CompletableFuture.completedFuture(expected));

        ResponseEntity<InventoryResponse> response =
                inventoryController.getStock(AppTestConstants.PRODUCT_FIRST_ID).join();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
        verify(inventoryService, times(1)).getStockByProductIdAsync(AppTestConstants.PRODUCT_FIRST_ID);
    }

    @Test
    @DisplayName("getStock - ERROR: propaga la excepcion del servicio sin envolverla en 200")
    void getStock_conProductoInexistente_debePropagarExcepcion() {
        CompletableFuture<InventoryResponse> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(
                new InventoryNotFoundException("El identificador " + AppTestConstants.PRODUCT_LAST_ID + " no existe"));

        when(inventoryService.getStockByProductIdAsync(AppTestConstants.PRODUCT_LAST_ID)).thenReturn(failedFuture);

        CompletableFuture<ResponseEntity<InventoryResponse>> result =
                inventoryController.getStock(AppTestConstants.PRODUCT_LAST_ID);

        assertThatThrownBy(result::join)
                .isInstanceOf(CompletionException.class)
                .hasCauseInstanceOf(InventoryNotFoundException.class);
    }

    @Test
    @DisplayName("addNewProduct - EXITO: retorna 201 CREATED con el response del servicio")
    void addNewProduct_conRequestValido_debeRetornar201ConResponse() {
        InventoryResponse expected = new InventoryResponse(AppTestConstants.PRODUCT_SECOND_ID, 40);

        when(inventoryService.addNewInventoryProductAsync(defaultNewProductRequest))
                .thenReturn(CompletableFuture.completedFuture(expected));

        ResponseEntity<InventoryResponse> response =
                inventoryController.addNewProduct(defaultNewProductRequest).join();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    @DisplayName("addNewProduct - ERROR: propaga IllegalArgumentException del servicio")
    void addNewProduct_conRequestInvalido_debePropagarExcepcion() {
        CompletableFuture<InventoryResponse> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(
                new IllegalArgumentException(AppTestConstants.MSG_ERR_PRODUCTO_NO_EXIST));

        when(inventoryService.addNewInventoryProductAsync(defaultNewProductRequest)).thenReturn(failedFuture);

        CompletableFuture<ResponseEntity<InventoryResponse>> result =
                inventoryController.addNewProduct(defaultNewProductRequest);

        assertThatThrownBy(result::join)
                .isInstanceOf(CompletionException.class)
                .hasCauseInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("updateStockProduct - EXITO: retorna 204 NO_CONTENT")
    void updateStockProduct_conRequestValido_debeRetornar204() {

        InventoryResponse serviceResult = new InventoryResponse(AppTestConstants.PRODUCT_FOURTH_ID, 60);

        when(inventoryService.updateInventoryProductAsync(AppTestConstants.PRODUCT_FOURTH_ID, defaultExistentRequest))
                .thenReturn(CompletableFuture.completedFuture(serviceResult));

        ResponseEntity<Void> response =
                inventoryController.updateStockProduct(AppTestConstants.PRODUCT_FOURTH_ID, defaultExistentRequest).join();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("updateStockProduct - ERROR: propaga EntityNotFoundException "
            + "(antes de la correccion, este caso incorrectamente devolvia 204)")
    void updateStockProduct_conProductoInexistente_debePropagarExcepcion() {
        CompletableFuture<InventoryResponse> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(
                new EntityNotFoundException("No existe un producto con id: " + AppTestConstants.PRODUCT_FIFTH_ID));

        when(inventoryService.updateInventoryProductAsync(AppTestConstants.PRODUCT_FIFTH_ID, defaultExistentRequest))
                .thenReturn(failedFuture);

        CompletableFuture<ResponseEntity<Void>> result =
                inventoryController.updateStockProduct(AppTestConstants.PRODUCT_FIFTH_ID, defaultExistentRequest);

        assertThatThrownBy(result::join)
                .isInstanceOf(CompletionException.class)
                .hasCauseInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("deleteStockProduct - EXITO: retorna 204 NO_CONTENT")
    void deleteStockProduct_conProductoExistente_debeRetornar204() {

        when(inventoryService.deleteProductAsync(AppTestConstants.PRODUCT_SIXTH_ID))
                .thenReturn(CompletableFuture.completedFuture(null));

        ResponseEntity<Void> response =
                inventoryController.deleteStockProduct(AppTestConstants.PRODUCT_SIXTH_ID).join();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("deleteStockProduct - ERROR: propaga EntityNotFoundException "
            + "(antes de la correccion, este caso incorrectamente devolvia 204)")
    void deleteStockProduct_conProductoInexistente_debePropagarExcepcion() {
        CompletableFuture<Void> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(
                new EntityNotFoundException("No existe un producto con id: " + AppTestConstants.PRODUCT_SEVENTH_ID));

        when(inventoryService.deleteProductAsync(AppTestConstants.PRODUCT_SEVENTH_ID)).thenReturn(failedFuture);

        CompletableFuture<ResponseEntity<Void>> result =
                inventoryController.deleteStockProduct(AppTestConstants.PRODUCT_SEVENTH_ID);

        assertThatThrownBy(result::join)
                .isInstanceOf(CompletionException.class)
                .hasCauseInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("EXITO: Debería retornar 200 OK con la lista de inventarios de forma asíncrona")
    void getStockByProducts_shouldReturnOkWithResponses() throws Exception {
        // Arrange
        List<String> productIds = List.of(AppTestConstants.PRODUCT_SECOND_ID, AppTestConstants.PRODUCT_THIRD_ID);
        InventoryBatchRequest request = new InventoryBatchRequest(productIds);

        List<InventoryResponse> responses = List.of(new InventoryResponse(AppTestConstants.PRODUCT_SECOND_ID, 50),
                                                    new InventoryResponse(AppTestConstants.PRODUCT_THIRD_ID, 20));

        when(inventoryService.getStockByProductIdsAsync(any(InventoryBatchRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(responses));

        // Act & Assert
        // Nota: Como el controlador retorna un CompletableFuture, usamos mvc.perform(...)
        // y Spring se encarga de manejar el hilo asíncrono automáticamente.
        var mvcResult = mockMvc.perform(post("/api/v1/inventory/batch") // Ajusta la ruta base de tu controlador si es diferente
                .accept(MediaType.APPLICATION_JSON)
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request))))
                .andExpect(request().asyncStarted())
                .andReturn();

        // Para endpoints asíncronos en MockMvc, se suele esperar al resultado con asyncDispatch
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].productId").value(AppTestConstants.PRODUCT_SECOND_ID))
                .andExpect(jsonPath("$[0].stock").value(50))
                .andExpect(jsonPath("$[1].productId").value(AppTestConstants.PRODUCT_THIRD_ID))
                .andExpect(jsonPath("$[1].stock").value(20));

        // Verify: Comprobamos que el servicio fue llamado exactamente una vez
        verify(inventoryService, times(1)).getStockByProductIdsAsync(any(InventoryBatchRequest.class));                
    }

    @Test
    @DisplayName("EXITO: Debería retornar 200 OK con la lista de inventarios de forma asíncrona")
    void getStockByProductsWb_shouldReturnOkWithResponses() {

        // Arrange
        List<String> productIds = List.of(AppTestConstants.PRODUCT_SECOND_ID,
                                          AppTestConstants.PRODUCT_THIRD_ID);

        InventoryBatchRequest request = new InventoryBatchRequest(productIds);

        List<InventoryResponse> responses = List.of(new InventoryResponse(AppTestConstants.PRODUCT_SECOND_ID, 50),
                                                    new InventoryResponse(AppTestConstants.PRODUCT_THIRD_ID, 20));

        when(inventoryService.getStockByProductIdsAsync(any(InventoryBatchRequest.class)))
            .thenReturn(CompletableFuture.completedFuture(responses));

        // Act & Assert
        webTestClient.post()
            .uri("/api/v1/inventory/batch")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.length()").isEqualTo(2)
            .jsonPath("$[0].productId").isEqualTo(AppTestConstants.PRODUCT_SECOND_ID)
            .jsonPath("$[0].stock").isEqualTo(50)
            .jsonPath("$[1].productId").isEqualTo(AppTestConstants.PRODUCT_THIRD_ID)
            .jsonPath("$[1].stock").isEqualTo(20);

        verify(inventoryService).getStockByProductIdsAsync(any(InventoryBatchRequest.class));
    }

    @Test
    @DisplayName("Éxito - Debería retornar lista vacía cuando no se encuentran productos")
    void shouldReturnEmptyListWhenNoProductsFound() throws Exception {
        // Arrange
        List<String> productIds = List.of("UNKNOWN-999");
        InventoryBatchRequest request = new InventoryBatchRequest(productIds);

        List<InventoryResponse> responses = List.of();

        when(inventoryService.getStockByProductIdsAsync(any(InventoryBatchRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(responses));

        // Act
        var mvcResult = mockMvc.perform(post("/api/v1/inventory/batch")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request))))
                .andExpect(request().asyncStarted())
                .andReturn();

        // Para endpoints asíncronos en MockMvc, se suele esperar al resultado con asyncDispatch                
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(0)));

        // Verify: Comprobamos que el servicio fue llamado exactamente una vez                
        verify(inventoryService, times(1)).getStockByProductIdsAsync(any(InventoryBatchRequest.class)); 
    }
}