package hogar.codelive.products.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import jakarta.persistence.EntityNotFoundException;

import hogar.codelive.products.dto.InventoryDto;
import hogar.codelive.products.entity.ProductEntity;
import hogar.codelive.products.mapper.ProductMapper;
import hogar.codelive.products.enums.InventoryStatus;
import hogar.codelive.products.client.InventoryClient;
import hogar.codelive.products.constants.AppConstants;
import hogar.codelive.products.request.ProductNewRequest;
import hogar.codelive.products.request.ProductBatchRequest;
import hogar.codelive.products.repository.ProductRepository;
import hogar.codelive.products.request.ProductExistentRequest;
import hogar.codelive.products.response.EnrichedProductResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;  
    private final ProductMapper productMapper;
    private final InventoryClient inventoryClient;

    @Cacheable(value = "productSearchCache", key = "#query")
    public CompletableFuture<List<EnrichedProductResponse>> search(String query) {
        List<CompletableFuture<EnrichedProductResponse>> futures = productRepository
            .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query)
            .stream()
            .map(this::enrichAsync)
            .toList();

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
            .thenApply(values -> futures.stream()
                .map(CompletableFuture::join)
                .toList());
    }

    @Cacheable(value = "productSearchBatchCache", key = "#query")
    public CompletableFuture<List<EnrichedProductResponse>> searchBatch(String query) {
        List<ProductEntity> products = productRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);

        return Optional.of(products)
                .filter(list -> !list.isEmpty())
                .map(prods -> fetchAndBuildResponses(prods, query))
                .orElseGet(() -> CompletableFuture.completedFuture(List.of()));
    }

    @Cacheable(value = "productByIdCache", key = "#id")
    public CompletableFuture<EnrichedProductResponse> getProductId(String id) {
        return productRepository.findById(Objects.requireNonNull(id))
            .map(this::enrichAsync)
            .orElseGet(() -> CompletableFuture.failedFuture(new EntityNotFoundException("No existe un producto con id: " + id)));
    }

    @CacheEvict(value = {"productByIdCache", "productSearchCache"}, allEntries = true)
    public CompletableFuture<EnrichedProductResponse> addNewProductAsync(ProductNewRequest request) {
        return CompletableFuture.supplyAsync(() -> validateAndBuildEntity(request))
            .thenApply(productRepository::save)
            .thenApply(this::buildOnSaveResponseFromEntity);
    }

    @CacheEvict(value = {"productByIdCache", "productSearchCache"}, allEntries = true)
    public CompletableFuture<EnrichedProductResponse> updateProductAsync(@NonNull String productId,
                                                                         ProductExistentRequest request) {
        return CompletableFuture.supplyAsync(() -> validateAndUpdateEntity(productId, request))
            .thenApply(productRepository::save)
            .thenApply(this::buildOnSaveResponseFromEntity);
    }

    @CacheEvict(value = {"productByIdCache", "productSearchCache"}, allEntries = true)
    public CompletableFuture<Void> deleteProductAsync(@NonNull String productId) {
        return CompletableFuture.runAsync(() -> validateAndDelete(productId));
    }

    private CompletableFuture<EnrichedProductResponse> enrichAsync(ProductEntity product) {
        return inventoryClient
            .getStock(product.getId())
            .handle((inventory, ex) -> Optional.ofNullable(ex)
                .map(error -> unavailable(product))
                .orElseGet(() -> buildToResponseFromEntity(product, inventory)));
    }

    private EnrichedProductResponse unavailable(ProductEntity productEntity) {
        log.warn("No fue posible obtener el inventario para productId={}. Se marca como UNAVAILABLE.", productEntity.getId());
        return buildToResponseFromEntity(productEntity, null);
    }

    private EnrichedProductResponse buildToResponseFromEntity(ProductEntity product, InventoryDto inventoryDto) {
        EnrichedProductResponse response = productMapper.toEnrichedResponse(product);
        Optional<Integer> stockOpt = Optional.ofNullable(inventoryDto).map(InventoryDto::getStock);
        
        response.setStock(stockOpt.orElse(null));
        response.setInventoryStatus(stockOpt
            .map(valueEnum -> valueEnum > 0 ? InventoryStatus.IN_STOCK : InventoryStatus.OUT_OF_STOCK)
            .orElse(InventoryStatus.UNAVAILABLE));
        
        return response;
    }

    private ProductEntity validateAndBuildEntity(ProductNewRequest request) {
        return Optional.ofNullable(request)
            .map(productMapper::toDto)
            .map(productMapper::toEntity)
            .orElseThrow(() -> new IllegalArgumentException(AppConstants.MSG_NOT_FOUND));
    }

    private EnrichedProductResponse buildOnSaveResponseFromEntity(ProductEntity product) {
        return buildToResponseFromEntity(product, null);
    }

    private ProductEntity validateAndUpdateEntity(@NonNull String productId, ProductExistentRequest request) {
        return Optional.ofNullable(request)
            .map(updateRequest -> productRepository.findById(productId)
                    .map(productMapper::fromEntity)
                    .map(dto -> { productMapper.updateEntity(updateRequest, dto); return dto; })
                    .map(productMapper::toEntity)
                    .orElseThrow(() -> new EntityNotFoundException(String.format(AppConstants.MSG_INVENTORY_NOT_EXISTS, productId))))
            .orElseThrow(() -> new IllegalArgumentException(AppConstants.MSG_PRODUCT_NOT_NULL));
    }

    private void validateAndDelete(@NonNull String productId) {
        Optional.ofNullable(productId)
            .flatMap(productRepository::findById)
            .ifPresentOrElse(productRepository::delete, () -> { throw new EntityNotFoundException(String.format(AppConstants.MSG_INVENTORY_NOT_EXISTS, productId)); });
    }

    private List<EnrichedProductResponse> buildToResponseFromEntities(List<ProductEntity> products, 
                                                                      List<InventoryDto> inventoryDtos) {

        // Mapeo eficiente O(1) de inventarios por productId
        Map<String, InventoryDto> inventoryMap = inventoryDtos.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(InventoryDto::getProductId, dto -> dto,
                (existing, replacement) -> existing));

        return products.stream()
            .map(product -> {
                EnrichedProductResponse response = productMapper.toEnrichedResponse(product);
                InventoryDto inventoryDto = inventoryMap.get(product.getId());

                Optional<Integer> stockOpt = Optional.ofNullable(inventoryDto)
                    .map(InventoryDto::getStock);

                response.setStock(stockOpt.orElse(null));
                response.setInventoryStatus(stockOpt
                    .map(valueEnum -> valueEnum > 0 ? InventoryStatus.IN_STOCK : InventoryStatus.OUT_OF_STOCK)
                    .orElse(InventoryStatus.UNAVAILABLE));

                return response;
            })
            .toList();
    }

    private CompletableFuture<List<EnrichedProductResponse>> fetchAndBuildResponses(
            List<ProductEntity> products, String query) {
        
        List<String> productIds = products.stream()
                .map(ProductEntity::getId)
                .toList();

        ProductBatchRequest request = new ProductBatchRequest(productIds);

        return inventoryClient.getStockBatch(request)
                .handle((inventoryList, ex) -> resolveSafeInventory(inventoryList, ex, query))
                .thenApply(safeInventoryList -> buildToResponseFromEntities(products, safeInventoryList));
    }

    private List<InventoryDto> resolveSafeInventory(List<InventoryDto> inventoryList, Throwable ex, String query) {       
        return Optional.ofNullable(ex)
            .map(error -> {
                logWarning(query, error);
                return List.<InventoryDto>of();
            })
            .orElseGet(() -> Optional.ofNullable(inventoryList).orElse(List.of()));
    }

    private void logWarning(String query, Throwable ex) {
        log.warn("No fue posible obtener el inventario por lote para query={}. Motivo: {}. Se marcan como UNAVAILABLE.", 
                query, ex.getMessage());
    }
}
