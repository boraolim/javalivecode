package hogar.codelive.inventory.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.IntStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityNotFoundException;

import hogar.codelive.inventory.dto.InventoryDto;
import hogar.codelive.inventory.entity.InventoryEntity;
import hogar.codelive.inventory.mapper.InventoryMapper;
import hogar.codelive.inventory.constants.AppTestConstants;
import hogar.codelive.inventory.response.InventoryResponse;
import hogar.codelive.inventory.repository.InventoryRepository;
import hogar.codelive.inventory.request.InventoryBatchRequest;
import hogar.codelive.inventory.request.InventoryExistentRequest;
import hogar.codelive.inventory.request.InventoryNewProductRequest;
import hogar.codelive.inventory.exception.InventoryNotFoundException;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.doNothing;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
@DisplayName("InventoryServiceTests - Integration Tests")
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryMapper inventoryMapper;

    @InjectMocks
    private InventoryService inventoryService;

    private InventoryExistentRequest defaultExistentRequest;
    private InventoryNewProductRequest defaultNewInventoryRequest;

    @BeforeEach
    void setUp() {
        defaultNewInventoryRequest = new InventoryNewProductRequest();
        defaultNewInventoryRequest.setIdProduct(AppTestConstants.PRODUCT_ID_P100);
        defaultNewInventoryRequest.setProductStock(50);

        defaultExistentRequest = new InventoryExistentRequest();
        defaultExistentRequest.setProductStock(80);
    }

    @Test
    @DisplayName("EXITO: retorna InventoryResponse cuando el producto existe")
    void getStockByProductIdAsync_withExistentProduct_shouldReturnResponseResponse() {
        InventoryEntity entity = new InventoryEntity(AppTestConstants.PRODUCT_ID_P1, 25);
        InventoryDto dto = new InventoryDto(AppTestConstants.PRODUCT_ID_P1, 25);
 
        when(inventoryRepository.findById(AppTestConstants.PRODUCT_ID_P1)).thenReturn(Optional.of(entity));
        when(inventoryMapper.fromEntity(entity)).thenReturn(dto);
 
        InventoryResponse response = inventoryService.getStockByProductIdAsync(AppTestConstants.PRODUCT_ID_P1).join();
 
        assertThat(response).isNotNull();
        assertThat(response.getProductId()).isEqualTo(AppTestConstants.PRODUCT_ID_P1);
        assertThat(response.getStock()).isEqualTo(25);
    }

    @Test
    @DisplayName("EXITO: invoca el repositorio y el mapper exactamente una vez, con los parametros correctos")
    void getStockByProductIdAsync_debeInvocarDependenciasUnaVezConParametrosCorrectos() {
        InventoryEntity entity = new InventoryEntity(AppTestConstants.PRODUCT_ID_P3, 5);
        InventoryDto dto = new InventoryDto(AppTestConstants.PRODUCT_ID_P3, 5);
 
        when(inventoryRepository.findById(AppTestConstants.PRODUCT_ID_P3)).thenReturn(Optional.of(entity));
        when(inventoryMapper.fromEntity(entity)).thenReturn(dto);
 
        inventoryService.getStockByProductIdAsync(AppTestConstants.PRODUCT_ID_P3).join();
 
        verify(inventoryRepository, times(1)).findById(AppTestConstants.PRODUCT_ID_P3);
        verify(inventoryMapper, times(1)).fromEntity(entity);
        verifyNoMoreInteractions(inventoryRepository, inventoryMapper);
    }

    @Test
    @DisplayName("EXITO: stock en cero se mapea correctamente (no se confunde con ausencia de valor)")
    void getStockByProductIdAsync_conStockCero_debeRetornarResponseConStockCero() {
        InventoryEntity entity = new InventoryEntity(AppTestConstants.PRODUCT_ID_P4, 0);
        InventoryDto dto = new InventoryDto(AppTestConstants.PRODUCT_ID_P4, 0);
 
        when(inventoryRepository.findById(AppTestConstants.PRODUCT_ID_P4)).thenReturn(Optional.of(entity));
        when(inventoryMapper.fromEntity(entity)).thenReturn(dto);
 
        InventoryResponse response = inventoryService.getStockByProductIdAsync(AppTestConstants.PRODUCT_ID_P4).join();
 
        assertThat(response.getStock()).isZero();
    }

    @Test
    @DisplayName("ERROR: lanza InventoryNotFoundException cuando el producto no existe en el repositorio")
    void getStockByProductIdAsync_conProductoInexistente_debeLanzarExcepcion() {
        when(inventoryRepository.findById(AppTestConstants.PRODUCT_NOT_EXISTENT_ID)).thenReturn(Optional.empty());
 
        CompletableFuture<InventoryResponse> future = inventoryService.getStockByProductIdAsync(AppTestConstants.PRODUCT_NOT_EXISTENT_ID);
 
        assertThatThrownBy(future::join)
                .isInstanceOf(CompletionException.class)
                .hasCauseInstanceOf(InventoryNotFoundException.class)
                .cause()
                .hasMessageContaining(AppTestConstants.PRODUCT_NOT_EXISTENT_ID);
    }

    @Test
    @DisplayName("ERROR: lanza InventoryNotFoundException cuando el mapper devuelve null")
    void getStockByProductIdAsync_conMapperQueDevuelveNull_debeLanzarExcepcion() {
        InventoryEntity entity = new InventoryEntity(AppTestConstants.PRODUCT_ID_P2, 10);
 
        when(inventoryRepository.findById(AppTestConstants.PRODUCT_ID_P2)).thenReturn(Optional.of(entity));
        when(inventoryMapper.fromEntity(entity)).thenReturn(null);
 
        CompletableFuture<InventoryResponse> future = inventoryService.getStockByProductIdAsync(AppTestConstants.PRODUCT_ID_P2);
 
        assertThatThrownBy(future::join)
                .isInstanceOf(CompletionException.class)
                .hasCauseInstanceOf(InventoryNotFoundException.class)
                .cause()
                .hasMessage(AppTestConstants.MSG_ERR_PRODUCTO_NOT_EXIST);
    }

    @Test
    @DisplayName("ERROR: no invoca el mapper si el producto no existe (corta el flujo antes)")
    void getStockByProductIdAsync_conProductoInexistente_noDebeInvocarMapper() {
         when(inventoryRepository.findById(AppTestConstants.PRODUCT_NOT_EXISTENT_ID)).thenReturn(Optional.empty());
 
        CompletableFuture<InventoryResponse> future = inventoryService.getStockByProductIdAsync(AppTestConstants.PRODUCT_NOT_EXISTENT_ID);
 
        assertThatThrownBy(future::join).isInstanceOf(CompletionException.class);
        verify(inventoryMapper, times(0)).fromEntity(any());
    }

    @ParameterizedTest(name = "[{index}] productId={0}, stock={1}")
    @MethodSource("provideDiezProductos")
    @DisplayName("EXITO: mapea correctamente 10 productos distintos")
    void getStockByProductIdAsync_conDiezProductosDistintos_debeMapearCadaUnoCorrectamente(String productId, int stock) {
 
        InventoryEntity entity = new InventoryEntity(productId, stock);
        InventoryDto dto = new InventoryDto(productId, stock);
 
        when(inventoryRepository.findById(productId)).thenReturn(Optional.of(entity));
        when(inventoryMapper.fromEntity(entity)).thenReturn(dto);
 
        InventoryResponse response = inventoryService.getStockByProductIdAsync(productId).join();
 
        assertThat(response.getProductId()).isEqualTo(productId);
        assertThat(response.getStock()).isEqualTo(stock);
    }

    @Test
    @DisplayName("EXITO: procesa 10 llamadas concurrentes sin mezclar resultados entre productos")
    void getStockByProductIdAsync_conDiezLlamadasConcurrentes_debeResolverCadaUnaCorrectamente() {
        List<String> productIds = IntStream.rangeClosed(1, AppTestConstants.TOTAL_PRODUCTOS)
                .mapToObj(i -> "CONC-" + i)
                .toList();
 
        productIds.forEach(id -> {
            int stock = Integer.parseInt(id.replace("CONC-", "")) * 10;
            InventoryEntity entity = new InventoryEntity(id, stock);
            InventoryDto dto = new InventoryDto(id, stock);
 
            when(inventoryRepository.findById(id)).thenReturn(Optional.of(entity));
            when(inventoryMapper.fromEntity(entity)).thenReturn(dto);
        });
 
        List<CompletableFuture<InventoryResponse>> futures = productIds.stream()
                .map(inventoryService::getStockByProductIdAsync)
                .toList();
 
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
 
        for (int i = 0; i < productIds.size(); i++) {
            InventoryResponse response = futures.get(i).join();
            String expectedId = productIds.get(i);
            int expectedStock = Integer.parseInt(expectedId.replace("CONC-", "")) * 10;
 
            assertThat(response.getProductId()).isEqualTo(expectedId);
            assertThat(response.getStock()).isEqualTo(expectedStock);
        }
 
        verify(inventoryRepository, times(AppTestConstants.TOTAL_PRODUCTOS)).findById(anyString());
    }

    @Test
    @DisplayName("EXITO: crea un nuevo producto de inventario correctamente")
    void addNewInventoryProductAsync_conRequestValido_debeRetornarResponse() {
        InventoryDto dtoFromRequest = new InventoryDto(AppTestConstants.PRODUCT_ID_P100, 50);
        InventoryEntity entity = new InventoryEntity(AppTestConstants.PRODUCT_ID_P100, 50);
        InventoryEntity savedEntity = new InventoryEntity(AppTestConstants.PRODUCT_ID_P100, 50);
        InventoryDto dtoFromEntity = new InventoryDto(AppTestConstants.PRODUCT_ID_P100, 50);

        when(inventoryMapper.toDto(defaultNewInventoryRequest)).thenReturn(dtoFromRequest);
        when(inventoryMapper.toEntity(dtoFromRequest)).thenReturn(entity);
        when(inventoryRepository.save(entity)).thenReturn(savedEntity);
        when(inventoryMapper.fromEntity(savedEntity)).thenReturn(dtoFromEntity);
        when(inventoryMapper.fromDto(dtoFromEntity)).thenReturn(new InventoryResponse(AppTestConstants.PRODUCT_ID_P100, 50));

        InventoryResponse response = inventoryService.addNewInventoryProductAsync(defaultNewInventoryRequest).join();

        assertThat(response.getProductId()).isEqualTo(AppTestConstants.PRODUCT_ID_P100);
        assertThat(response.getStock()).isEqualTo(50);
    }

    @Test
    @DisplayName("ERROR: lanza IllegalArgumentException cuando el request es null")
    void addNewInventoryProductAsync_conRequestNulo_debeLanzarExcepcion() {
        CompletableFuture<InventoryResponse> future = inventoryService.addNewInventoryProductAsync(null);

        assertThatThrownBy(future::join)
            .isInstanceOf(CompletionException.class)
            .hasCauseInstanceOf(IllegalArgumentException.class)
            .cause()
            .hasMessage(AppTestConstants.MSG_ERR_PRODUCTO_NO_EXIST);
    }

    @Test
    @DisplayName("EXITO: actualiza un producto existente correctamente")
    void updateInventoryProductAsync_conRequestValidoYProductoExistente_debeRetornarResponse() {
 
        InventoryEntity existingEntity = new InventoryEntity(AppTestConstants.PRODUCT_ID_P200, 50);
        InventoryDto dtoFromEntity = new InventoryDto(AppTestConstants.PRODUCT_ID_P200, 50);
        InventoryEntity entityToSave = new InventoryEntity(AppTestConstants.PRODUCT_ID_P200, 80);
        InventoryEntity savedEntity = new InventoryEntity(AppTestConstants.PRODUCT_ID_P200, 80);
        InventoryDto dtoFromSavedEntity = new InventoryDto(AppTestConstants.PRODUCT_ID_P200, 80);
 
        when(inventoryRepository.findById(AppTestConstants.PRODUCT_ID_P200)).thenReturn(Optional.of(existingEntity));
        when(inventoryMapper.fromEntity(existingEntity)).thenReturn(dtoFromEntity);
        when(inventoryMapper.toEntity(dtoFromEntity)).thenReturn(entityToSave);
        when(inventoryRepository.save(entityToSave)).thenReturn(savedEntity);
        when(inventoryMapper.fromEntity(savedEntity)).thenReturn(dtoFromSavedEntity);
        when(inventoryMapper.fromDto(dtoFromSavedEntity)).thenReturn(new InventoryResponse(AppTestConstants.PRODUCT_ID_P200, 80));
 
        InventoryResponse response = inventoryService.updateInventoryProductAsync(AppTestConstants.PRODUCT_ID_P200, defaultExistentRequest).join();
 
        assertThat(response.getProductId()).isEqualTo(AppTestConstants.PRODUCT_ID_P200);
        assertThat(response.getStock()).isEqualTo(80);
        verify(inventoryMapper, times(1)).updateEntity(defaultExistentRequest, dtoFromEntity);
        verify(inventoryRepository, times(1)).save(entityToSave);
    }
 
    @Test
    @DisplayName("ERROR: lanza IllegalArgumentException cuando el request es null")
    void updateInventoryProductAsync_conRequestNulo_debeLanzarExcepcion() {
        CompletableFuture<InventoryResponse> future =
                inventoryService.updateInventoryProductAsync(AppTestConstants.PRODUCT_ID_P201, null);
 
        assertThatThrownBy(future::join)
                .isInstanceOf(CompletionException.class)
                .hasCauseInstanceOf(IllegalArgumentException.class)
                .cause()
                .hasMessage(AppTestConstants.MSG_ERR_PRODUCTO_NO_EXIST);
 
        verify(inventoryRepository, times(0)).findById(anyString());
    }
 
    @Test
    @DisplayName("ERROR: lanza EntityNotFoundException cuando el producto no existe")
    void updateInventoryProductAsync_conProductoInexistente_debeLanzarExcepcion() {
        when(inventoryRepository.findById(AppTestConstants.PRODUCT_NOT_EXISTENT_ID)).thenReturn(Optional.empty());
 
        CompletableFuture<InventoryResponse> future =
                inventoryService.updateInventoryProductAsync(AppTestConstants.PRODUCT_NOT_EXISTENT_ID, defaultExistentRequest);
 
        assertThatThrownBy(future::join)
                .isInstanceOf(CompletionException.class)
                .hasCauseInstanceOf(EntityNotFoundException.class)
                .cause()
                .hasMessageContaining(AppTestConstants.PRODUCT_NOT_EXISTENT_ID);
 
        verify(inventoryRepository, times(0)).save(any());
    }
 
    @Test
    @DisplayName("ERROR: no invoca save() ni updateEntity() cuando el producto no existe")
    void updateInventoryProductAsync_conProductoInexistente_noDebeGuardarNiActualizar() {
        when(inventoryRepository.findById(AppTestConstants.PRODUCT_NOT_EXISTENT_ID)).thenReturn(Optional.empty());
 
        CompletableFuture<InventoryResponse> future =
                inventoryService.updateInventoryProductAsync(AppTestConstants.PRODUCT_NOT_EXISTENT_ID, defaultExistentRequest);
 
        assertThatThrownBy(future::join).isInstanceOf(CompletionException.class);
 
        verify(inventoryMapper, times(0)).updateEntity(any(), any());
        verify(inventoryRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("EXITO: elimina un producto existente sin lanzar excepcion")
    void deleteProductAsync_conProductoExistente_debeEliminarSinExcepcion() {
        InventoryEntity entity = new InventoryEntity(AppTestConstants.PRODUCT_ID_P300, 15);
 
        when(inventoryRepository.findById(AppTestConstants.PRODUCT_ID_P300)).thenReturn(Optional.of(entity));
        doNothing().when(inventoryRepository).delete(entity);
 
        CompletableFuture<Void> future = inventoryService.deleteProductAsync(AppTestConstants.PRODUCT_ID_P300);
 
        future.join();
 
        verify(inventoryRepository, times(1)).delete(entity);
    }
 
    @Test
    @DisplayName("ERROR: lanza EntityNotFoundException cuando el producto no existe")
    void deleteProductAsync_conProductoInexistente_debeLanzarExcepcion() {
        when(inventoryRepository.findById(AppTestConstants.PRODUCT_NOT_EXISTENT_ID)).thenReturn(Optional.empty());
 
        CompletableFuture<Void> future = inventoryService.deleteProductAsync(AppTestConstants.PRODUCT_NOT_EXISTENT_ID);
 
        assertThatThrownBy(future::join)
                .isInstanceOf(CompletionException.class)
                .hasCauseInstanceOf(EntityNotFoundException.class)
                .cause()
                .hasMessageContaining(AppTestConstants.PRODUCT_NOT_EXISTENT_ID);
 
        verify(inventoryRepository, times(0)).delete(any());
    }
 
    @Test
    @DisplayName("ERROR: lanza EntityNotFoundException cuando el productId es null")
    void deleteProductAsync_conProductIdNulo_debeLanzarExcepcion() {
        CompletableFuture<Void> future = inventoryService.deleteProductAsync(null);
 
        assertThatThrownBy(future::join)
                .isInstanceOf(CompletionException.class)
                .hasCauseInstanceOf(EntityNotFoundException.class);
 
        verifyNoInteractions(inventoryRepository);
    }
 
    @Test
    @DisplayName("ERROR: si delete() del repositorio falla, la excepcion se propaga en el future")
    void deleteProductAsync_conFalloEnRepositorio_debePropagarExcepcion() {
        InventoryEntity entity = new InventoryEntity(AppTestConstants.PRODUCT_ID_P301, 15);
 
        when(inventoryRepository.findById(AppTestConstants.PRODUCT_ID_P301)).thenReturn(Optional.of(entity));
        doThrow(new RuntimeException(AppTestConstants.MSG_ERR_CONEXION_BD))
                .when(inventoryRepository).delete(entity);
 
        CompletableFuture<Void> future = inventoryService.deleteProductAsync(AppTestConstants.PRODUCT_ID_P301);
 
        assertThatThrownBy(future::join)
                .isInstanceOf(CompletionException.class)
                .hasCauseInstanceOf(RuntimeException.class)
                .cause()
                .hasMessage(AppTestConstants.MSG_ERR_CONEXION_BD);
    }

    @Test
    @DisplayName("Éxito - Debería retornar lista de inventario mapeada de forma asíncrona")
    void shouldReturnStockResponsesSuccessfully() throws Exception {
        // Arrange
        List<String> productIds = List.of("EXT-002", "EXT-003");
        InventoryBatchRequest request = new InventoryBatchRequest(productIds);

        InventoryEntity entity1 = InventoryEntity.builder().productId(AppTestConstants.PRODUCT_FIRST_ID).stock(50).build();
        InventoryEntity entity2 = InventoryEntity.builder().productId(AppTestConstants.PRODUCT_SECOND_ID).stock(20).build();

        InventoryDto dto1 = new InventoryDto(AppTestConstants.PRODUCT_FIRST_ID, 50);
        InventoryDto dto2 = new InventoryDto(AppTestConstants.PRODUCT_SECOND_ID, 20);

        when(inventoryRepository.findAllById(productIds)).thenReturn(List.of(entity1, entity2));

        when(inventoryMapper.fromEntity(entity1)).thenReturn(dto1);
        when(inventoryMapper.fromEntity(entity2)).thenReturn(dto2);

        // Act
        CompletableFuture<List<InventoryResponse>> futureResult = inventoryService.getStockByProductIdsAsync(request);

        // Assert (Esperamos a que el CompletableFuture termine su ejecución asíncrona)
        List<InventoryResponse> responses = futureResult.get();

        assertThat(responses).isNotNull().hasSize(2);
        assertThat(responses.get(0).getProductId()).isEqualTo(AppTestConstants.PRODUCT_FIRST_ID);
        assertThat(responses.get(0).getStock()).isEqualTo(50);
        assertThat(responses.get(1).getProductId()).isEqualTo(AppTestConstants.PRODUCT_SECOND_ID);
        assertThat(responses.get(1).getStock()).isEqualTo(20);

        verify(inventoryRepository, times(1)).findAllById(productIds);
    }

    @Test
    @DisplayName("Éxito - Debería retornar lista vacía cuando no se encuentran productos")
    void shouldReturnEmptyListWhenNoProductsFound() throws Exception {
        // Arrange
        List<String> productIds = List.of("UNKNOWN-999");
        InventoryBatchRequest request = new InventoryBatchRequest(productIds);

        when(inventoryRepository.findAllById(productIds)).thenReturn(List.of());

        // Act
        CompletableFuture<List<InventoryResponse>> futureResult = inventoryService.getStockByProductIdsAsync(request);
        List<InventoryResponse> responses = futureResult.get();

        // Assert
        assertThat(responses).isNotNull().isEmpty();
        verify(inventoryRepository, times(1)).findAllById(productIds);
    }

    @Test
    @DisplayName("Error - Debería propagar la excepción cuando el repositorio falla de forma asíncrona")
    void shouldPropagateExceptionWhenRepositoryFails() {
        // Arrange
        List<String> productIds = List.of(AppTestConstants.PRODUCT_FIRST_ID);
        InventoryBatchRequest request = new InventoryBatchRequest(productIds);

        when(inventoryRepository.findAllById(anyList())).thenThrow(new RuntimeException("Error de conexión a BD"));

        // Act & Assert
        CompletableFuture<List<InventoryResponse>> futureResult = inventoryService.getStockByProductIdsAsync(request);

        // Al ejecutarse de forma asíncrona dentro de supplyAsync, la excepción se envuelve en un ExecutionException
        assertThatThrownBy(futureResult::get)
                .isInstanceOf(ExecutionException.class)
                .hasCauseInstanceOf(RuntimeException.class)
                .hasRootCauseMessage("Error de conexión a BD");

        verify(inventoryRepository, times(1)).findAllById(productIds);
    }

    private static Stream<Arguments> provideDiezProductos() {
        EasyRandom easyRandom = new EasyRandom(new EasyRandomParameters().seed(123L));
 
        return IntStream.rangeClosed(1, AppTestConstants.TOTAL_PRODUCTOS)
                .mapToObj(i -> Arguments.of("PROD-" + i, easyRandom.nextInt(1, 500)));
    }
}