package liverpool.codelive.products.repository;

import java.util.List;
import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import liverpool.codelive.products.entity.ProductEntity;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional // 🚀 Hace Rollback automático de los inserts después de cada test
@DisplayName("ProductRepository - Integration Tests")
class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    private EasyRandom easyRandom;

    private ProductEntity product1;
    private ProductEntity product2;
    private ProductEntity product3;

    @BeforeEach
    void setUpTestData() {
        // Limpiamos por seguridad
        productRepository.deleteAll();

        easyRandom = new EasyRandom();

        // 💡 Poblamos la base de datos con datos controlados para las búsquedas
        product1 = ProductEntity.builder()
                .id("PROD-001")
                .name("PlayStation 5 Slim")
                .description("Next-gen gaming console from Sony")
                .price(new BigDecimal("499.99"))
                .build();

        product2 = ProductEntity.builder()
                .id("PROD-002")
                .name("Nintendo Switch OLED")
                .description("Portable family console")
                .price(new BigDecimal("349.99"))
                .build();

        // Creamos un producto aleatorio para rellenar usando easyRandom
        product3 = easyRandom.nextObject(ProductEntity.class).toBuilder()
                .id("PROD-RAND")
                .name("Xbox Series X")
                .description("Powerful console with GamePass")
                .price(new BigDecimal("499.00"))
                .build();

        productRepository.saveAll(List.of(product1, product2, product3));


    }

    @Test
    @DisplayName("findByNameOrDescription - Should find products when name matches ignoring case")
    void shouldFindProductsWhenNameMatchesIgnoringCase() {
        // Act - Buscamos "playstation" en minúsculas
        List<ProductEntity> result = productRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase("playstation", "playstation");

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("PROD-001");
        assertThat(result.get(0).getName()).isEqualTo("PlayStation 5 Slim");
    }

    @Test
    @DisplayName("findByNameOrDescription - Should find products when description matches ignoring case")
    void shouldFindProductsWhenDescriptionMatchesIgnoringCase() {
        // Act - Buscamos "PORTABLE" en mayúsculas (debe coincidir con la descripción del Switch)
        List<ProductEntity> result = productRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase("PORTABLE", "PORTABLE");

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("PROD-002");
    }

    @Test
    @DisplayName("findByNameOrDescription - Should find multiple products when keyword matches different fields")
    void shouldFindMultipleProductsWhenKeywordIsBroad() {
        // Act - Buscamos "console" que aparece en la descripción del PS5, Switch y Xbox
        List<ProductEntity> result = productRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase("console", "console");

        // Assert
        assertThat(result).hasSize(3);
        assertThat(result).extracting(ProductEntity::getId)
                .containsExactlyInAnyOrder("PROD-001", "PROD-002", "PROD-RAND");
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
}
