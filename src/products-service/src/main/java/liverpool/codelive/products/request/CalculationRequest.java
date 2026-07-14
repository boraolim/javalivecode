package liverpool.codelive.products.request;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Datos para realizar un cálculo de inventario o costos")
public class CalculationRequest {

    @JsonProperty("valorA")
    @Schema(description = "Primer valor a sumar (ej. Stock Almacén Central)", example = "45")
    @NotNull(message = "El valorA no puede ser nulo")
    private int valorA;

    @JsonProperty("valorB")
    @Schema(description = "Segundo valor a sumar (ej. Stock Almacén Tienda)", example = "12")
    @NotNull(message = "El valorA no puede ser nulo")
    private int valorB;
}
