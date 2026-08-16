package hogar.codelive.products.repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import hogar.codelive.products.entity.ProductEntity;
import hogar.codelive.products.constants.AppTestConstants;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
@DisplayName("ProductRepository - Integration Tests")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    private String targetId;
    private ProductEntity product1;
    private ProductEntity product2;
    private ProductEntity product3;
    private ProductEntity product4;
    private List<ProductEntity> inventoryList;

    @BeforeEach
    void setUp() {
        product1 = ProductEntity.builder()
                .id(AppTestConstants.PRODUCT_FIRST_ID)
                .name("PlayStation 5 Slim")
                .description("Next-gen gaming console from Sony")
                .price(new BigDecimal("499.99"))
                .build();

        product2 = ProductEntity.builder()
                .id(AppTestConstants.PRODUCT_SECOND_ID)
                .name("Nintendo Switch OLED")
                .description("Portable family console")
                .price(new BigDecimal("349.99"))
                .build();

        product3 = ProductEntity.builder()
                .id(AppTestConstants.PRODUCT_THIRD_ID)
                .name("Xbox Series X")
                .description("Powerful console with GamePass")
                .price(new BigDecimal("499.00"))
                .build();

        product4 = ProductEntity.builder()
                .id(AppTestConstants.PRODUCT_FOURTH_ID)
                .name("PlayStation 5")
                .description("Standard gaming console from Sony")
                .price(new BigDecimal("2449.99"))
                .build();

        inventoryList = List.of(product1, product2, product3);
    }

    @Test
    @DisplayName("findByNameOrDescription - Should find products when name matches ignoring case")
    void shouldFindProductsWhenNameMatchesIgnoringCase() {
        List<ProductEntity> result = productRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase("playstation", "playstation");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(AppTestConstants.PRODUCT_FIRST_ID);
        assertThat(result.get(0).getName()).isEqualTo("PlayStation 5 Slim");
    }

    @Test
    @DisplayName("findByNameOrDescription - Should find products when description matches ignoring case")
    void shouldFindProductsWhenDescriptionMatchesIgnoringCase() {
        List<ProductEntity> result = productRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase("PORTABLE", "PORTABLE");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(AppTestConstants.PRODUCT_SECOND_ID);
    }

    @Test
    @DisplayName("findByNameOrDescription - Should find multiple products when keyword matches different fields")
    void shouldFindMultipleProductsWhenKeywordIsBroad() {
        List<ProductEntity> result = productRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase("console", "console");

        assertThat(result).hasSize(3);
        assertThat(result).extracting(ProductEntity::getId)
                .containsExactlyInAnyOrder(AppTestConstants.PRODUCT_FIRST_ID, AppTestConstants.PRODUCT_SECOND_ID, AppTestConstants.PRODUCT_THIRD_ID);
    }
    
    @Test
    @DisplayName("findByNameOrDescription - Should return empty list when no match is found")
    void shouldReturnEmptyListWhenNoMatchIsFound() {
        List<ProductEntity> result = productRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase("Sega", "Sega");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Create - Debería guardar un nuevo inventario con éxito (Create)")
    void shouldSaveInventory() {
        ProductEntity savedEntity = productRepository.save(Objects.requireNonNull(product4));

        assertAll("Validar inserción de entidad",
                () -> assertNotNull(savedEntity),
                () -> assertEquals(AppTestConstants.PRODUCT_FOURTH_ID, savedEntity.getId()),
                () -> assertEquals(new BigDecimal("2449.99"), savedEntity.getPrice())
        );
    }

    @Test
    @DisplayName("Read - Debería buscar y encontrar un producto por su ID (Read)")
    void shouldFindProductById() {

        Optional<ProductEntity> foundEntityOpt = productRepository.findById(AppTestConstants.PRODUCT_FIRST_ID);

        assertTrue(foundEntityOpt.isPresent());
        assertEquals(new BigDecimal("499.99"), foundEntityOpt.get().getPrice());
    }

    @Test
    @DisplayName("Retrieve - Debería encontrar un elemento precargado de la lista inicial")
    void shouldFindPreloadedProductFromList() {
        targetId = Objects.requireNonNull(inventoryList.get(0).getId());

        Optional<ProductEntity> foundEntityOpt = productRepository.findById(targetId);

        assertTrue(foundEntityOpt.isPresent());
        assertEquals(new BigDecimal("499.99"), foundEntityOpt.get().getPrice());
    }

    @Test
    @DisplayName("Update - Debería actualizar el precio de un producto existente (Update)")
    void shouldUpdateProductPrice() {
        ProductEntity existingEntity = productRepository.findById(AppTestConstants.PRODUCT_SECOND_ID).orElseThrow();
        
        existingEntity.setPrice(new BigDecimal("1349.99"));

        ProductEntity updatedEntity = productRepository.saveAndFlush(existingEntity);

        assertEquals(new BigDecimal("1349.99"), updatedEntity.getPrice());
    }

    @Test
    @DisplayName("Delete - Debería eliminar un registro de productos por su ID (Delete)")
    void shouldDeleteProduct() {
        productRepository.deleteById(AppTestConstants.PRODUCT_SECOND_ID);
        
        Optional<ProductEntity> deletedEntityOpt = productRepository.findById(AppTestConstants.PRODUCT_SECOND_ID);

        assertFalse(deletedEntityOpt.isPresent());
    }
}