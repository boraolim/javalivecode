package liverpool.codelive.products.mapper;

import java.math.BigDecimal;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

import liverpool.codelive.products.entity.ProductEntity;
import liverpool.codelive.products.dto.ExternalProductDto;
import liverpool.codelive.products.response.EnrichedProductResponse;

@DisplayName("ProductMapper - Unit Tests")
class ProductMapperTest {
    private final EasyRandom easyRandom = new EasyRandom();
    private final ProductMapper mapper = org.mapstruct.factory.Mappers.getMapper(ProductMapper.class);

    @Test
    @DisplayName("toEntity - Should map all fields correctly including title to name")
    void shouldMapAllFieldsCorrectlyIncludingTitleToName() {
        ExternalProductDto dto = new ExternalProductDto(
                "EXT-001",
                "External product",
                "External description",
                "199.99",
                "MXN",
                "Electronics",
                true
        );

        ProductEntity entity = mapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo("EXT-001");
        assertThat(entity.getName()).isEqualTo("External product");
        assertThat(entity.getDescription()).isEqualTo("External description");
        assertThat(entity.getPrice()).isEqualByComparingTo(new BigDecimal("199.99"));
    }

    @Test
    @DisplayName("toEntity - Should return BigDecimal.ZERO when price is null")
    void shouldReturnZeroWhenPriceIsNull() {
        ExternalProductDto dto = new ExternalProductDto(
                "EXT-002",
                "Product without price",
                "Description",
                null,
                "MXN",
                "Electronics",
                true
        );

        ProductEntity entity = mapper.toEntity(dto);

        assertThat(entity.getPrice()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("toEntity - Should return BigDecimal.ZERO when price is blank")
    void shouldReturnZeroWhenPriceIsBlank() {
        ExternalProductDto dto = new ExternalProductDto(
                "EXT-003",
                "Product with empty price",
                "Description",
                "   ",
                "MXN",
                "Electronics",
                true
        );

        ProductEntity entity = mapper.toEntity(dto);

        assertThat(entity.getPrice()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("toEntity - Should return null when DTO is null")
    void shouldReturnNullWhenDtoIsNull() {
        ProductEntity entity = mapper.toEntity(null);

        assertThat(entity).isNull();
    }

    @Test
    @DisplayName("toEntity - Should map without errors when using random data")
    void shouldMapRandomDataWithoutErrors() {
        ExternalProductDto dto = easyRandom.nextObject(ExternalProductDto.class);
        dto.setPrice("123.45"); 

        ProductEntity entity = mapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(dto.getId());
        assertThat(entity.getName()).isEqualTo(dto.getTitle());
        assertThat(entity.getDescription()).isEqualTo(dto.getDescription());
        assertThat(entity.getPrice()).isEqualByComparingTo(new BigDecimal("123.45"));
    }

    @Test
    @DisplayName("toEnrichedResponse - Should map base fields and ignore stock and inventoryStatus")
    void shouldMapBaseFieldsAndIgnoreStockAndInventoryStatus() {
        ProductEntity entity = easyRandom.nextObject(ProductEntity.class);

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
        EnrichedProductResponse response = mapper.toEnrichedResponse(null);

        assertThat(response).isNull();
    }

    // --- PARAMETERIZED TESTS FOR stringToBigDecimal ---

    @ParameterizedTest(name = "Converting \"'{0}'\" should yield {1}")
    @MethodSource("provideStringToBigDecimalCases")
    @DisplayName("stringToBigDecimal - Should map inputs to expected BigDecimals")
    void shouldConvertStringToBigDecimalCorrectly(String input, BigDecimal expected) {
        BigDecimal result = mapper.stringToBigDecimal(input);
        assertThat(result).isEqualByComparingTo(expected);
    }

    private static Stream<Arguments> provideStringToBigDecimalCases() {
        return Stream.of(
            // Valid conversions
            Arguments.of("199.99", new BigDecimal("199.99")),
            Arguments.of("  50.25  ", new BigDecimal("50.25")),
            Arguments.of("-15.50", new BigDecimal("-15.50")),
            
            // Null, empty, and blank cases (should return ZERO)
            Arguments.of(null, BigDecimal.ZERO),
            Arguments.of("", BigDecimal.ZERO),
            Arguments.of("   ", BigDecimal.ZERO),
            
            // Invalid formats (should fall back to ZERO without throwing exceptions)
            Arguments.of("N/A", BigDecimal.ZERO),
            Arguments.of("$199.99", BigDecimal.ZERO)
        );
    }
}
