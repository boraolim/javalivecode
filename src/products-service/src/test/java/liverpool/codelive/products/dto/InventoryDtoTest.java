package liverpool.codelive.products.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("InventoryDto - Unit Tests")
class InventoryDtoTest {

    @Test
    @DisplayName("Should create object using builder and verify getters")
    void shouldCreateObjectUsingBuilder() {
        // Act
        InventoryDto dto = InventoryDto.builder()
                .productId("PROD-100")
                .stock(50)
                .build();

        // Assert
        assertThat(dto.getProductId()).isEqualTo("PROD-100");
        assertThat(dto.getStock()).isEqualTo(50);
    }

    @Test
    @DisplayName("Should create object using NoArgsConstructor and Setters")
    void shouldCreateObjectUsingNoArgsConstructorAndSetters() {
        // Arrange & Act
        InventoryDto dto = new InventoryDto();
        dto.setProductId("PROD-200");
        dto.setStock(15);

        // Assert
        assertThat(dto.getProductId()).isEqualTo("PROD-200");
        assertThat(dto.getStock()).isEqualTo(15);
    }

    @Test
    @DisplayName("Should clone object using toBuilder()")
    void shouldCloneObjectUsingToBuilder() {
        // Arrange
        InventoryDto original = InventoryDto.builder()
                .productId("PROD-100")
                .stock(10)
                .build();

        // Act
        InventoryDto clone = original.toBuilder()
                .stock(100)
                .build();

        // Assert
        assertThat(clone.getProductId()).isEqualTo("PROD-100"); // Permanece
        assertThat(clone.getStock()).isEqualTo(100); // Modificado
    }

    @Test
    @DisplayName("Should verify equals, hashCode and toString methods")
    void shouldVerifyEqualsHashCodeAndToString() {
        // Arrange
        InventoryDto dto1 = new InventoryDto("PROD-1", 5);
        InventoryDto dto2 = new InventoryDto("PROD-1", 5);
        InventoryDto dto3 = new InventoryDto("PROD-2", 10);

        // Assert
        assertThat(dto1)
                .isEqualTo(dto2)
                .isNotEqualTo(dto3)
                .hasSameHashCodeAs(dto2);
        
        assertThat(dto1.toString())
                .contains("productId=PROD-1", "stock=5");
    }
}