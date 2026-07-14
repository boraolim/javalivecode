package liverpool.codelive.inventory.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import liverpool.codelive.inventory.service.InventoryService;
import liverpool.codelive.inventory.response.InventoryResponse;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Operaciones de consulta de inventario")
public class InventoryController {
    private final InventoryService inventoryService;

    @GetMapping("/{productId}")
    @Operation(summary = "Obtener el stock actual de un producto por su id")
    public ResponseEntity<InventoryResponse> getStock(@PathVariable String productId) {
        return ResponseEntity.ok(inventoryService.getStockByProductId(productId));
    }   
}
