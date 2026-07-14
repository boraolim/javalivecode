package liverpool.codelive.products.entity;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ProductEntity - Pruebas unitarias del POJO")
class ProductEntityTest {

    @Test
    @DisplayName("Debe crear una entidad usando el constructor vacío")
    void shouldCreateEmptyConstructor() {

        ProductEntity entity = new ProductEntity();

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isNull();
        assertThat(entity.getName()).isNull();
        assertThat(entity.getDescription()).isNull();
        assertThat(entity.getPrice()).isNull();
    }

    @Test
    @DisplayName("Debe crear una entidad usando el constructor completo")
    void shouldCreateAllArgsConstructor() {

        ProductEntity entity = new ProductEntity(
                "1",
                "Laptop",
                "Laptop Gamer",
                BigDecimal.valueOf(25000)
        );

        assertThat(entity.getId()).isEqualTo("1");
        assertThat(entity.getName()).isEqualTo("Laptop");
        assertThat(entity.getDescription()).isEqualTo("Laptop Gamer");
        assertThat(entity.getPrice()).isEqualByComparingTo("25000");
    }

    @Test
    @DisplayName("Debe asignar valores usando setters")
    void shouldSetValues() {

        ProductEntity entity = new ProductEntity();

        entity.setId("10");
        entity.setName("Mouse");
        entity.setDescription("Mouse inalámbrico");
        entity.setPrice(BigDecimal.valueOf(450));

        assertThat(entity.getId()).isEqualTo("10");
        assertThat(entity.getName()).isEqualTo("Mouse");
        assertThat(entity.getDescription()).isEqualTo("Mouse inalámbrico");
        assertThat(entity.getPrice()).isEqualByComparingTo("450");
    }

    @Test
    @DisplayName("Debe construir una entidad usando Builder")
    void shouldBuildEntity() {

        ProductEntity entity = ProductEntity.builder()
                .id("100")
                .name("Monitor")
                .description("27 pulgadas")
                .price(BigDecimal.valueOf(6500))
                .build();

        assertThat(entity.getId()).isEqualTo("100");
        assertThat(entity.getName()).isEqualTo("Monitor");
        assertThat(entity.getDescription()).isEqualTo("27 pulgadas");
        assertThat(entity.getPrice()).isEqualByComparingTo("6500");
    }

    @Test
    @DisplayName("Debe clonar la entidad usando toBuilder")
    void shouldCloneUsingToBuilder() {

        ProductEntity original = ProductEntity.builder()
                .id("1")
                .name("Teclado")
                .description("Mecánico")
                .price(BigDecimal.valueOf(900))
                .build();

        ProductEntity copy = original.toBuilder()
                .price(BigDecimal.valueOf(1200))
                .build();

        assertThat(copy.getId()).isEqualTo(original.getId());
        assertThat(copy.getName()).isEqualTo(original.getName());
        assertThat(copy.getDescription()).isEqualTo(original.getDescription());
        assertThat(copy.getPrice()).isEqualByComparingTo("1200");
    }

    @Test
    @DisplayName("Dos entidades iguales deben ser iguales")
    void shouldCompareEqualsAndHashCode() {

        ProductEntity entity1 = ProductEntity.builder()
                .id("1")
                .name("Producto")
                .description("Descripción")
                .price(BigDecimal.TEN)
                .build();

        ProductEntity entity2 = ProductEntity.builder()
                .id("1")
                .name("Producto")
                .description("Descripción")
                .price(BigDecimal.TEN)
                .build();

        assertThat(entity1)
                .isEqualTo(entity2)
                .hasSameHashCodeAs(entity2);
    }

    @Test
    @DisplayName("toString debe contener los datos de la entidad")
    void shouldGenerateToString() {

        ProductEntity entity = ProductEntity.builder()
                .id("5")
                .name("Tablet")
                .description("Android")
                .price(BigDecimal.valueOf(3500))
                .build();

        assertThat(entity.toString())
                .contains("Tablet")
                .contains("Android")
                .contains("3500");
    }
}