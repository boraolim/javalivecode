package hogar.codelive.inventory.request;

import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Datos para agregar stock existente a un identificador del producto existente.")
public class InventoryExistentRequest {
    @JsonProperty("productStock")
    @NotNull(message = "Stock requerido.")
    @Schema(description = "Product stock", example = "1300", minimum = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @Min(value = 1, message = "El valor minimo de stock debe ser mayor a 0.")    
    private Integer productStock;
}
