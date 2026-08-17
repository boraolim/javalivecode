package hogar.codelive.products.service;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.junit.jupiter.MockitoExtension;

import hogar.codelive.products.BaseProductTest;
import hogar.codelive.products.dto.InventoryDto;
import jakarta.persistence.EntityNotFoundException;
import hogar.codelive.products.mapper.ProductMapper;
import hogar.codelive.products.entity.ProductEntity;
import hogar.codelive.products.enums.InventoryStatus;
import hogar.codelive.products.client.InventoryClient;
import hogar.codelive.products.constants.AppConstants;
import hogar.codelive.products.dto.ExternalProductDto;
import hogar.codelive.products.request.ProductNewRequest;
import hogar.codelive.products.constants.AppTestConstants;
import hogar.codelive.products.request.ProductBatchRequest;
import hogar.codelive.products.repository.ProductRepository;
import hogar.codelive.products.request.ProductExistentRequest;
import hogar.codelive.products.response.EnrichedProductResponse;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ProductService - Unit Tests")
class ProductServiceTest extends BaseProductTest {
    // Usamos MockitoBean solo para el cliente externo (InventoryClient) si es una llamada HTTP simulada,
    // pero el repositorio (ProductRepository) será EL REAL y consultará la base de datos H2 poblada por tu SQL.
    @MockitoBean
    private InventoryClient inventoryClient;

    @Autowired
    private ProductService productService;

    @Test
    @DisplayName("search - Success: Should find products from H2 database and enrich with inventory")
    void searchProductIdAvailableStockSuccess() throws Exception {
        // Arrange
        // Usamos anyString() para evitar NullPointerException si la búsqueda de H2 
        // devuelve más de un producto que coincida con "laptop"
        when(inventoryClient.getStock(anyString()))
                .thenReturn(CompletableFuture.completedFuture(inventoryProductDto));

        // Act
        List<EnrichedProductResponse> result = productService.search("laptop").get();

        // Assert
        assertThat(result).isNotEmpty();
        
        // Verificamos específicamente el producto EXT-004 traído desde H2
        EnrichedProductResponse laptopGamer = result.stream()
                .filter(p -> p.getId().equals(AppTestConstants.PRODUCT_FOURTH_ID))
                .findFirst()
                .orElseThrow();

        assertThat(laptopGamer.getStock()).isEqualTo(100);
        assertThat(laptopGamer.getInventoryStatus()).isEqualTo(InventoryStatus.IN_STOCK);
    }

    @Test
    @DisplayName("search - Fallback: Should mark inventory as UNAVAILABLE when inventory service fails")
    void searchInventoryFailureFallback() throws Exception {
        // Arrange
        // Configuramos el cliente externo para que falle (esto SÍ se mockea porque es una llamada de red/externa)
        when(inventoryClient.getStock(anyString()))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Service down")));

        // Act
        // Buscamos "tablet" para que el repositorio real consulte H2 y encuentre el producto 'EXT-008' (productEighth)
        List<EnrichedProductResponse> result = productService.search("tablet").get();

        // Assert
        assertThat(result).isNotEmpty();
        
        EnrichedProductResponse tabletResult = result.stream()
                .filter(p -> p.getId().equals(AppTestConstants.PRODUCT_EIGHTH_ID))
                .findFirst()
                .orElseThrow();

        assertThat(tabletResult.getStock()).isNull();
        assertThat(tabletResult.getInventoryStatus()).isEqualTo(InventoryStatus.UNAVAILABLE);
    }

    @Test
    @DisplayName("getProductId - Success: Should return enriched product when id exists")
    void searchProductIdOutOfStockSuccess() throws Exception {
        // Arrange
        // Usamos anyString() para evitar NullPointerException si la búsqueda de H2 
        // devuelve más de un producto que coincida con "laptop"
        when(inventoryClient.getStock(anyString()))
                .thenReturn(CompletableFuture.completedFuture(inventorySixthProductDto));

        // Act
        List<EnrichedProductResponse> result = productService.search("laptop").get();

        // Assert
        assertThat(result).isNotEmpty();
        
        // Verificamos específicamente el producto EXT-006 traído desde H2
        EnrichedProductResponse sixthProduct = result.stream()
                .filter(p -> p.getId().equals(AppTestConstants.PRODUCT_SIXTH_ID))
                .findFirst()
                .orElseThrow();

        assertThat(sixthProduct.getStock()).isZero();
        assertThat(sixthProduct.getInventoryStatus()).isEqualTo(InventoryStatus.OUT_OF_STOCK);
    }

