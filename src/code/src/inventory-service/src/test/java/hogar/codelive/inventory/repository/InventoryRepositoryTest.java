package hogar.codelive.inventory.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import hogar.codelive.inventory.entity.InventoryEntity;
import hogar.codelive.inventory.constants.AppTestConstants;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("InventoryRepository - Integration Tests")
class InventoryRepositoryTest {

    @Autowired
    private InventoryRepository inventoryRepository;

    private String targetId;
    private InventoryEntity sampleEntity;
    private List<InventoryEntity> inventoryList;

    @BeforeEach
    void setUp() {
        inventoryRepository.deleteAll();

        sampleEntity = InventoryEntity.builder()
                .productId(AppTestConstants.PRODUCT_FIRST_ID)
                .stock(150)
                .build();

        inventoryList = IntStream.rangeClosed(1, 10)
            .mapToObj(itemRow -> InventoryEntity.builder()
                    .productId(String.format(AppTestConstants.PRODUCT_FMT_ID, itemRow))
                    .stock(itemRow * 10)
                    .build())
            .toList();                

        inventoryRepository.saveAll(inventoryList);
    }

    @Test
    @DisplayName("Create - Debería guardar un nuevo inventario con éxito (Create)")
    void shouldSaveInventory() {
        InventoryEntity savedEntity = inventoryRepository.save(sampleEntity);

        assertAll("Validar inserción de entidad",
                () -> assertNotNull(savedEntity),
                () -> assertEquals(AppTestConstants.PRODUCT_FIRST_ID, savedEntity.getProductId()),
                () -> assertEquals(150, savedEntity.getStock())
        );
    }

    @Test
    @DisplayName("Read - Debería buscar y encontrar un inventario por su ID (Read)")
    void shouldFindInventoryById() {
        inventoryRepository.save(sampleEntity);

        Optional<InventoryEntity> foundEntityOpt = inventoryRepository.findById(AppTestConstants.PRODUCT_FIRST_ID);

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
        inventoryRepository.save(sampleEntity);

        InventoryEntity existingEntity = inventoryRepository.findById(AppTestConstants.PRODUCT_FIRST_ID).orElseThrow();
        existingEntity.setStock(300);
        InventoryEntity updatedEntity = inventoryRepository.saveAndFlush(existingEntity);

        assertEquals(300, updatedEntity.getStock());
    }

    @Test
    @DisplayName("Delete - Debería eliminar un registro de inventario por su ID (Delete)")
    void shouldDeleteInventory() {
        inventoryRepository.save(sampleEntity);

        inventoryRepository.deleteById(AppTestConstants.PRODUCT_FIRST_ID);
        Optional<InventoryEntity> deletedEntityOpt = inventoryRepository.findById(AppTestConstants.PRODUCT_FIRST_ID);

        assertFalse(deletedEntityOpt.isPresent());
    }
}