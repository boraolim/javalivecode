package hogar.codelive.products.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.persistence.EntityNotFoundException;

import hogar.codelive.products.service.ProductService;
import hogar.codelive.products.request.ProductNewRequest;
import hogar.codelive.products.request.ProductExistentRequest;
import hogar.codelive.products.response.EnrichedProductResponse;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.eq;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
@DisplayName("productController - Pruebas unitarias")
class ProControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private ProductNewRequest defaultNewRequest;
    private ProductExistentRequest defaultExistentRequest;

    @BeforeEach
    void setUp() {
        defaultNewRequest = new ProductNewRequest();
        defaultNewRequest.setProductId("EXT-001");
        defaultNewRequest.setNameProduct("Laptop Lenovo");

        defaultExistentRequest = new ProductExistentRequest();
        defaultExistentRequest.setNameProduct("Laptop Lenovo Actualizada");
    }

    @Test
    @DisplayName("search - EXITO: retorna 200 OK con la lista de productos enriquecidos")
    void search_conQueryValida_debeRetornar200ConResponse() {
        EnrichedProductResponse responseDto = new EnrichedProductResponse();
        responseDto.setId("EXT-001");
        responseDto.setName("Laptop Lenovo");

        when(productService.search("laptop"))
                .thenReturn(CompletableFuture.completedFuture(List.of(responseDto)));

        ResponseEntity<List<EnrichedProductResponse>> response =
                productController.search("laptop").join();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getId()).isEqualTo("EXT-001");
        verify(productService, times(1)).search("laptop");
    }

    @Test
    @DisplayName("getProduct - EXITO: retorna 200 OK con el producto enriquecido")
    void getProduct_conIdExistente_debeRetornar200ConResponse() {
        EnrichedProductResponse responseDto = new EnrichedProductResponse();
        responseDto.setId("EXT-001");

        when(productService.getProductId("EXT-001"))
                .thenReturn(CompletableFuture.completedFuture(responseDto));

        ResponseEntity<EnrichedProductResponse> response =
                productController.getProduct("EXT-001").join();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(responseDto);
        verify(productService, times(1)).getProductId("EXT-001");
    }

    @Test
    @DisplayName("getProduct - ERROR: propaga EntityNotFoundException cuando el producto no existe")
    void getProduct_conIdInexistente_debePropagarExcepcion() {
        CompletableFuture<EnrichedProductResponse> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new EntityNotFoundException("No existe un producto con id: EXT-999"));

        when(productService.getProductId("EXT-999")).thenReturn(failedFuture);

        CompletableFuture<ResponseEntity<EnrichedProductResponse>> result =
                productController.getProduct("EXT-999");

        assertThatThrownBy(result::join)
                .isInstanceOf(CompletionException.class)
                .hasCauseInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("addNewProduct - EXITO: retorna 201 CREATED con el producto creado")
    void addNewProduct_conRequestValido_debeRetornar201ConResponse() {
        EnrichedProductResponse responseDto = new EnrichedProductResponse();
        responseDto.setId("EXT-001");

        when(productService.addNewProductAsync(defaultNewRequest))
                .thenReturn(CompletableFuture.completedFuture(responseDto));

        ResponseEntity<EnrichedProductResponse> response =
                productController.addNewProduct(defaultNewRequest).join();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(responseDto);
    }

    @Test
    @DisplayName("addNewProduct - ERROR: propaga IllegalArgumentException del servicio")
    void addNewProduct_conRequestInvalido_debePropagarExcepcion() {
        CompletableFuture<EnrichedProductResponse> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new IllegalArgumentException("El producto no puede ser nulo"));

        when(productService.addNewProductAsync(defaultNewRequest)).thenReturn(failedFuture);

        CompletableFuture<ResponseEntity<EnrichedProductResponse>> result =
                productController.addNewProduct(defaultNewRequest);

        assertThatThrownBy(result::join)
                .isInstanceOf(CompletionException.class)
                .hasCauseInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("updateStockProduct - EXITO: retorna 204 NO_CONTENT al actualizar")
    void updateStockProduct_conRequestValido_debeRetornar204() {
        EnrichedProductResponse responseDto = new EnrichedProductResponse();

        when(productService.updateProductAsync(eq("EXT-001"), eq(defaultExistentRequest)))
                .thenReturn(CompletableFuture.completedFuture(responseDto));

        ResponseEntity<Void> response =
                productController.updateStockProduct("EXT-001", defaultExistentRequest).join();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("updateStockProduct - ERROR: propaga EntityNotFoundException si no existe el producto")
    void updateStockProduct_conProductoInexistente_debePropagarExcepcion() {
        CompletableFuture<EnrichedProductResponse> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new EntityNotFoundException("No existe el inventario para el producto: EXT-999"));

        when(productService.updateProductAsync(eq("EXT-999"), eq(defaultExistentRequest)))
                .thenReturn(failedFuture);

        CompletableFuture<ResponseEntity<Void>> result =
                productController.updateStockProduct("EXT-999", defaultExistentRequest);

        assertThatThrownBy(result::join)
                .isInstanceOf(CompletionException.class)
                .hasCauseInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("deleteStockProduct - EXITO: retorna 204 NO_CONTENT al eliminar")
    void deleteStockProduct_conProductoExistente_debeRetornar204() {
        when(productService.deleteProductAsync("EXT-001"))
                .thenReturn(CompletableFuture.completedFuture(null));

        ResponseEntity<Void> response =
                productController.deleteStockProduct("EXT-001").join();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("deleteStockProduct - ERROR: propaga EntityNotFoundException si no existe el producto")
    void deleteStockProduct_conProductoInexistente_debePropagarExcepcion() {
        CompletableFuture<Void> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new EntityNotFoundException("No existe el inventario para el producto: EXT-999"));

        when(productService.deleteProductAsync("EXT-999")).thenReturn(failedFuture);

        CompletableFuture<ResponseEntity<Void>> result =
                productController.deleteStockProduct("EXT-999");

        assertThatThrownBy(result::join)
                .isInstanceOf(CompletionException.class)
                .hasCauseInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("searchbatch - EXITO: retorna 200 OK con la lista de productos enriquecidos por lote")
    void searchbatch_conQueryValida_debeRetornar200ConResponse() {
        EnrichedProductResponse responseDto = new EnrichedProductResponse();
        responseDto.setId("EXT-001");
        responseDto.setName("Laptop Lenovo");

        when(productService.searchBatch("laptop"))
                .thenReturn(CompletableFuture.completedFuture(List.of(responseDto)));

        ResponseEntity<List<EnrichedProductResponse>> response =
                productController.searchbatch("laptop").join();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getId()).isEqualTo("EXT-001");
        verify(productService, times(1)).searchBatch("laptop");
    }
}