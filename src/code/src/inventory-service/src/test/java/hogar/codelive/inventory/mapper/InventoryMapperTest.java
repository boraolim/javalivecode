package hogar.codelive.inventory.mapper;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import org.mapstruct.factory.Mappers;

import hogar.codelive.inventory.dto.InventoryDto;
import hogar.codelive.inventory.entity.InventoryEntity;
import hogar.codelive.inventory.constants.AppTestConstants;
import hogar.codelive.inventory.response.InventoryResponse;
import hogar.codelive.inventory.request.InventoryExistentRequest;
import hogar.codelive.inventory.request.InventoryNewProductRequest;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;    
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("InventoryMapperTest - Unit Tests")
class InventoryMapperTest {

    private final InventoryMapper mapper = Mappers.getMapper(InventoryMapper.class);

    @Test
    @DisplayName("toEntity - Should map InventoryDto to InventoryEntity")
    void shouldMapDtoToEntity() {

        InventoryDto dto = InventoryDto.builder()
            .productId(AppTestConstants.PRODUCT_FIRST_ID)
            .stock(100)
            .build();

        InventoryEntity entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(AppTestConstants.PRODUCT_FIRST_ID, entity.getProductId());
        assertEquals(100, entity.getStock());
    }

    @Test
    @DisplayName("toEntity - Should return null when dto is null")
    void shouldReturnNullWhenDtoIsNull() {

        InventoryEntity entity = mapper.toEntity(null);

        assertNull(entity);
    }

    @Test
    @DisplayName("toDto - Should map InventoryNewProductRequest to InventoryDto")
    void shouldMapRequestToDto() {

        InventoryNewProductRequest request = new InventoryNewProductRequest();

        request.setIdProduct(AppTestConstants.PRODUCT_FIRST_ID);
        request.setProductStock(50);

        InventoryDto dto = mapper.toDto(request);

        assertNotNull(dto);
        assertEquals(AppTestConstants.PRODUCT_FIRST_ID, dto.getProductId());
        assertEquals(50, dto.getStock());
    }

    @Test
    @DisplayName("fromEntity - Should map InventoryEntity to InventoryDto")
    void shouldMapEntityToDto() {

        InventoryEntity entity = InventoryEntity.builder()
            .productId(AppTestConstants.PRODUCT_FIRST_ID)
            .stock(30)
            .build();

        InventoryDto dto = mapper.fromEntity(entity);

        assertNotNull(dto);
        assertEquals(AppTestConstants.PRODUCT_FIRST_ID, dto.getProductId());
        assertEquals(30, dto.getStock());
    }

    @Test
    @DisplayName("fromEntity - Should return null when entity is null")
    void shouldReturnNullWhenEntityIsNull() {

        InventoryDto dto = mapper.fromEntity(null);

        assertNull(dto);
    }

    @Test
    @DisplayName("fromDto - Should map InventoryDto to InventoryResponse")
    void shouldMapFromDtoToResponse() {

        InventoryDto dto = InventoryDto.builder()
            .productId(AppTestConstants.PRODUCT_FIRST_ID)
            .stock(80)
            .build();

        InventoryResponse response = mapper.fromDto(dto);

        assertNotNull(response);
        assertEquals(AppTestConstants.PRODUCT_FIRST_ID, response.getProductId());
        assertEquals(80, response.getStock());
    }

    @Test
    @DisplayName("fromDto - Should return null when dto is null")
    void shouldReturnNullWhenFromDtoIsNull() {

        InventoryResponse response = mapper.fromDto(null);

        assertNull(response);
    }

    @ParameterizedTest
    @MethodSource("stocks")
    @DisplayName("Should map different stock values")
    void shouldMapDifferentStockValues(Integer stock) {

        InventoryDto dto = InventoryDto.builder()
            .productId(AppTestConstants.PRODUCT_FIRST_ID)
            .stock(stock)
            .build();

        InventoryEntity entity = mapper.toEntity(dto);

        assertEquals(stock, entity.getStock());
    }

    static Stream<Integer> stocks() {
        return Stream.of(0, 1, 10, 100, Integer.MAX_VALUE);
    }

    @Test
    @DisplayName("Should map null productId")
    void shouldMapNullProductId() {

        InventoryDto dto = InventoryDto.builder()
            .productId(null)
            .stock(10)
            .build();

        InventoryEntity entity = mapper.toEntity(dto);

        assertNull(entity.getProductId());
        assertEquals(10, entity.getStock());
    }

    @Test
    @DisplayName("Should map null stock")
    void shouldMapNullStock() {

        InventoryDto dto = InventoryDto.builder()
            .productId(AppTestConstants.PRODUCT_FIRST_ID)
            .stock(null)
            .build();

        InventoryEntity entity = mapper.toEntity(dto);

        assertEquals(AppTestConstants.PRODUCT_FIRST_ID, entity.getProductId());
        assertNull(entity.getStock());
    }

    @Test
    @DisplayName("toDto - Should return null when request is null")
    void shouldReturnNullWhenRequestIsNull() {

        // Act
        InventoryDto result = mapper.toDto(null);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("updateEntity - Should update stock")
    void shouldUpdateStock() {
        InventoryExistentRequest request = new InventoryExistentRequest();
        request.setProductStock(150);

        InventoryDto dto = InventoryDto.builder()
            .productId(AppTestConstants.PRODUCT_FIRST_ID)
            .stock(50)
            .build();

        mapper.updateEntity(request, dto);

        assertEquals(AppTestConstants.PRODUCT_FIRST_ID, dto.getProductId());
        assertEquals(150, dto.getStock());
    }

    @Test
    @DisplayName("updateEntity - Should ignore null stock")
    void shouldIgnoreNullStock() {

        InventoryExistentRequest request = new InventoryExistentRequest();
        request.setProductStock(null);

        InventoryDto dto = InventoryDto.builder()
            .productId(AppTestConstants.PRODUCT_FIRST_ID)
            .stock(25)
            .build();

        mapper.updateEntity(request, dto);

        assertEquals(AppTestConstants.PRODUCT_FIRST_ID, dto.getProductId());
        assertEquals(25, dto.getStock());
    }

    @Test
    @DisplayName("updateEntity - Should wipe stock when request stock is zero")
    void shouldUpdateStockWithZero() {

        InventoryExistentRequest request = new InventoryExistentRequest();
        request.setProductStock(0);

        InventoryDto dto = InventoryDto.builder()
            .productId(AppTestConstants.PRODUCT_FIRST_ID)
            .stock(50)
            .build();

        mapper.updateEntity(request, dto);

        assertEquals(0, dto.getStock());
    }

    @Test
    @DisplayName("updateEntity - Should do nothing when request is null")
    void shouldDoNothingWhenRequestIsNull() {

        InventoryDto dto = InventoryDto.builder()
            .productId(AppTestConstants.PRODUCT_FIRST_ID)
            .stock(80)
            .build();

        mapper.updateEntity(null, dto);

        assertEquals(AppTestConstants.PRODUCT_FIRST_ID, dto.getProductId());
        assertEquals(80, dto.getStock());
    }

    @Test
    @DisplayName("updateEntity - Should throw NullPointerException when target is null")
    void shouldThrowExceptionWhenTargetIsNull() {

        InventoryExistentRequest request = new InventoryExistentRequest();
        request.setProductStock(100);

        assertThrows(NullPointerException.class, () -> mapper.updateEntity(request, null));
    }

    @Test
    void shouldThrowExceptionWhenBothAreNull() {

        assertDoesNotThrow(() -> mapper.updateEntity(null, null));
    }
}