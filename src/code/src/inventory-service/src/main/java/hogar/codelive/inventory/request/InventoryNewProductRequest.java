package hogar.codelive.inventory.request;

import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Datos para agregar stock existente a un identificador del producto.")
public class InventoryNewProductRequest {

    @JsonProperty("productId")
    @Schema(description = "Product id.", example = "PROD-001", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Identificador de producto requerido.")
    @Pattern(regexp = "^EXT-\\d{3}$", message = "El identificador solo puede contener letras, números, guion y guion bajo.")
    private String idProduct;

    @JsonProperty("productStock")
    @NotNull(message = "Stock requerido.")
    @Schema(description = "Product stock", example = "1300", minimum = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @Min(value = 1, message = "El valor minimo de stock debe ser mayor a 0.")    
    private Integer productStock;
}
