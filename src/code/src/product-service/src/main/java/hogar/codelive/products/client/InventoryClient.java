package hogar.codelive.products.client;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import lombok.extern.slf4j.Slf4j;

import reactor.core.publisher.Mono;

import org.slf4j.MDC;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import hogar.codelive.products.dto.InventoryDto;
import hogar.codelive.common.middleware.MiddlewareUtil;
import hogar.codelive.products.request.ProductBatchRequest;

@Slf4j
@Component
public class InventoryClient {
    private final WebClient webClient;

    public InventoryClient(WebClient.Builder webClientBuilder,
                           @Value("${inventory.service.base-url}") String baseUrl) {
        this.webClient = webClientBuilder
            .baseUrl(Objects.requireNonNull(baseUrl, "inventory.service.base-url no puede ser nulo o vacío"))
            .build();
    }

    @Retry(name = "inventoryService")
    @CircuitBreaker(name = "inventoryService", fallbackMethod = "fallbackInventory")
    @TimeLimiter(name = "inventoryService", fallbackMethod = "fallbackInventory")
    public CompletableFuture<InventoryDto> getStock(String productId) {
         Map<String, String> callerContext = MDC.getCopyOfContextMap();

        return webClient.get()
                .uri("/api/v1/inventory/{productId}", productId)
                .retrieve()
                .onStatus(status -> status.value() == 404, response -> Mono.empty())
                .bodyToMono(InventoryDto.class)
                .defaultIfEmpty(new InventoryDto(productId, null))
                .doOnError(ex -> MiddlewareUtil.restoreMdc(callerContext))
                .toFuture();
    }

    @Retry(name = "inventoryService")
    @CircuitBreaker(name = "inventoryService", fallbackMethod = "fallbackInventoryBatch")
    @TimeLimiter(name = "inventoryService", fallbackMethod = "fallbackInventoryBatch")
    public CompletableFuture<List<InventoryDto>> getStockBatch(ProductBatchRequest request) {
        Map<String, String> callerContext = MDC.getCopyOfContextMap();

        return webClient.post()
                .uri("/api/v1/inventory/batch") // Ajusta la ruta del nuevo endpoint batch
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status.value() == 404, response -> Mono.empty())
                .bodyToFlux(InventoryDto.class)
                .collectList()
                .defaultIfEmpty(List.of())
                .doOnError(ex -> MiddlewareUtil.restoreMdc(callerContext))
                .toFuture();
    }

    CompletableFuture<InventoryDto> fallbackInventory(String productId, Throwable ex) {
        return MiddlewareUtil.withMdcCleanup(() -> {
            log.warn("Fallback de inventario activado para productId={}. Motivo: {}", productId, ex.getMessage());
            return CompletableFuture.completedFuture(new InventoryDto(productId, null));
        });
    }

    CompletableFuture<List<InventoryDto>> fallbackInventoryBatch(Throwable ex) {
        return MiddlewareUtil.withMdcCleanup(() -> {
            log.warn("Fallback de inventario activado para la lista de poductos seleccionados. Motivo: {}", ex.getMessage());
            return CompletableFuture.completedFuture(List.of());
        });
    }
}
