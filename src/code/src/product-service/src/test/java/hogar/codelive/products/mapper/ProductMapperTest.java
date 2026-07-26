package hogar.codelive.products.mapper;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import hogar.codelive.products.entity.ProductEntity;
import hogar.codelive.products.dto.ExternalProductDto;
import hogar.codelive.products.response.ProductResponse;
import hogar.codelive.products.request.ProductNewRequest;
import hogar.codelive.products.constants.AppTestConstants;
import hogar.codelive.products.request.ProductExistentRequest;
import hogar.codelive.products.response.EnrichedProductResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("ProductMapper - Unit Tests")
class ProductMapperTest {

    private final ProductMapper mapper = org.mapstruct.factory.Mappers.getMapper(ProductMapper.class);

    @Test
    @DisplayName("toEntity - Should map all fields correctly including title to name")
    void shouldMapAllFieldsCorrectlyIncludingTitleToName() {
        ExternalProductDto dto = new ExternalProductDto(
                AppTestConstants.PRODUCT_FIRST_ID,
                "External product",
                "External description",
                BigDecimal.valueOf(199.99),
                "MXN",
                "Electronics",
                true
        );

        ProductEntity entity = mapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(AppTestConstants.PRODUCT_FIRST_ID);
        assertThat(entity.getName()).isEqualTo("External product");
        assertThat(entity.getDescription()).isEqualTo("External description");
        assertThat(entity.getPrice()).isEqualByComparingTo(new BigDecimal("199.99"));
    }

    @Test
    @DisplayName("toEntity - Should return null when DTO is null")
    void shouldReturnNullWhenDtoIsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    @DisplayName("toEnrichedResponse - Should map base fields and ignore stock and inventoryStatus")
    void shouldMapBaseFieldsAndIgnoreStockAndInventoryStatus() {
        ProductEntity entity = ProductEntity.builder()
                .id(AppTestConstants.PRODUCT_FIRST_ID)
                .name("Laptop")
                .description("Gaming Laptop")
                .price(new BigDecimal("1299.99"))
                .build();

        EnrichedProductResponse response = mapper.toEnrichedResponse(entity);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(entity.getId());
        assertThat(response.getName()).isEqualTo(entity.getName());
        assertThat(response.getDescription()).isEqualTo(entity.getDescription());
        assertThat(response.getPrice()).isEqualByComparingTo(entity.getPrice());
        assertThat(response.getStock()).isNull();
        assertThat(response.getInventoryStatus()).isNull();
    }

