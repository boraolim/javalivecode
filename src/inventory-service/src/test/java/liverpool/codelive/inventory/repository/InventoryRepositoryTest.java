package liverpool.codelive.inventory.repository;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import liverpool.codelive.inventory.entity.InventoryEntity;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@Transactional // 🚀 Hace Rollback automático de los inserts después de cada test
@DisplayName("InventoryRepository - Integration Tests")
class InventoryRepositoryTest {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private TestEntityManager entityManager;

    private InventoryEntity sampleEntity;

    @BeforeEach
    void setUp() {
        sampleEntity = InventoryEntity.builder()
                .productId("PROD-999")
                .stock(150)
                .build();
    }

    @Test
    @DisplayName("C - Debería guardar un nuevo inventario con éxito (Create)")
    void shouldSaveInventory() {
        // When
        InventoryEntity savedEntity = inventoryRepository.save(sampleEntity);

        // Then
        assertAll("Validar inserción de entidad",
                () -> assertNotNull(savedEntity),
                () -> assertEquals("PROD-999", savedEntity.getProductId()),
                () -> assertEquals(150, savedEntity.getStock())
        );
    }

    @Test
    @DisplayName("R - Debería buscar y encontrar un inventario por su ID (Read)")
    void shouldFindInventoryById() {
        // Given
        // Usamos TestEntityManager para persistir antes del test de lectura limpia
        entityManager.persistAndFlush(sampleEntity);

        // When
        Optional<InventoryEntity> foundEntityOpt = inventoryRepository.findById("PROD-999");

        // Then
        assertTrue(foundEntityOpt.isPresent(), "La entidad debería existir en la base de datos");
        assertEquals(150, foundEntityOpt.get().getStock());
    }

    @Test
    @DisplayName("U - Debería actualizar el stock de un producto existente (Update)")
    void shouldUpdateInventoryStock() {
        // Given
        entityManager.persistAndFlush(sampleEntity);

        // When
        InventoryEntity existingEntity = inventoryRepository.findById("PROD-999").orElseThrow();
        existingEntity.setStock(300); // Modificamos el stock
        InventoryEntity updatedEntity = inventoryRepository.saveAndFlush(existingEntity);

        // Then
        assertEquals(300, updatedEntity.getStock(), "El stock debió actualizarse a 300");
    }

    @Test
    @DisplayName("D - Debería eliminar un registro de inventario por su ID (Delete)")
    void shouldDeleteInventory() {
        // Given
        entityManager.persistAndFlush(sampleEntity);

        // When
        inventoryRepository.deleteById("PROD-999");
        Optional<InventoryEntity> deletedEntityOpt = inventoryRepository.findById("PROD-999");

        // Then
        assertFalse(deletedEntityOpt.isPresent(), "La entidad debió ser eliminada correctamente");
    }
}