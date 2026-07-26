package hogar.codelive.products.loader;

import java.util.List;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Collections;
import java.nio.charset.StandardCharsets;
import java.io.ByteArrayInputStream;

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
import org.mockito.junit.jupiter.MockitoExtension;

import hogar.codelive.products.entity.ProductEntity;
import hogar.codelive.products.mapper.ProductMapper;
import hogar.codelive.products.dto.ExternalProductDto;
import hogar.codelive.products.constants.AppTestConstants;
import hogar.codelive.products.repository.ProductRepository;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.doReturn;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
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
 
    private ProductEntity mappedProductEntity;

    @BeforeEach
    void setUp() {
        mappedProductEntity = ProductEntity.builder()
                .id(AppTestConstants.PRODUCT_FIRST_ID)
                .name("Test Product")
                .description("Description test")
                .price(new BigDecimal("99.99"))
                .build();
    }

    private void mockObjectMapperWithJson(String jsonInput) throws Exception {
        MappingIterator<ExternalProductDto> realIterator = new ObjectMapper()
                .readerFor(ExternalProductDto.class)
                .readValues(new ByteArrayInputStream(jsonInput.getBytes(StandardCharsets.UTF_8)));

        ObjectReader readerMock = mock(ObjectReader.class);
        doReturn(readerMock).when(objectMapper).readerFor(ExternalProductDto.class);
        doReturn(realIterator).when(readerMock).readValues(any(InputStream.class));
    }

    @Test
    @DisplayName("EXITO: Debería omitir el proceso si no hay productos activos para procesar")
    void run_sinProductosActivos_debeFinalizarSinAcciones() throws Exception {
        ObjectReader readerMock = mock(ObjectReader.class);
 
        @SuppressWarnings("unchecked")
        MappingIterator<ExternalProductDto> emptyIterator = mock(MappingIterator.class);
        lenient().when(emptyIterator.hasNext()).thenReturn(false);
 
        doReturn(readerMock).when(objectMapper).readerFor(ExternalProductDto.class);
        doReturn(emptyIterator).when(readerMock).readValues(any(InputStream.class));
 
        productDataLoader.run();
 
        verifyNoInteractions(productRepository);
        verifyNoInteractions(productMapper);
    }

    @Test
    @DisplayName("EXITO: Debería guardar los nuevos productos cuando no existen previamente en el repositorio")
    void run_conProductosNuevos_debeGuardarCorrectamente() throws Exception {
        String jsonInput = "[{"
                + "\"id\":\"" + AppTestConstants.PRODUCT_FIRST_ID + "\","
                + "\"title\":\"Test Product\","
                + "\"description\":\"Description test\","
                + "\"price\":99.99,"
                + "\"active\":true"
                + "}]";

        mockObjectMapperWithJson(jsonInput);

        when(productMapper.toEntity(any(ExternalProductDto.class))).thenReturn(mappedProductEntity);
        when(productRepository.findAllById(anyList())).thenReturn(Collections.emptyList());

        productDataLoader.run();

        verify(productMapper, times(1)).toEntity(any(ExternalProductDto.class));
        verify(productRepository, times(1)).findAllById(anyList());
        verify(productRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("EXITO: Debería omitir la inserción si todos los productos leídos ya existen en la base de datos")
    void run_conProductosYaExistentesEnBd_debeOmitirInsercion() throws Exception {
        String jsonInput = "[{"
                + "\"id\":\"" + AppTestConstants.PRODUCT_FIRST_ID + "\","
                + "\"title\":\"Test Product\","
                + "\"description\":\"Description test\","
                + "\"price\":99.99,"
                + "\"active\":true"
                + "}]";

        mockObjectMapperWithJson(jsonInput);

        when(productMapper.toEntity(any(ExternalProductDto.class))).thenReturn(mappedProductEntity);
        when(productRepository.findAllById(anyList())).thenReturn(List.of(mappedProductEntity));

        productDataLoader.run();

        verify(productRepository, times(1)).findAllById(anyList());
        verify(productRepository, org.mockito.Mockito.never()).saveAll(anyList());
    }
}