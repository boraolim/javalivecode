package hogar.codelive.inventory.request;

import java.util.Set;

import jakarta.validation.Validator;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("InventoryNewProductRequestTest - Unit Tests")
class InventoryNewProductRequestTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private InventoryNewProductRequest validRequest() {
        InventoryNewProductRequest request = new InventoryNewProductRequest();
        request.setIdProduct("EXT-001");
        request.setProductStock(100);
        return request;
    }

    @Test
    @DisplayName("Should accept valid request")
    void shouldAcceptValidRequest() {

        InventoryNewProductRequest request = validRequest();

        Set<ConstraintViolation<InventoryNewProductRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = { "EXT-000", "EXT-001", "EXT-123", "EXT-999"})
    @DisplayName("Should accept valid product ids")
    void shouldAcceptValidIds(String productId) {

        InventoryNewProductRequest request = validRequest();
        request.setIdProduct(productId);

        assertTrue(validator.validate(request).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 10, 100, 9999, Integer.MAX_VALUE })
    @DisplayName("Should accept valid stock values")
    void shouldAcceptValidStock(Integer stock) {

        InventoryNewProductRequest request = validRequest();
        request.setProductStock(stock);

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    @DisplayName("Should reject null productId")
    void shouldRejectNullProductId() {

        InventoryNewProductRequest request = validRequest();
        request.setIdProduct(null);

        assertFalse(validator.validate(request).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = { "", " ", "ABC-001", "EXT001", "EXT_001", "EXT-01", "EXT-1000", "ext-001", "EXT-A01" })
    @DisplayName("Should reject invalid product id")
    void shouldRejectInvalidProductId(String id) {

        InventoryNewProductRequest request = validRequest();
        request.setIdProduct(id);

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    @DisplayName("Should reject zero stock")
    void shouldRejectZeroStock() {

        InventoryNewProductRequest request = validRequest();
        request.setProductStock(0);

        assertFalse(validator.validate(request).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = { -1, -10, Integer.MIN_VALUE })
    @DisplayName("Should reject negative stock")
    void shouldRejectNegativeStock(Integer stock) {

        InventoryNewProductRequest request = validRequest();
        request.setProductStock(stock);

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    @DisplayName("Should reject null stock")
    void shouldRejectNullStock() {

        InventoryNewProductRequest request = validRequest();
        request.setProductStock(null);

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    @DisplayName("Should detect multiple validation errors")
    void shouldDetectMultipleErrors() {

        InventoryNewProductRequest request = new InventoryNewProductRequest();

        request.setIdProduct("ABC");
        request.setProductStock(0);

        Set<ConstraintViolation<InventoryNewProductRequest>> violations = validator.validate(request);

        assertEquals(2, violations.size());
    }
}
