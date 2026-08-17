package hogar.codelive.products.repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import hogar.codelive.products.BaseProductTest;
import hogar.codelive.products.entity.ProductEntity;
import hogar.codelive.products.constants.AppTestConstants;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@AutoConfigureTestDatabase
@DisplayName("ProductRepository - Integration Tests")
class ProductRepositoryTest extends BaseProductTest {
    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("BaseProducts - Should verify base test products configuration")
    void shouldVerifyBaseTestProducts() {
        // Assert
        assertThat(inventoryList).hasSize(3);
        assertThat(productFirst.getName()).isEqualTo("PlayStation 5 Slim");
    }

    @Test
    @DisplayName("findByNameOrDescription - Should find products when name matches ignoring case")
    void shouldFindProductsWhenNameMatchesIgnoringCase() {
        // Act
        List<ProductEntity> result = productRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase("playstation", "playstation");

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(AppTestConstants.PRODUCT_FIRST_ID);
        assertThat(result.get(0).getName()).isEqualTo("PlayStation 5 Slim");
    }

    @Test
    @DisplayName("findByNameOrDescription - Should find products when description matches ignoring case")
    void shouldFindProductsWhenDescriptionMatchesIgnoringCase() {
        // Act
        List<ProductEntity> result = productRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase("PORTABLE", "PORTABLE");

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(AppTestConstants.PRODUCT_SECOND_ID);
    }

    @Test
    @DisplayName("findByNameOrDescription - Should find multiple products when keyword matches different fields")
    void shouldFindMultipleProductsWhenKeywordIsBroad() {
        // Act
        List<ProductEntity> result = productRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase("console", "console");

        // Assert
        assertThat(result).hasSize(3);
        assertThat(result).extracting(ProductEntity::getId)
                .containsExactlyInAnyOrder(AppTestConstants.PRODUCT_FIRST_ID, AppTestConstants.PRODUCT_SECOND_ID, AppTestConstants.PRODUCT_THIRD_ID);
    }
    
    @Test
    @DisplayName("findByNameOrDescription - Should return empty list when no match is found")
    void shouldReturnEmptyListWhenNoMatchIsFound() {
        // Act
        List<ProductEntity> result = productRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase("Sega", "Sega");

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Create - Should save a new product inventory successfully")
    void shouldSaveInventory() {
        // Act
        ProductEntity savedEntity = productRepository.save(Objects.requireNonNull(productSeventh));

        // Assert
        assertAll("Validar inserción de entidad",
                () -> assertNotNull(savedEntity),
                () -> assertEquals(AppTestConstants.PRODUCT_SEVENTH_ID, savedEntity.getId()),
                () -> assertEquals(new BigDecimal("7830.20"), savedEntity.getPrice())
        );
    }

    @Test
    @DisplayName("Read - Should find and return product by its ID")
    void shouldFindProductById() {
        // Act
        Optional<ProductEntity> foundEntityOpt = productRepository.findById(AppTestConstants.PRODUCT_FIRST_ID);

        // Assert
        assertTrue(foundEntityOpt.isPresent());
        assertEquals(new BigDecimal("499.99"), foundEntityOpt.get().getPrice());
    }

    @Test
    @DisplayName("Retrieve - Should find a preloaded element from the initial list")
    void shouldFindPreloadedProductFromList() {
        // Arrange
        String targetId = Objects.requireNonNull(inventoryList.get(0).getId());

        // Act
        Optional<ProductEntity> foundEntityOpt = productRepository.findById(targetId);

        // Assert
        assertTrue(foundEntityOpt.isPresent());
        assertEquals(new BigDecimal("499.99"), foundEntityOpt.get().getPrice());
    }

    @Test
    @DisplayName("Update - Should update the price of an existing product")
    void shouldUpdateProductPrice() {
        // Arrange
        ProductEntity existingEntity = productRepository.findById(AppTestConstants.PRODUCT_SECOND_ID).orElseThrow();
        existingEntity.setPrice(new BigDecimal("1349.99"));

        // Act
        ProductEntity updatedEntity = productRepository.saveAndFlush(existingEntity);

        // Assert
        assertEquals(new BigDecimal("1349.99"), updatedEntity.getPrice());
    }

    @Test
    @DisplayName("Delete - Should delete a product record by its ID")
    void shouldDeleteProduct() {
        // Act
        productRepository.deleteById(AppTestConstants.PRODUCT_SECOND_ID);
        Optional<ProductEntity> deletedEntityOpt = productRepository.findById(AppTestConstants.PRODUCT_SECOND_ID);

        // Assert
        assertFalse(deletedEntityOpt.isPresent());
    }
}