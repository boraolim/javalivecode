package hogar.codelive.inventory.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import hogar.codelive.inventory.entity.InventoryEntity;
import hogar.codelive.inventory.constants.AppTestConstants;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@Transactional
@DisplayName("InventoryRepository - Integration Tests")
class InventoryRepositoryTest {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private TestEntityManager entityManager;

    private String targetId;
    private InventoryEntity sampleEntity;
    private List<InventoryEntity> inventoryList;

    @BeforeEach
    void setUp() {
        sampleEntity = InventoryEntity.builder()
                .productId(AppTestConstants.PRODUCT_LAST_ID)
                .stock(150)
                .build();

        inventoryList = IntStream.rangeClosed(1, 10)
            .mapToObj(itemRow -> InventoryEntity.builder()
                    .productId(String.format(AppTestConstants.PRODUCT_FMT_ID, itemRow))
                    .stock(itemRow * 10)
                    .build())
            .toList();                

        inventoryList.forEach(entityManager::persistAndFlush);
    }

    @Test
    @DisplayName("Create - Debería guardar un nuevo inventario con éxito (Create)")
    void shouldSaveInventory() {
        InventoryEntity savedEntity = inventoryRepository.save(sampleEntity);

        assertAll("Validar inserción de entidad",
                () -> assertNotNull(savedEntity),
                () -> assertEquals(AppTestConstants.PRODUCT_LAST_ID, savedEntity.getProductId()),
                () -> assertEquals(150, savedEntity.getStock())
        );
    }

    @Test
    @DisplayName("Read - Debería buscar y encontrar un inventario por su ID (Read)")
    void shouldFindInventoryById() {
        entityManager.persistAndFlush(sampleEntity);

        Optional<InventoryEntity> foundEntityOpt = inventoryRepository.findById(AppTestConstants.PRODUCT_LAST_ID);

        assertTrue(foundEntityOpt.isPresent());
        assertEquals(150, foundEntityOpt.get().getStock());
    }

    @Test
    @DisplayName("Retrieve - Debería encontrar un elemento precargado de la lista inicial")
    void shouldFindPreloadedInventoryFromList() {
        targetId = inventoryList.get(0).getProductId();

        Optional<InventoryEntity> foundEntityOpt = inventoryRepository.findById(targetId);

        assertTrue(foundEntityOpt.isPresent());
        assertEquals(10, foundEntityOpt.get().getStock());
    }

    @Test
    @DisplayName("Update - Debería actualizar el stock de un producto existente (Update)")
    void shouldUpdateInventoryStock() {
        entityManager.persistAndFlush(sampleEntity);

        InventoryEntity existingEntity = inventoryRepository.findById(AppTestConstants.PRODUCT_LAST_ID).orElseThrow();
        existingEntity.setStock(300);
        InventoryEntity updatedEntity = inventoryRepository.saveAndFlush(existingEntity);

        assertEquals(300, updatedEntity.getStock());
    }

    @Test
    @DisplayName("Delete - Debería eliminar un registro de inventario por su ID (Delete)")
    void shouldDeleteInventory() {
        entityManager.persistAndFlush(sampleEntity);

        inventoryRepository.deleteById(AppTestConstants.PRODUCT_LAST_ID);
        Optional<InventoryEntity> deletedEntityOpt = inventoryRepository.findById(AppTestConstants.PRODUCT_LAST_ID);

        assertFalse(deletedEntityOpt.isPresent());
    }
}