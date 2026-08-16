package hogar.codelive.products.request;

import java.util.Set;
import java.util.List;


import jakarta.validation.Validator;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ProductBatchRequestTest - Integration Tests")
class ProductBatchRequestTest {
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("EXITO: El request es válido cuando contiene una lista con IDs")
    void shouldBeValidWhenProductIdsAreProvided() {
        // Arrange
        ProductBatchRequest request = new ProductBatchRequest(List.of("EXT-002", "EXT-003"));

        // Act
        Set<ConstraintViolation<ProductBatchRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("ERROR: Debería fallar la validación cuando la lista de productIds está vacía")
    void shouldFailWhenProductIdsListIsEmpty() {
        // Arrange (asumiendo que tu record tiene anotación @NotEmpty o @Size(min = 1))
        ProductBatchRequest request = new ProductBatchRequest(List.of());

        // Act
        Set<ConstraintViolation<ProductBatchRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isNotEmpty().hasSize(1);
        
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("La lista de productos no puede estar vacía");
    }

    @Test
    @DisplayName("ERROR: Debería fallar la validación cuando la lista de productIds es nula")
    void shouldFailWhenProductIdsListIsNull() {
        // Arrange (asumiendo que tu record tiene anotación @NotNull)
        ProductBatchRequest request = new ProductBatchRequest(null);

        // Act
        Set<ConstraintViolation<ProductBatchRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isNotEmpty();
    }
}
