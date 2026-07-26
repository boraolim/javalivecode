package hogar.codelive.products.request;

import lombok.Data;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Datos para actualizar un producto existente")
public class ProductExistentRequest {
    @JsonProperty("nameProduct")
    @Schema(description = "Name of product", example = "Laptop Lenovo", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Nombre del producto requerido.")
    @Size(max = 255, message = "El nombre del producto no puede exceder 255 caracteres.")
    @Pattern(regexp = "^[\\p{L}\\p{N} ]+$", message = "El nombre del producto solo puede contener letras, números y espacios.")
    private String nameProduct;

    @JsonProperty("descriptionProduct")
    @Schema(description = "Product detail", example = "Laptop Lenovo ThinkPad con 16 GB de RAM", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "La descripción del producto no puede ser nulo.")
    @Size(max = 255, message = "La descripción del producto no puede exceder 255 caracteres.")
    @Pattern(regexp = "^[\\p{L}\\p{N} ]+$", message = "La descripción del producto solo puede contener letras, números y espacios.")
    private String descriptionProduct;

    @JsonProperty("priceProduct")
    @Schema(description = "Price product", example = "1299.99", minimum = "0.01", requiredMode = Schema.RequiredMode.REQUIRED)
    @DecimalMin(value = "0.01", inclusive = true, message = "El precio del producto debe ser mayor a 0.")
    private BigDecimal priceProduct;
}
