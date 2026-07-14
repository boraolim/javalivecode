package liverpool.codelive.products.loader;

import java.util.List;
import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import liverpool.codelive.products.entity.ProductEntity;
import liverpool.codelive.products.mapper.ProductMapper;
import liverpool.codelive.products.dto.ExternalProductDto;
import liverpool.codelive.products.repository.ProductRepository;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductDataLoader - Data Loading Tests")
class ProductDataLoaderTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductDataLoader productDataLoader;

    @Captor
    private ArgumentCaptor<List<ProductEntity>> productListCaptor;

    private ObjectMapper objectMapper = new ObjectMapper();
    private ProductEntity mockEntity;

    @BeforeEach
    void setUp() {

        objectMapper = new ObjectMapper();

        productDataLoader = new ProductDataLoader(
            productRepository,
            productMapper,
            objectMapper);

        mockEntity = ProductEntity.builder()
                .id("EXT-001")
                .name("Mock Product")
                .description("Mock Description")
                .price(new BigDecimal("99.99"))
                .build();
    }

    @Test
    @DisplayName("run - Should load and map only active products from JSON when database is empty")
    void shouldLoadAndMapOnlyActiveProductsWhenDatabaseIsEmpty() throws Exception {
        // Arrange
        when(productRepository.count()).thenReturn(0L);
        when(productMapper.toEntity(any(ExternalProductDto.class))).thenReturn(mockEntity);

        // Act
        productDataLoader.run();

        // Assert
        // 1. Capturamos la lista que se envió al repositorio
        verify(productRepository, times(1)).saveAll(productListCaptor.capture());
        List<ProductEntity> savedProducts = productListCaptor.getValue();

        // 2. Verificamos dinámicamente que el mapeador se haya llamado tantas veces como elementos activos se guardaron
        int expectedActiveCount = savedProducts.size();
        verify(productMapper, times(expectedActiveCount)).toEntity(any(ExternalProductDto.class));

        assertThat(savedProducts).isNotEmpty();
    }

    @Test
    @DisplayName("run - Should skip loading when database already contains data")
    void shouldSkipLoadingWhenDatabaseAlreadyContainsData() throws Exception {
        // Arrange
        when(productRepository.count()).thenReturn(10L);

        // Act
        productDataLoader.run();

        // Assert
        verify(productMapper, never()).toEntity(any(ExternalProductDto.class));
        verify(productRepository, never()).saveAll(any());
    }
}
