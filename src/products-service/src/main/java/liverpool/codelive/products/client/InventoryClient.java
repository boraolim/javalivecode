package liverpool.codelive.products.client;

import java.util.concurrent.CompletableFuture;

import lombok.extern.slf4j.Slf4j;

import reactor.core.publisher.Mono;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import liverpool.codelive.products.dto.InventoryDto;

@Slf4j
@Component
public class InventoryClient {
    private final WebClient webClient;

    public InventoryClient(WebClient.Builder webClientBuilder,
                           @Value("${inventory.service.base-url}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    @Retry(name = "inventoryService")
    @CircuitBreaker(name = "inventoryService", fallbackMethod = "fallbackInventory")
    @TimeLimiter(name = "inventoryService", fallbackMethod = "fallbackInventory")
    public CompletableFuture<InventoryDto> getStock(String productId) {
        return webClient.get()
                .uri("/api/v1/inventory/{productId}", productId)
                .retrieve()
                .onStatus(status -> status.value() == 404, response -> Mono.empty())
                .bodyToMono(InventoryDto.class)
                .defaultIfEmpty(new InventoryDto(productId, null))
                .toFuture();
    }

    CompletableFuture<InventoryDto> fallbackInventory(String productId, Throwable ex) {
        log.warn("Fallback de inventario activado para productId={}. Motivo: {}", productId, ex.getMessage());
        return CompletableFuture.completedFuture(new InventoryDto(productId, null));
    }
}
