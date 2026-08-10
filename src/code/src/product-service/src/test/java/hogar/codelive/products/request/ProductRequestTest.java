package hogar.codelive.products.request;

import java.math.BigDecimal;
import java.util.stream.Stream;

import jakarta.validation.Validator;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DisplayName("ProductRequest - Integration Tests")
class ProductRequestTest {
    private static Validator validator;
    private static ValidatorFactory validatorFactory;
    
    @BeforeAll
    static void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        validatorFactory.close();
    }

    @Test
    void shouldValidateProductRequestSuccessfully() {
        ProductNewRequest request = validRequest();

        assertTrue(validator.validate(request).isEmpty());
    }

    @ParameterizedTest
    @MethodSource("invalidProductIds")
    void shouldRejectInvalidProductId(String productId) {
        ProductNewRequest request = validRequest();
        request.setProductId(productId);

        assertFalse(validator.validate(request).isEmpty());
    }

    static Stream<Arguments> invalidProductIds() {
        return Stream.of(
            Arguments.of((String) null),
            Arguments.of("EXT-01"),
            Arguments.of("EXT-0001"),
            Arguments.of("EXT-ABC"),
            Arguments.of("ext-001"),
            Arguments.of("PROD-001"),
            Arguments.of("EXT_001"),
            Arguments.of("EXT-12A")
        );
    }

    @ParameterizedTest
    @MethodSource("validProductIds")
    void shouldAcceptValidProductId(String productId) {
        ProductNewRequest request = validRequest();
        request.setProductId(productId);

        assertTrue(validator.validate(request).isEmpty());
    }

    static Stream<Arguments> validProductIds() {
        return Stream.of(
            Arguments.of("EXT-001"),
            Arguments.of("EXT-123"),
            Arguments.of("EXT-999")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidTextValues")
    void shouldRejectInvalidNameProduct(String nameProduct) {
        ProductNewRequest request = validRequest();
        request.setNameProduct(nameProduct);

        assertFalse(validator.validate(request).isEmpty());
    }

    @ParameterizedTest
    @MethodSource("invalidTextValues")
    void shouldRejectInvalidDescriptionProduct(String descriptionProduct) {
        ProductNewRequest request = validRequest();
        request.setDescriptionProduct(descriptionProduct);

        assertFalse(validator.validate(request).isEmpty());
    }

    static Stream<Arguments> invalidTextValues() {
        return Stream.of(
            Arguments.of((String) null),
            Arguments.of("Producto@123"),
            Arguments.of("Producto#123"),
            Arguments.of("Producto-123"),
            Arguments.of("Producto_123"),
            Arguments.of("Producto.123")
        );
    }

    @Test
    void shouldAcceptAccentsInNameProduct() {
        ProductNewRequest request = validRequest();
        request.setNameProduct("Cámara Fotográfica");

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void shouldAcceptAccentsInDescriptionProduct() {
        ProductNewRequest request = validRequest();
        request.setDescriptionProduct(
            "Cámara fotográfica con resolución y batería recargable"
        );

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void shouldRejectNameProductWithMoreThan255Characters() {
        ProductNewRequest request = validRequest();
        request.setNameProduct("A".repeat(256));

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void shouldAcceptNameProductWith255Characters() {
        ProductNewRequest request = validRequest();
        request.setNameProduct("A".repeat(255));

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void shouldRejectDescriptionProductWithMoreThan255Characters() {
        ProductNewRequest request = validRequest();
        request.setDescriptionProduct("A".repeat(256));

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void shouldAcceptDescriptionProductWith255Characters() {
        ProductNewRequest request = validRequest();
        request.setDescriptionProduct("A".repeat(255));

        assertTrue(validator.validate(request).isEmpty());
    }

    @ParameterizedTest
    @MethodSource("validPrices")
    void shouldAcceptValidPrice(BigDecimal price) {
        ProductNewRequest request = validRequest();
        request.setPriceProduct(price);

        assertTrue(validator.validate(request).isEmpty());
    }

    static Stream<Arguments> validPrices() {
        return Stream.of(
            Arguments.of(BigDecimal.valueOf(0.01)),
            Arguments.of(BigDecimal.valueOf(1.00)),
            Arguments.of(BigDecimal.valueOf(1299.99)),
            Arguments.of(BigDecimal.valueOf(999999.99)));
    }

    @ParameterizedTest
    @MethodSource("invalidPrices")
    void shouldRejectInvalidPrice(BigDecimal price) {
        ProductNewRequest request = validRequest();
        request.setPriceProduct(price);

        assertFalse(validator.validate(request).isEmpty());
    }

    static Stream<Arguments> invalidPrices() {
        return Stream.of(
            Arguments.of(BigDecimal.valueOf(0.0)),
            Arguments.of(BigDecimal.valueOf(-0.01)),
            Arguments.of(BigDecimal.valueOf(-100.0)));
    }

    private static ProductNewRequest validRequest() {
        ProductNewRequest request = new ProductNewRequest();

        request.setProductId("EXT-001");
        request.setNameProduct("Laptop Lenovo");
        request.setDescriptionProduct("Laptop Lenovo ThinkPad con 16 GB de RAM");
        request.setPriceProduct(BigDecimal.valueOf(1299.99));

        return request;
    }
}
