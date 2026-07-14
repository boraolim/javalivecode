package liverpool.codelive.products.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ExternalProductDto - Unit Tests")
class ExternalProductDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Should create object using builder and verify getters")
    void shouldCreateObjectUsingBuilder() {
        // Act
        ExternalProductDto dto = ExternalProductDto.builder()
                .id("PROD-123")
                .title("Console")
                .description("Next-gen gaming")
                .price("499.99")
                .currency("USD")
                .category("Electronics")
                .active(true)
                .build();

        // Assert
        assertThat(dto.getId()).isEqualTo("PROD-123");
        assertThat(dto.getTitle()).isEqualTo("Console");
        assertThat(dto.getDescription()).isEqualTo("Next-gen gaming");
        assertThat(dto.getPrice()).isEqualTo("499.99");
        assertThat(dto.getCurrency()).isEqualTo("USD");
        assertThat(dto.getCategory()).isEqualTo("Electronics");
        assertThat(dto.isActive()).isTrue();
    }

    @Test
    @DisplayName("Should clone object using toBuilder()")
    void shouldCloneObjectUsingToBuilder() {
        // Arrange
        ExternalProductDto original = ExternalProductDto.builder()
                .id("PROD-123")
                .title("Old Title")
                .active(false)
                .build();

        // Act
        ExternalProductDto clone = original.toBuilder()
                .title("New Title")
                .active(true)
                .build();

        // Assert
        assertThat(clone.getId()).isEqualTo("PROD-123"); // Se mantiene intacto
        assertThat(clone.getTitle()).isEqualTo("New Title"); // Se actualizó
        assertThat(clone.isActive()).isTrue(); // Se actualizó
    }

    @Test
    @DisplayName("Should verify setters and getters (Lombok @Data)")
    void shouldVerifySettersAndGetters() {
        // Arrange
        ExternalProductDto dto = new ExternalProductDto();

        // Act
        dto.setId("PROD-99");
        dto.setTitle("Phone");
        dto.setDescription("Smartphone");
        dto.setPrice("999.00");
        dto.setCurrency("MXN");
        dto.setCategory("Mobiles");
        dto.setActive(true);

        // Assert
        assertThat(dto.getId()).isEqualTo("PROD-99");
        assertThat(dto.getTitle()).isEqualTo("Phone");
        assertThat(dto.getDescription()).isEqualTo("Smartphone");
        assertThat(dto.getPrice()).isEqualTo("999.00");
        assertThat(dto.getCurrency()).isEqualTo("MXN");
        assertThat(dto.getCategory()).isEqualTo("Mobiles");
        assertThat(dto.isActive()).isTrue();
    }

    @Test
    @DisplayName("Should verify equals, hashCode and toString methods")
    void shouldVerifyEqualsHashCodeAndToString() {
        // Arrange
        ExternalProductDto dto1 = ExternalProductDto.builder().id("1").title("A").build();
        ExternalProductDto dto2 = ExternalProductDto.builder().id("1").title("A").build();
        ExternalProductDto dto3 = ExternalProductDto.builder().id("2").title("B").build();

        // Assert
        assertThat(dto1)
                .isEqualTo(dto2)
                .isNotEqualTo(dto3)
                .hasSameHashCodeAs(dto2);
        
        assertThat(dto1.toString()).contains("id=1", "title=A");
    }

    @Test
    @DisplayName("Should deserialize JSON correctly respecting @JsonProperty('id')")
    void shouldDeserializeJsonRespectingAnnotations() throws Exception {
        // Arrange
        String json = "{"
                + "\"id\":\"PROD-JACKSON\","
                + "\"title\":\"Smart TV\","
                + "\"description\":\"4K Resolution\","
                + "\"price\":\"599.99\","
                + "\"currency\":\"USD\","
                + "\"category\":\"Electronics\","
                + "\"active\":true"
                + "}";

        // Act
        ExternalProductDto result = objectMapper.readValue(json, ExternalProductDto.class);

        // Assert
        assertThat(result.getId()).isEqualTo("PROD-JACKSON");
        assertThat(result.getTitle()).isEqualTo("Smart TV");
        assertThat(result.isActive()).isTrue();
    }
}