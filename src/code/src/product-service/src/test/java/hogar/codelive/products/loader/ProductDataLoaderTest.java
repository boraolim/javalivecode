package hogar.codelive.products.loader;

import java.util.List;
import java.util.Map;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Collections;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.MappingIterator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Spy;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import hogar.codelive.products.mapper.ProductMapper;
import hogar.codelive.products.entity.ProductEntity;
import hogar.codelive.products.dto.ExternalProductDto;
import hogar.codelive.products.constants.AppTestConstants;
import hogar.codelive.products.repository.ProductRepository;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doReturn;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ProductDataLoader - Pruebas unitarias")
class ProductDataLoaderTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private ProductDataLoader productDataLoader;

    private String jsonInput;
    private ProductEntity existingProduct;

    @BeforeEach
    void setUp() throws Exception {
        existingProduct = ProductEntity.builder()
                .id(AppTestConstants.PRODUCT_FIRST_ID)
                .name("PlayStation 5 Slim")
                .description("Next-gen gaming console from Sony")
                .price(new BigDecimal("499.99"))
                .build();

        jsonInput = new ObjectMapper().writeValueAsString(List.of(
            Map.of("id", AppTestConstants.PRODUCT_FIRST_ID,
                "title", "PlayStation 5 Slim",
                "description", "Next-gen gaming console from Sony",
                "price", 499.99,
                "active", true)
        ));
    }

    @Test
    @DisplayName("EXITO: Debería omitir el proceso si no hay productos activos para procesar")
    void run_withoutActiveProducts_shouldFinishWithoutActions() throws Exception {
        // Arrange
        ObjectReader readerMock = mock(ObjectReader.class);
 
        @SuppressWarnings("unchecked")
        MappingIterator<ExternalProductDto> emptyIterator = mock(MappingIterator.class);
        doReturn(false).when(emptyIterator).hasNext();
 
        doReturn(readerMock).when(objectMapper).readerFor(ExternalProductDto.class);
        doReturn(emptyIterator).when(readerMock).readValues(any(InputStream.class));
 
        // Act
        productDataLoader.run();
 
        // Assert
        verifyNoInteractions(productRepository);
        verifyNoInteractions(productMapper);
    }

    @Test
    @DisplayName("EXITO: Debería guardar los nuevos productos cuando no existen previamente en el repositorio")
    void run_withNewProducts_shouldSaveCorrectly() throws Exception {
        // Arrange
        mockObjectMapperWithJson(jsonInput);
        when(productMapper.toEntity(any(ExternalProductDto.class))).thenReturn(existingProduct);
        when(productRepository.findAllById(anyList())).thenReturn(Collections.emptyList());

        // Act
        productDataLoader.run();

        // Assert
        verify(productMapper, times(1)).toEntity(any(ExternalProductDto.class));
        verify(productRepository, times(1)).findAllById(anyList());
        verify(productRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("EXITO: Debería omitir la inserción si todos los productos leídos ya existen en la base de datos")
    void run_withAlreadyExistingProductsInDb_shouldSkipInsertion() throws Exception {
        // Arrange
        mockObjectMapperWithJson(jsonInput);
        when(productMapper.toEntity(any(ExternalProductDto.class))).thenReturn(existingProduct);
        when(productRepository.findAllById(anyList())).thenReturn(List.of(existingProduct));

        // Act
        productDataLoader.run();

        // Assert
        verify(productRepository, times(1)).findAllById(anyList());
        verify(productRepository, never()).saveAll(anyList());
    }

    private void mockObjectMapperWithJson(String jsonInput) throws Exception {
        MappingIterator<ExternalProductDto> realIterator = new ObjectMapper()
                .readerFor(ExternalProductDto.class)
                .readValues(new ByteArrayInputStream(jsonInput.getBytes(StandardCharsets.UTF_8)));

        ObjectReader readerMock = mock(ObjectReader.class);
        doReturn(readerMock).when(objectMapper).readerFor(ExternalProductDto.class);
        doReturn(realIterator).when(readerMock).readValues(any(InputStream.class));
    }
}