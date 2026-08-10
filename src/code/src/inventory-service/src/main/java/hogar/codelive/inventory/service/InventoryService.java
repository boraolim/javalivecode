package hogar.codelive.inventory.service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import lombok.RequiredArgsConstructor;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import jakarta.persistence.EntityNotFoundException;

import hogar.codelive.inventory.constants.AppConstants;
import hogar.codelive.inventory.entity.InventoryEntity;
import hogar.codelive.inventory.mapper.InventoryMapper;
import hogar.codelive.inventory.response.InventoryResponse;
import hogar.codelive.inventory.repository.InventoryRepository;
import hogar.codelive.inventory.request.InventoryExistentRequest;
import hogar.codelive.inventory.request.InventoryNewProductRequest;
import hogar.codelive.inventory.exception.InventoryNotFoundException;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;

    @Cacheable(value = "inventoryByIdCache", key = "#productId")
    public CompletableFuture<InventoryResponse> getStockByProductIdAsync(@NonNull String productId) {
        return CompletableFuture.supplyAsync(() -> inventoryRepository.findById(productId))
            .thenApply(optionalEntity -> validateAndGet(optionalEntity, productId))
            .thenApply(this::buildToResponse);
    }

    @CacheEvict(value = {"inventorySearchCache", "inventoryByIdCache"}, allEntries = true)
    public CompletableFuture<InventoryResponse> addNewInventoryProductAsync(InventoryNewProductRequest request) {
        return CompletableFuture.supplyAsync(() -> validateAndBuildEntity(request))
            .thenApply(inventoryRepository::save)
            .thenApply(this::buildToResponseFromEntity);
    }

    @CacheEvict(value = {"inventorySearchCache", "inventoryByIdCache"}, allEntries = true)
    public CompletableFuture<InventoryResponse> updateInventoryProductAsync(@NonNull String productId,
                                                                            InventoryExistentRequest request) {
        return CompletableFuture.supplyAsync(() -> validateAndUpdateEntity(productId, request))
            .thenApply(inventoryRepository::save)
            .thenApply(this::buildToResponseFromEntity);
    }

    @CacheEvict(value = {"inventorySearchCache", "inventoryByIdCache"}, allEntries = true)
    public CompletableFuture<Void> deleteProductAsync(@NonNull String productId) {
        return CompletableFuture.runAsync(() -> validateAndDelete(productId));
    }

    // Funciones privadas.
    private InventoryEntity validateAndGet(Optional<InventoryEntity> optionalInventory, String productId) {
        return Optional.ofNullable(optionalInventory)
            .flatMap(optEntity -> optEntity)
            .orElseThrow(() ->  new InventoryNotFoundException(String.format(AppConstants.MSG_PRODUCT_ID_NOT_FOUND, productId)));
    }

    private InventoryResponse buildToResponse(InventoryEntity entity) {
        return Optional.ofNullable(entity)
            .map(inventoryMapper::fromEntity)
            .map(dto -> new InventoryResponse(dto.getProductId(), dto.getStock()))
            .orElseThrow(() -> new InventoryNotFoundException(AppConstants.MSG_PRODUCT_NOT_NULL));
    }

    private InventoryEntity validateAndBuildEntity(InventoryNewProductRequest request) {
        return Optional.ofNullable(request)
            .map(inventoryMapper::toDto)
            .map(inventoryMapper::toEntity)
            .orElseThrow(() -> new IllegalArgumentException(AppConstants.MSG_PRODUCT_NOT_NULL));
    }

    private InventoryResponse buildToResponseFromEntity(InventoryEntity entity) {
        return inventoryMapper.fromDto(inventoryMapper.fromEntity(entity));
    }

    private InventoryEntity validateAndUpdateEntity(@NonNull String productId, InventoryExistentRequest request) {
        return Optional.ofNullable(request)
            .map(updateRequest -> inventoryRepository.findById(productId)
                    .map(inventoryMapper::fromEntity)
                    .map(dto -> { inventoryMapper.updateEntity(updateRequest, dto); return dto; })
                    .map(inventoryMapper::toEntity)
                    .orElseThrow(() -> new EntityNotFoundException(String.format(AppConstants.MSG_INVENTORY_NOT_EXISTS, productId))))
            .orElseThrow(() -> new IllegalArgumentException(AppConstants.MSG_PRODUCT_NOT_NULL));
    }

    private void validateAndDelete(@NonNull String productId) {
        Optional.ofNullable(productId)
            .flatMap(inventoryRepository::findById)
            .ifPresentOrElse(inventoryRepository::delete, () -> { throw new EntityNotFoundException(String.format(AppConstants.MSG_INVENTORY_NOT_EXISTS, productId)); });
    }
}