    /*    
    @Test
    @DisplayName("getProductId - Error: Should throw EntityNotFoundException when id does not exist")
    void getProductIdNotFound() {
        when(productRepository.findById("INVALID-ID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductId("INVALID-ID").get())
                .hasCauseInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("addNewProductAsync - Success: Should save and return new product response")
    void addNewProductAsyncSuccess() throws Exception {
        ProductNewRequest request = new ProductNewRequest();
        request.setNameProduct("New Item");

        ExternalProductDto dto = new ExternalProductDto();
        ProductEntity productFourth = ProductEntity.builder().id("NEW-1").name("New Item").build();
        EnrichedProductResponse responseProduct = new EnrichedProductResponse();
        responseProduct.setId("NEW-1");

        when(productMapper.toDto(request)).thenReturn(dto);
        when(productMapper.toEntity(dto)).thenReturn(productFourth);
        when(productRepository.save(any(ProductEntity.class))).thenReturn(productFourth);
        when(productMapper.toEnrichedResponse(productFourth)).thenReturn(responseProduct);

        EnrichedProductResponse result = productService.addNewProductAsync(request).get();

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("NEW-1");
        assertThat(result.getStock()).isNull();
        assertThat(result.getInventoryStatus()).isEqualTo(InventoryStatus.UNAVAILABLE);
    }

    @Test
    @DisplayName("addNewProductAsync - Error: Should throw IllegalArgumentException when request is null")
    void addNewProductAsyncNullRequest() {
        assertThatThrownBy(() -> productService.addNewProductAsync(null).get())
                .hasCauseInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(AppConstants.MSG_NOT_FOUND);
    }

    @Test
    @DisplayName("updateProductAsync - Success: Should update and return product response")
    void updateProductAsyncSuccess() throws Exception {
        String productId = "EXT-001";
        ProductExistentRequest request = new ProductExistentRequest();
        request.setNameProduct("Updated Name");

        ProductEntity existingEntity = ProductEntity.builder()
                .id(productId)
                .name("Old Name")
                .build();

        ExternalProductDto dto = new ExternalProductDto();
        dto.setId(productId);
        dto.setTitle("Old Name");

        ProductEntity updatedEntity = ProductEntity.builder()
                .id(productId)
                .name("Updated Name")
                .build();

        EnrichedProductResponse responseProduct = new EnrichedProductResponse();
        responseProduct.setId(productId);
        responseProduct.setName("Updated Name");

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingEntity));
        when(productMapper.fromEntity(existingEntity)).thenReturn(dto);
        when(productMapper.toEntity(dto)).thenReturn(updatedEntity);
        when(productRepository.save(any(ProductEntity.class))).thenReturn(updatedEntity);
        when(productMapper.toEnrichedResponse(updatedEntity)).thenReturn(responseProduct);

        EnrichedProductResponse result = productService.updateProductAsync(productId, request).get();

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(productId);
        assertThat(result.getName()).isEqualTo("Updated Name");
    }

    @Test
    @DisplayName("updateProductAsync - Error: Should throw IllegalArgumentException when request is null")
    void updateProductAsyncNullRequest() {
        assertThatThrownBy(() -> productService.updateProductAsync("EXT-001", null).get())
                .hasCauseInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(AppConstants.MSG_PRODUCT_NOT_NULL);
    }

    @Test
    @DisplayName("updateProductAsync - Error: Should throw EntityNotFoundException when product does not exist")
    void updateProductAsyncNotFound() {
        String productId = "INVALID-1";
        ProductExistentRequest request = new ProductExistentRequest();

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.updateProductAsync(productId, request).get())
                .hasCauseInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(String.format(AppConstants.MSG_INVENTORY_NOT_EXISTS, productId));
    }

    @Test
    @DisplayName("deleteProductAsync - Success: Should delete product when it exists")
    void deleteProductAsyncSuccess() throws Exception {
        String productId = "EXT-001";
        ProductEntity productFourth = ProductEntity.builder().id(productId).build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(productFourth));

        productService.deleteProductAsync(productId).get();

        verify(productRepository).delete(productFourth);
    }

    @Test
    @DisplayName("deleteProductAsync - Error: Should throw EntityNotFoundException when product does not exist")
    void deleteProductAsyncNotFound() {
        String productId = "INVALID-1";

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.deleteProductAsync(productId).get())
                .hasCauseInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(String.format(AppConstants.MSG_INVENTORY_NOT_EXISTS, productId));
    }

    @Test
    @DisplayName("search - Success: Should return enriched products list using batch inventory call")
    void searchSuccessWithBatchInventory() throws Exception {
        // Arrange
        ProductEntity productFourth = ProductEntity.builder()
                .id("EXT-001")
                .name("Laptop")
                .description("Gaming laptop")
                .price(BigDecimal.valueOf(1000))
                .build();

        EnrichedProductResponse responseProduct = new EnrichedProductResponse();
        responseProduct.setId("EXT-001");
        responseProduct.setName("Laptop");

        inventoryProductDto inventoryProductDto = new inventoryProductDto("EXT-001", 10);

        when(productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase("laptop", "laptop"))
                .thenReturn(List.of(productFourth));
        when(productMapper.toEnrichedResponse(productFourth)).thenReturn(responseProduct);
        
        // Mockeamos la llamada al cliente por lote (batch)
        when(inventoryClient.getStockBatch(any(ProductBatchRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(List.of(inventoryProductDto)));

        // Act
        List<EnrichedProductResponse> result = productService.searchBatch("laptop").get();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStock()).isEqualTo(10);
        assertThat(result.get(0).getInventoryStatus()).isEqualTo(InventoryStatus.IN_STOCK);
        
        // Verificamos que se haya invocado al cliente batch
        verify(inventoryClient).getStockBatch(any(ProductBatchRequest.class));
    }

    @Test
    @DisplayName("searchBatch - Empty: Should return empty list when no products match query")
    void searchBatchEmptyResult() throws Exception {
        // Arrange
        String query = "nonexistent";

        when(productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query))
                .thenReturn(List.of());

        // Act
        List<EnrichedProductResponse> result = productService.searchBatch(query).get();

        // Assert
        assertThat(result).isNotNull().isEmpty();
        
        // Verificamos que NUNCA se llame al cliente de inventario si la BD no arrojó productos
        verify(inventoryClient, never()).getStockBatch(any(ProductBatchRequest.class));
    }

    @Test
    @DisplayName("search - Fallback: Should mark inventory as UNAVAILABLE when batch inventory service fails")
    void searchBatchInventoryFailureFallback() throws Exception {
        // Arrange
        ProductEntity productFourth = ProductEntity.builder()
                .id("EXT-001")
                .name("Laptop")
                .build();

        EnrichedProductResponse responseProduct = new EnrichedProductResponse();
        responseProduct.setId("EXT-001");

        when(productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase("laptop", "laptop"))
                .thenReturn(List.of(productFourth));
        when(productMapper.toEnrichedResponse(productFourth)).thenReturn(responseProduct);
        
        // Simulamos que la llamada por lote falla retornando un CompletableFuture con error (o lista vacía desde el fallback del cliente)
        when(inventoryClient.getStockBatch(any(ProductBatchRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(List.of())); // O failedFuture según cómo gestione tu servicio el fallo en bloque

        // Act
        List<EnrichedProductResponse> result = productService.searchBatch("laptop").get();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStock()).isNull();
        assertThat(result.get(0).getInventoryStatus()).isEqualTo(InventoryStatus.UNAVAILABLE);
    }

    @Test
    @DisplayName("searchBatch - Fallback: Should log warning and mark inventory as UNAVAILABLE when batch inventory service fails")
    void searchBatchInventoryFailureFallbackWithUnavailable() throws Exception {
        // Arrange
        ProductEntity productFourth = ProductEntity.builder()
                .id("EXT-001")
                .name("Laptop")
                .build();

        EnrichedProductResponse responseProduct = new EnrichedProductResponse();
        responseProduct.setId("EXT-001");

        when(productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase("laptop", "laptop"))
                .thenReturn(List.of(productFourth));
        when(productMapper.toEnrichedResponse(productFourth)).thenReturn(responseProduct);
        
        // Simuamos el fallo que hace que 'ex' no sea nulo, entrando directamente al .map()
        when(inventoryClient.getStockBatch(any(ProductBatchRequest.class)))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Batch Service Down")));

        // Act
        List<EnrichedProductResponse> result = productService.searchBatch("laptop").get();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStock()).isNull();
        assertThat(result.get(0).getInventoryStatus()).isEqualTo(InventoryStatus.UNAVAILABLE);
        
        // Verificamos que se ejecutó el flujo del cliente
        verify(inventoryClient).getStockBatch(any(ProductBatchRequest.class));
    }

    @Test
    @DisplayName("searchBatch - Success: Should handle duplicate inventory items in batch response using merge function")
    void searchBatchWithDuplicateInventories() throws Exception {
        // Arrange
        ProductEntity productFourth = ProductEntity.builder()
                .id("EXT-001")
                .name("Laptop")
                .build();

        EnrichedProductResponse responseProduct = new EnrichedProductResponse();
        responseProduct.setId("EXT-001");
        responseProduct.setName("Laptop");

        // Simulamos una respuesta con duplicados para forzar el uso de la función de merge (existing, replacement) -> existing
        inventoryProductDto inventory1 = new inventoryProductDto("EXT-001", 10);
        inventoryProductDto inventory2 = new inventoryProductDto("EXT-001", 5); // Duplicado del mismo ID

        when(productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase("laptop", "laptop"))
                .thenReturn(List.of(productFourth));
        when(productMapper.toEnrichedResponse(productFourth)).thenReturn(responseProduct);
        when(inventoryClient.getStockBatch(any(ProductBatchRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(List.of(inventory1, inventory2)));

        // Act
        List<EnrichedProductResponse> result = productService.searchBatch("laptop").get();

        // Assert
        assertThat(result).hasSize(1);
        // Debe conservar el valor del primer elemento ("existing") debido a la regla de merge
        assertThat(result.get(0).getStock()).isEqualTo(10); 
        assertThat(result.get(0).getInventoryStatus()).isEqualTo(InventoryStatus.IN_STOCK);
        
        verify(inventoryClient).getStockBatch(any(ProductBatchRequest.class));
    }
    */
}