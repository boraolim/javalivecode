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
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityNotFoundException;

import hogar.codelive.products.dto.InventoryDto;
import hogar.codelive.products.mapper.ProductMapper;
import hogar.codelive.products.entity.ProductEntity;
import hogar.codelive.products.enums.InventoryStatus;
import hogar.codelive.products.client.InventoryClient;
import hogar.codelive.products.constants.AppConstants;
import hogar.codelive.products.dto.ExternalProductDto;
import hogar.codelive.products.request.ProductNewRequest;
import hogar.codelive.products.repository.ProductRepository;
import hogar.codelive.products.request.ProductExistentRequest;
import hogar.codelive.products.response.EnrichedProductResponse;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService - Unit Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private InventoryClient inventoryClient;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("search - Success: Should return enriched products list when matching query")
    void searchSuccess() throws Exception {
        ProductEntity entity = ProductEntity.builder()
                .id("EXT-001")
                .name("Laptop")
                .description("Gaming laptop")
                .price(BigDecimal.valueOf(1000))
                .build();

        EnrichedProductResponse responseDto = new EnrichedProductResponse();
        responseDto.setId("EXT-001");
        responseDto.setName("Laptop");

        InventoryDto inventoryDto = new InventoryDto();
        inventoryDto.setStock(10);

        when(productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase("laptop", "laptop"))
                .thenReturn(List.of(entity));
        when(productMapper.toEnrichedResponse(entity)).thenReturn(responseDto);
        when(inventoryClient.getStock("EXT-001")).thenReturn(CompletableFuture.completedFuture(inventoryDto));

        List<EnrichedProductResponse> result = productService.search("laptop").get();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStock()).isEqualTo(10);
        assertThat(result.get(0).getInventoryStatus()).isEqualTo(InventoryStatus.IN_STOCK);
    }

    @Test
    @DisplayName("search - Fallback: Should mark inventory as UNAVAILABLE when inventory service fails")
    void searchInventoryFailureFallback() throws Exception {
        ProductEntity entity = ProductEntity.builder()
                .id("EXT-001")
                .name("Laptop")
                .build();

        EnrichedProductResponse responseDto = new EnrichedProductResponse();
        responseDto.setId("EXT-001");

        when(productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase("laptop", "laptop"))
                .thenReturn(List.of(entity));
        when(productMapper.toEnrichedResponse(entity)).thenReturn(responseDto);
        when(inventoryClient.getStock("EXT-001"))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Service down")));

        List<EnrichedProductResponse> result = productService.search("laptop").get();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStock()).isNull();
        assertThat(result.get(0).getInventoryStatus()).isEqualTo(InventoryStatus.UNAVAILABLE);
    }

    @Test
    @DisplayName("getProductId - Success: Should return enriched product when id exists")
    void getProductIdSuccess() throws Exception {
        ProductEntity entity = ProductEntity.builder()
                .id("EXT-001")
                .name("Mouse")
                .build();

        EnrichedProductResponse responseDto = new EnrichedProductResponse();
        responseDto.setId("EXT-001");

        InventoryDto inventoryDto = new InventoryDto();
        inventoryDto.setStock(0);

        when(productRepository.findById("EXT-001")).thenReturn(Optional.of(entity));
        when(productMapper.toEnrichedResponse(entity)).thenReturn(responseDto);
        when(inventoryClient.getStock("EXT-001")).thenReturn(CompletableFuture.completedFuture(inventoryDto));

        EnrichedProductResponse result = productService.getProductId("EXT-001").get();

        assertThat(result).isNotNull();
        assertThat(result.getStock()).isZero();
        assertThat(result.getInventoryStatus()).isEqualTo(InventoryStatus.OUT_OF_STOCK);
    }

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
        ProductEntity entity = ProductEntity.builder().id("NEW-1").name("New Item").build();
        EnrichedProductResponse responseDto = new EnrichedProductResponse();
        responseDto.setId("NEW-1");

        when(productMapper.toDto(request)).thenReturn(dto);
        when(productMapper.toEntity(dto)).thenReturn(entity);
        when(productRepository.save(any(ProductEntity.class))).thenReturn(entity);
        when(productMapper.toEnrichedResponse(entity)).thenReturn(responseDto);

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

        EnrichedProductResponse responseDto = new EnrichedProductResponse();
        responseDto.setId(productId);
        responseDto.setName("Updated Name");

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingEntity));
        when(productMapper.fromEntity(existingEntity)).thenReturn(dto);
        when(productMapper.toEntity(dto)).thenReturn(updatedEntity);
        when(productRepository.save(any(ProductEntity.class))).thenReturn(updatedEntity);
        when(productMapper.toEnrichedResponse(updatedEntity)).thenReturn(responseDto);

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
        ProductEntity entity = ProductEntity.builder().id(productId).build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(entity));

        productService.deleteProductAsync(productId).get();

        verify(productRepository).delete(entity);
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
}