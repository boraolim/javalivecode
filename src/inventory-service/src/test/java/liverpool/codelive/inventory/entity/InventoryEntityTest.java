package liverpool.codelive.inventory.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DisplayName("InventoryEntityTest - Integration Tests")
class InventoryEntityTest {

    @Test
    @DisplayName("Debería crear una instancia de InventoryEntity usando el Builder de Lombok")
    void shouldCreateInventoryEntityWithBuilder() {
        // Given
        String expectedProductId = "PROD-12345";
        Integer expectedStock = 50;

        // When
        InventoryEntity entity = InventoryEntity.builder()
                .productId(expectedProductId)
                .stock(expectedStock)
                .build();

        // Then
        assertAll("Validación de propiedades mediante Builder",
                () -> assertNotNull(entity),
                () -> assertEquals(expectedProductId, entity.getProductId()),
                () -> assertEquals(expectedStock, entity.getStock())
        );
    }

    @Test
    @DisplayName("Debería validar los métodos Getter, Setter y Constructor vacío")
    void shouldValidateGettersAndSetters() {
        // Given & When
        InventoryEntity entity = new InventoryEntity();
        entity.setProductId("PROD-999");
        entity.setStock(10);

        // Then
        assertAll("Validación de Getters y Setters tradicionales",
                () -> assertEquals("PROD-999", entity.getProductId()),
                () -> assertEquals(10, entity.getStock())
        );
    }

    @Test
    @DisplayName("Debería validar el funcionamiento de los métodos equals, hashCode y toString de Lombok")
    void shouldValidateEqualsHashCodeAndToString() {
        // Given
        InventoryEntity entity1 = new InventoryEntity("PROD-1", 100);
        InventoryEntity entity2 = new InventoryEntity("PROD-1", 100);
        InventoryEntity entity3 = new InventoryEntity("PROD-2", 50);

        // Then
        assertAll("Validación de utilidades de Lombok (@Data)",
                // Equals & HashCode
                () -> assertEquals(entity1, entity2),
                () -> assertEquals(entity1.hashCode(), entity2.hashCode()),
                () -> assertNotEquals(entity1, entity3),
                
                // ToString
                () -> assertTrue(entity1.toString().contains("productId=PROD-1")),
                () -> assertTrue(entity1.toString().contains("stock=100"))
        );
    }
}