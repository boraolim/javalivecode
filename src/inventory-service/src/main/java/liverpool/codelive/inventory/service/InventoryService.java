package liverpool.codelive.inventory.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import liverpool.codelive.inventory.response.InventoryResponse;
import liverpool.codelive.inventory.repository.InventoryRepository;
import liverpool.codelive.inventory.exception.InventoryNotFoundException;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public InventoryResponse getStockByProductId(String productId) {
        return inventoryRepository.findById(productId)
                .map(item -> new InventoryResponse(item.getProductId(), item.getStock()))
                .orElseThrow(() -> new InventoryNotFoundException(productId));
    }
}
