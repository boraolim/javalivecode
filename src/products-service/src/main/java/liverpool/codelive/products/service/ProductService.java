package liverpool.codelive.products.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import liverpool.codelive.products.dto.InventoryDto;
import liverpool.codelive.products.entity.ProductEntity;
import liverpool.codelive.products.mapper.ProductMapper;
import liverpool.codelive.products.enums.InventoryStatus;
import liverpool.codelive.products.client.InventoryClient;
import liverpool.codelive.products.repository.ProductRepository;
import liverpool.codelive.products.external.SoapCalculatorClient;
import liverpool.codelive.products.response.EnrichedProductResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;  
    private final ProductMapper productMapper;
    private final InventoryClient inventoryClient;
    private final SoapCalculatorClient soapCalculatorClient;

    public List<EnrichedProductResponse> search(String query) {
        return productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query)
            .stream().map(this::enrichAsync)
            .map(CompletableFuture::join).toList();
    }

    public int getAddValue(int stockWarehouseA, int stockWarehouseB) {
        log.info("Iniciando cálculo de stock consolidado. Almacén A: [{}], Almacén B: [{}]", stockWarehouseA, stockWarehouseB);
    
        // Consumimos el cliente asíncrono y extraemos el entero de forma segura
        int resultado = soapCalculatorClient.ejecutarSuma(stockWarehouseA, stockWarehouseB).join();
    
        log.info("El servicio se ha consumido correctamente (o se resolvió mediante contingencia). Resultado de la suma: [{}]", resultado);
    
        return resultado;
    }

    private CompletableFuture<EnrichedProductResponse> enrichAsync(ProductEntity product) {
        return inventoryClient
                .getStock(product.getId())
                .handle((inventory, ex) -> Optional.ofNullable(ex)
                        .map(error -> unavailable(product))
                        .orElseGet(() -> buildResponse(product, inventory)));
    }

    private EnrichedProductResponse unavailable(ProductEntity productEntity) {
        log.warn("No fue posible obtener el inventario para productId={}. Se marca como UNAVAILABLE.");
        return buildResponse(productEntity, null);
    }

    private EnrichedProductResponse buildResponse(ProductEntity product, InventoryDto inventoryDto) {
        EnrichedProductResponse response = productMapper.toEnrichedResponse(product);
        Optional<Integer> stockOpt = Optional.ofNullable(inventoryDto).map(InventoryDto::getStock);
        
        response.setStock(stockOpt.orElse(null));
        response.setInventoryStatus(stockOpt
            .map(valueEnum -> valueEnum > 0 ? InventoryStatus.IN_STOCK : InventoryStatus.OUT_OF_STOCK)
            .orElse(InventoryStatus.UNAVAILABLE));
        
        return response;
    }
}