    @Test
    @DisplayName("toEnrichedResponse - Should return null when entity is null")
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.toEnrichedResponse(null)).isNull();
    }

    @Test
    @DisplayName("toDto - Should map ProductNewRequest to ExternalProductDto successfully")
    void shouldMapProductRequestToExternalProductDto() {
        ProductNewRequest request = new ProductNewRequest();
        request.setProductId(AppTestConstants.PRODUCT_FIRST_ID);
        request.setNameProduct("Laptop Lenovo");
        request.setDescriptionProduct("Laptop Lenovo ThinkPad con 16 GB de RAM");
        request.setPriceProduct(BigDecimal.valueOf(1299.99));

        ExternalProductDto result = mapper.toDto(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(AppTestConstants.PRODUCT_FIRST_ID);
        assertThat(result.getTitle()).isEqualTo("Laptop Lenovo");
        assertThat(result.getDescription()).isEqualTo("Laptop Lenovo ThinkPad con 16 GB de RAM");
        assertThat(result.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(1299.99));
    }

    @Test
    @DisplayName("toDto - Should return null when request is null")
    void shouldReturnNullWhenRequestProductIsNull() {
        assertThat(mapper.toDto(null)).isNull();
    }

    @Test
    @DisplayName("fromEntity - Should map ProductEntity to ExternalProductDto successfully")
    void shouldMapEntityToExternalDtoSuccessfully() {
        ProductEntity entity = ProductEntity.builder()
                .id(AppTestConstants.PRODUCT_FIRST_ID)
                .name("Laptop")
                .description("Gaming Laptop")
                .price(new BigDecimal("1299.99"))
                .build();

        ExternalProductDto dto = mapper.fromEntity(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(AppTestConstants.PRODUCT_FIRST_ID);
        assertThat(dto.getTitle()).isEqualTo("Laptop");
        assertThat(dto.getDescription()).isEqualTo("Gaming Laptop");
        assertThat(dto.getPrice()).isEqualByComparingTo(new BigDecimal("1299.99"));
    }

    @Test
    @DisplayName("fromEntity - Should return null when entity is null")
    void shouldReturnNullWhenFromEntityIsNull() {
        assertThat(mapper.fromEntity(null)).isNull();
    }

    @Test
    @DisplayName("fromDto - Should map ExternalProductDto to ProductResponse successfully")
    void shouldMapDtoToResponseSuccessfully() {
        ExternalProductDto dto = new ExternalProductDto();
        dto.setId(AppTestConstants.PRODUCT_FIRST_ID);
        dto.setTitle("Laptop");
        dto.setDescription("Gaming Laptop");
        dto.setPrice(new BigDecimal("1299.99"));

        ProductResponse response = mapper.fromDto(dto);

        assertThat(response).isNotNull();
        assertThat(response.getIdProduct()).isEqualTo(AppTestConstants.PRODUCT_FIRST_ID);
        assertThat(response.getNameProduct()).isEqualTo("Laptop");
        assertThat(response.getDescriptionProduct()).isEqualTo("Gaming Laptop");
        assertThat(response.getPriceProduct()).isEqualByComparingTo(new BigDecimal("1299.99"));
    }

    @Test
    @DisplayName("fromDto - Should return null when DTO is null")
    void shouldReturnNullWhenFromDtoIsNull() {
        assertThat(mapper.fromDto(null)).isNull();
    }

    @Test
    @DisplayName("updateEntity - Should update all fields correctly")
    void shouldUpdateAllFields() {
        ProductExistentRequest request = validRequest();
        ExternalProductDto dto = validDto();

        mapper.updateEntity(request, dto);

        assertThat(dto.getId()).isEqualTo(AppTestConstants.PRODUCT_FIRST_ID);
        assertThat(dto.getTitle()).isEqualTo("Nintendo Switch OLED");
        assertThat(dto.getDescription()).isEqualTo("Nueva consola OLED");
        assertThat(dto.getPrice()).isEqualByComparingTo(new BigDecimal("399.99"));
    }

    @Test
    @DisplayName("updateEntity - Should throw exception when target DTO is null")
    void shouldThrowExceptionWhenTargetDtoIsNull() {
        ProductExistentRequest request = validRequest();
        assertThrows(NullPointerException.class, () -> mapper.updateEntity(request, null));
    }

    @Test
    @DisplayName("updateEntity - Should do nothing when request is null")
    void shouldDoNothingWhenRequestIsNull() {
        ExternalProductDto dto = validDto();

        mapper.updateEntity(null, dto);

        assertThat(dto.getId()).isEqualTo(AppTestConstants.PRODUCT_FIRST_ID);
        assertThat(dto.getTitle()).isEqualTo("Nintendo Switch");
        assertThat(dto.getDescription()).isEqualTo("Portable console");
        assertThat(dto.getPrice()).isEqualByComparingTo(new BigDecimal("299.99"));
    }

    @Test
    @DisplayName("updateEntity - Should ignore update when name is null")
    void shouldIgnoreNullName() {
        ProductExistentRequest request = new ProductExistentRequest();
        request.setDescriptionProduct("Nueva descripción");
        request.setPriceProduct(new BigDecimal("500"));

        ExternalProductDto dto = validDto();
        mapper.updateEntity(request, dto);

        assertThat(dto.getTitle()).isEqualTo("Nintendo Switch");
        assertThat(dto.getDescription()).isEqualTo("Nueva descripción");
        assertThat(dto.getPrice()).isEqualByComparingTo(new BigDecimal("500"));
    }

    @Test
    @DisplayName("updateEntity - Should ignore update when description is null")
    void shouldIgnoreNullDescription() {
        ProductExistentRequest request = new ProductExistentRequest();
        request.setNameProduct("Nuevo nombre");
        request.setPriceProduct(new BigDecimal("450"));

        ExternalProductDto dto = validDto();
        mapper.updateEntity(request, dto);

        assertThat(dto.getTitle()).isEqualTo("Nuevo nombre");
        assertThat(dto.getDescription()).isEqualTo("Portable console");
        assertThat(dto.getPrice()).isEqualByComparingTo(new BigDecimal("450"));
    }

    @Test
    @DisplayName("updateEntity - Should ignore update when price is null")
    void shouldIgnoreNullPrice() {
        ProductExistentRequest request = new ProductExistentRequest();
        request.setNameProduct("Nuevo nombre");

        ExternalProductDto dto = validDto();
        mapper.updateEntity(request, dto);

        assertThat(dto.getTitle()).isEqualTo("Nuevo nombre");
        assertThat(dto.getDescription()).isEqualTo("Portable console");
        assertThat(dto.getPrice()).isEqualByComparingTo(new BigDecimal("299.99"));
    }

    private ProductExistentRequest validRequest() {
        ProductExistentRequest request = new ProductExistentRequest();
        request.setNameProduct("Nintendo Switch OLED");
        request.setDescriptionProduct("Nueva consola OLED");
        request.setPriceProduct(new BigDecimal("399.99"));
        return request;
    }

    private ExternalProductDto validDto() {
        ExternalProductDto dto = new ExternalProductDto();
        dto.setId(AppTestConstants.PRODUCT_FIRST_ID);
        dto.setTitle("Nintendo Switch");
        dto.setDescription("Portable console");
        dto.setPrice(new BigDecimal("299.99"));
        return dto;
    }
}