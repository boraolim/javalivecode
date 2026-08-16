package hogar.codelive.products.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

public record ProductBatchRequest
(
    @NotEmpty(message = "La lista de productos no puede estar vacía")
    List<String> productIds
) { }
