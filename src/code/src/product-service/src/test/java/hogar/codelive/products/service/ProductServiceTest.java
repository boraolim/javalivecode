package hogar.codelive.products.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import org.mockito.quality.Strictness;

import org.springframework.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.mockito.junit.jupiter.MockitoSettings;

import hogar.codelive.products.BaseProductTest;
import hogar.codelive.products.dto.InventoryDto;
import jakarta.persistence.EntityNotFoundException;
import hogar.codelive.products.entity.ProductEntity;
import hogar.codelive.products.enums.InventoryStatus;
import hogar.codelive.products.client.InventoryClient;
import hogar.codelive.products.constants.AppConstants;
import hogar.codelive.products.constants.AppTestConstants;
import hogar.codelive.products.request.ProductBatchRequest;
import hogar.codelive.products.repository.ProductRepository;
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
    private CacheManager cacheManager;

    @Autowired
    private ProductRepository productRepository; // <-- Agrega esta línea si te falta

    @Autowired
    private ProductService productService;

    @BeforeEach
    void setUp() {
        // Limpia la caché antes de ejecutar cada prueba individual
        Optional.ofNullable(cacheManager.getCache("productSearchCache"))
                .ifPresent(org.springframework.cache.Cache::clear);
        Optional.ofNullable(cacheManager.getCache("productSearchBatchCache"))
                .ifPresent(org.springframework.cache.Cache::clear);
        Optional.ofNullable(cacheManager.getCache("productByIdCache"))
                .ifPresent(org.springframework.cache.Cache::clear);
    }

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
        inventorySixthProductDto.setStock(0); // EXT-006 tiene stock 0

        // Configuramos por defecto que cualquier llamada devuelva stock 0 o un valor seguro,
        // o usamos anyString() si sabemos que la búsqueda solo traerá un producto.
        when(inventoryClient.getStock(anyString()))
                .thenReturn(CompletableFuture.completedFuture(inventorySixthProductDto));

        // Act
        List<EnrichedProductResponse> result = productService.search("laptop").get();

        // Assert
        assertThat(result).isNotEmpty();
        
        EnrichedProductResponse sixthProduct = result.stream()
                .filter(p -> p.getId().equals(AppTestConstants.PRODUCT_SIXTH_ID))
                .findFirst()
                .orElseThrow(() -> new AssertionError("El producto EXT-006 no fue encontrado"));

        assertThat(sixthProduct.getStock()).isZero();
        assertThat(sixthProduct.getInventoryStatus()).isEqualTo(InventoryStatus.OUT_OF_STOCK);
    }

    @Test
    @DisplayName("getProductId - Error: Should throw EntityNotFoundException when id does not exist")
    void getProductIdNotFound() {
        // Act & Assert
        // Al ser un repositorio real, buscará "INVALID-ID" en H2, no lo encontrará
        // y el servicio lanzará la excepción esperada.
        assertThatThrownBy(() -> productService.getProductId(AppTestConstants.PRODUCT_INVALID_ID).get())
                .hasCauseInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("addNewProductAsync - Success: Should save and return new product response")
    void addNewProductAsyncSuccess() throws Exception {
        
        // Arrange & Act
        // Asignamos aquí el nuevo request desde la variable 'newProductRequest'.
        // Al ser una prueba de integración, el mapper real convertirá el request,
        // el repositorio real guardará en H2 y se construirá la respuesta.
        EnrichedProductResponse result = productService.addNewProductAsync(newProductRequest).get();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull(); // El repositorio asignará un ID (o el generado por tu lógica/base de datos)
        assertThat(result.getName()).isEqualTo("Leche Alpura");
        assertThat(result.getStock()).isNull();
        assertThat(result.getInventoryStatus()).isEqualTo(InventoryStatus.UNAVAILABLE);
    }
    
    @Test
    @DisplayName("addNewProductAsync - Error: Should throw IllegalArgumentException when request is null")
    void addNewProductAsyncNullRequest() {
        // Act & Assert
        assertThatThrownBy(() -> productService.addNewProductAsync(null).get())
                .hasCauseInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(AppConstants.MSG_NOT_FOUND);
    }
    
    @Test
    @DisplayName("updateProductAsync - Success: Should update and return product response")
    void updateProductAsyncSuccess() throws Exception {
        // Arrange
        // Usamos un ID que sabemos existe por nuestro script SQL (EXT-001). Por si las dudas guardamos nuevamente el productFirst.
        targetId = AppTestConstants.PRODUCT_FIRST_ID;
        findOrCreateProduct(productFirst);

        // Act
        // El servicio buscará el producto real en H2, usará el Mapper real para actualizarlo
        // y guardará el cambio en la base de datos real.
        EnrichedProductResponse result = productService.updateProductAsync(targetId, existentProductRequest).get();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(targetId);
        assertThat(result.getName()).isEqualTo("Updated Name");

        // Opcional: Verificación extra de persistencia (opcional si quieres asegurar el guardado)
        // Puedes consultar el repositorio real para confirmar que el nombre cambió en H2
        ProductEntity savedProduct = productRepository.findById(targetId).orElseThrow();
        assertThat(savedProduct.getName()).isEqualTo("Updated Name");
        assertThat(savedProduct.getDescription()).isEqualTo("Cambio de la descripción del artículo EXT-001");
    }
    
    @Test
    @DisplayName("updateProductAsync - Error: Should throw IllegalArgumentException when request is null")
    void updateProductAsyncNullRequest() {
        // Act & Assert
        assertThatThrownBy(() -> productService.updateProductAsync(AppTestConstants.PRODUCT_FIRST_ID, null).get())
                .hasCauseInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(AppConstants.MSG_PRODUCT_NOT_NULL);
    }

    @Test
    @DisplayName("updateProductAsync - Error: Should throw EntityNotFoundException when product does not exist")
    void updateProductAsyncNotFound() {
        // Arrange
        targetId = AppTestConstants.PRODUCT_INVALID_ID;

        // Act & Assert
        // El repositorio real buscará "INVALID-ID" en H2, no lo encontrará y lanzará la EntityNotFoundException
        assertThatThrownBy(() -> productService.updateProductAsync(targetId, existentProductRequest).get())
                .hasCauseInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(String.format(AppConstants.MSG_INVENTORY_NOT_EXISTS, targetId));
    }

    @Test
    @DisplayName("deleteProductAsync - Success: Should delete product when it exists")
    void deleteProductAsyncSuccess() throws Exception {
        // Arrange
        targetId = AppTestConstants.PRODUCT_FIRST_ID;
        findOrCreateProduct(productFirst);

        // Act
        // El servicio buscará y eliminará el producto real de la base de datos H2
        productService.deleteProductAsync(targetId).get();

        // Assert
        // Verificamos de forma real en la BD que el producto ya no existe
        boolean exists = productRepository.existsById(targetId);
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("deleteProductAsync - Error: Should throw EntityNotFoundException when product does not exist")
    void deleteProductAsyncNotFound() {
        // Arrange
        targetId = AppTestConstants.PRODUCT_INVALID_ID;

        // Act & Assert
        // El repositorio real buscará un ID inexistente en H2 y lanzará la excepción esperada
        assertThatThrownBy(() -> productService.deleteProductAsync(targetId).get())
                .hasCauseInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(String.format(AppConstants.MSG_INVENTORY_NOT_EXISTS, targetId));
    }

    @Test
    @DisplayName("searchBatch - Success: Should return enriched products list using batch inventory call")
    void searchSuccessWithBatchInventory() throws Exception {
        // Arrange
        // Usamos anyString() para evitar NullPointerException si la búsqueda de H2 
        // devuelve más de un producto que coincida con "laptop"
        when(inventoryClient.getStockBatch(any(ProductBatchRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(List.of(inventoryProductDto)));

        // Act
        List<EnrichedProductResponse> result = productService.searchBatch("laptop").get();

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
    @DisplayName("searchBatch - Empty: Should return empty list when no products match query")
    void searchBatchEmptyResult() throws Exception {
        // Arrange
        queryStringFor = AppTestConstants.QUERY_STRING_NOT_EXIST;

        // Act
        // La consulta a la base de datos H2 real no encontrará coincidencias y retornará lista vacía de forma natural
        List<EnrichedProductResponse> result = productService.searchBatch(queryStringFor).get();

        // Assert
        assertThat(result).isNotNull().isEmpty();
        
        // Verificamos que NUNCA se llame al cliente de inventario si la BD no arrojó productos
        verify(inventoryClient, never()).getStockBatch(any(ProductBatchRequest.class));
    }

    @Test
    @DisplayName("searchBatch - Fallback: Should log warning and mark inventory as UNAVAILABLE when batch inventory service fails")
    void searchBatchInventoryFailureFallback() throws Exception {
        // Arrange
        // Configuramos el método batch del cliente externo para que falle correctamente
        when(inventoryClient.getStockBatch(any(ProductBatchRequest.class)))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Service down")));

        // Act
        List<EnrichedProductResponse> result = productService.searchBatch("tablet").get();

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
    @DisplayName("searchBatch - Success: Should handle duplicate inventory items in batch response using merge function")
    void searchBatchWithDuplicateInventories() throws Exception {
        // Arrange: Guardamos el producto real en la base de datos H2
        targetId = AppTestConstants.PRODUCT_FOURTH_ID;
        findOrCreateProduct(productFourth);

        // Simulamos una respuesta con duplicados para forzar el uso de la función de merge (existing, replacement) -> existing
        InventoryDto inventory1 = new InventoryDto(AppTestConstants.PRODUCT_FOURTH_ID, 10);
        InventoryDto inventory2 = new InventoryDto(AppTestConstants.PRODUCT_FOURTH_ID, 5); // Duplicado del mismo ID

        when(inventoryClient.getStockBatch(any(ProductBatchRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(List.of(inventory1, inventory2)));

        // Act
        List<EnrichedProductResponse> result = productService.searchBatch("laptop").get();

        // Assert
        assertThat(result).isNotEmpty();

        EnrichedProductResponse laptopResult = result.stream()
                .filter(p -> p.getId().equals(AppTestConstants.PRODUCT_FOURTH_ID))
                .findFirst()
                .orElseThrow();

        // Debe conservar el valor del primer elemento ("existing") debido a la regla de merge
        assertThat(laptopResult.getStock()).isEqualTo(10); 
        assertThat(laptopResult.getInventoryStatus()).isEqualTo(InventoryStatus.IN_STOCK);
        
        verify(inventoryClient).getStockBatch(any(ProductBatchRequest.class));
    }

    @Test
    @DisplayName("searchBatch - Success: Should return enriched product when id exists")
    void searchBatchProductIdOutOfStockSuccess() throws Exception {
        // Arrange
        inventorySixthProductDto.setStock(0); // EXT-006 tiene stock 0

        // Configuramos por defecto que cualquier llamada devuelva stock 0 o un valor seguro,
        // o usamos anyString() si sabemos que la búsqueda solo traerá un producto.
        when(inventoryClient.getStockBatch(any(ProductBatchRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(List.of(inventorySixthProductDto)));

        // Act
        List<EnrichedProductResponse> result = productService.searchBatch("laptop").get();

        // Assert
        assertThat(result).isNotEmpty();
        
        EnrichedProductResponse sixthProduct = result.stream()
                .filter(p -> p.getId().equals(AppTestConstants.PRODUCT_SIXTH_ID))
                .findFirst()
                .orElseThrow(() -> new AssertionError("El producto EXT-006 no fue encontrado"));

        assertThat(sixthProduct.getStock()).isZero();
        assertThat(sixthProduct.getInventoryStatus()).isEqualTo(InventoryStatus.OUT_OF_STOCK);
    }

    private ProductEntity findOrCreateProduct(ProductEntity product) {
        return productRepository.findById(product.getId())
                .orElseGet(() -> {
                    return productRepository.save(product);
                });
    }
}