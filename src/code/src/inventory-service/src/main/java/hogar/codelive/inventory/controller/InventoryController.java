package hogar.codelive.inventory.controller;

import java.util.concurrent.CompletableFuture;

import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.lang.NonNull;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hogar.codelive.inventory.service.InventoryService;
import hogar.codelive.inventory.response.InventoryResponse;
import hogar.codelive.inventory.request.InventoryExistentRequest;
import hogar.codelive.inventory.request.InventoryNewProductRequest;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Operaciones de consulta de inventario")
public class InventoryController {
    private final InventoryService inventoryService;

    @GetMapping(value = "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtener el stock actual de un producto por su id")
    public CompletableFuture<ResponseEntity<InventoryResponse>> getStock(@NotBlank
                                                                         @Pattern(regexp = "^EXT-\\d{3}$")
                                                                         @Parameter(description = "Identificador del producto")
                                                                         @PathVariable @NonNull String productId) {
         return inventoryService.getStockByProductIdAsync(productId)
            .thenApply(ResponseEntity::ok);
    }
 
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Registrar nuevo stock de un producto existente")
    public CompletableFuture<ResponseEntity<InventoryResponse>> addNewProduct(@Valid @RequestBody InventoryNewProductRequest request) {
         return inventoryService.addNewInventoryProductAsync(request)
                .thenApply(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }
 
    @PutMapping(value = "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Actualizar el stock de un producto", description = "Actualiza la informacion de un producto existente.")
    public CompletableFuture<ResponseEntity<Void>> updateStockProduct(@NotBlank
                                                                      @Pattern(regexp = "^EXT-\\d{3}$")
                                                                      @Parameter(description = "Identificador del producto")
                                                                      @PathVariable @NonNull String productId,
                                                                      @Valid @RequestBody InventoryExistentRequest request) {
        return inventoryService.updateInventoryProductAsync(productId, request)
                .thenApply(response -> ResponseEntity.noContent().build());
    }
 
    @DeleteMapping(value = "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Eliminar un producto del inventario", description = "Elimina un producto existente del inventario.")
    public CompletableFuture<ResponseEntity<Void>> deleteStockProduct(@NotBlank
                                                                      @Pattern(regexp = "^EXT-\\d{3}$")
                                                                      @Parameter(description = "Identificador del producto")
                                                                      @PathVariable @NonNull String productId) {
         return inventoryService.deleteProductAsync(productId)
                .thenApply(voidResult -> ResponseEntity.noContent().build());
    }
}