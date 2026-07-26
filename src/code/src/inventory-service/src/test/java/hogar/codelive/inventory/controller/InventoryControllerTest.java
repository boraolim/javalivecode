package hogar.codelive.inventory.controller;

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

import hogar.codelive.inventory.service.InventoryService;
import hogar.codelive.inventory.response.InventoryResponse;
import hogar.codelive.inventory.constants.AppTestConstants;
import hogar.codelive.inventory.request.InventoryExistentRequest;
import hogar.codelive.inventory.request.InventoryNewProductRequest;
import hogar.codelive.inventory.exception.InventoryNotFoundException;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
@DisplayName("InventoryController - Pruebas unitarias")
class InventoryControllerTest {

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private InventoryController inventoryController;

    private InventoryNewProductRequest defaultNewProductRequest;
    private InventoryExistentRequest defaultExistentRequest;

    @BeforeEach
    void setUp() {
        defaultNewProductRequest = new InventoryNewProductRequest();
        defaultNewProductRequest.setIdProduct(AppTestConstants.PRODUCT_SECOND_ID);
        defaultNewProductRequest.setProductStock(40);

        defaultExistentRequest = new InventoryExistentRequest();
        defaultExistentRequest.setProductStock(60);
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
}