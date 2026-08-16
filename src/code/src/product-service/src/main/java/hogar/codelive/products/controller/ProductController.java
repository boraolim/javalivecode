package hogar.codelive.products.controller;

import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hogar.codelive.products.service.ProductService;
import hogar.codelive.products.request.ProductNewRequest;
import hogar.codelive.products.request.ProductExistentRequest;
import hogar.codelive.products.response.EnrichedProductResponse;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Catalogo de productos enriquecido con inventario")
public class ProductController {
    private final ProductService productService;

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Buscar productos por nombre o descripcion, enriquecidos con stock actual")
    public CompletableFuture<ResponseEntity<List<EnrichedProductResponse>>> search(@Parameter(description = "Palabra clave a buscar en nombre o descripcion")
                                                                                   @RequestParam String query) {
        return productService.search(query).thenApply(ResponseEntity::ok);
    }

    @GetMapping(value = "/searchbatch", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Buscar productos por nombre o descripcion, enriquecidos con stock actual")
    public CompletableFuture<ResponseEntity<List<EnrichedProductResponse>>> searchbatch(@Parameter(description = "Palabra clave a buscar en nombre o descripcion")
                                                                                        @RequestParam String query) {
        return productService.searchBatch(query).thenApply(ResponseEntity::ok);
    }


    @GetMapping(value = "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtener el articulo actual de un producto por su id")
    public CompletableFuture<ResponseEntity<EnrichedProductResponse>> getProduct(@NotBlank
                                                                                 @Pattern(regexp = "^EXT-\\d{3}$")
                                                                                 @Parameter(description = "Identificador del producto")
                                                                                 @PathVariable @NonNull String productId) {
         return productService.getProductId(productId).thenApply(ResponseEntity::ok);
    }
 
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Registrar un nuevo producto en el catalogo.")
    public CompletableFuture<ResponseEntity<EnrichedProductResponse>> addNewProduct(@Valid @RequestBody ProductNewRequest request) {
         return productService.addNewProductAsync(request)
            .thenApply(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }
 
    @PutMapping(value = "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Actualizar la información de un producto existente.", description = "Actualiza la informacion de un producto existente.")
    public CompletableFuture<ResponseEntity<Void>> updateStockProduct(@NotBlank
                                                                      @Pattern(regexp = "^EXT-\\d{3}$")
                                                                      @Parameter(description = "Identificador del producto")
                                                                      @PathVariable @NonNull String productId,
                                                                      @Valid @RequestBody ProductExistentRequest request) {
        return productService.updateProductAsync(productId, request)
            .thenApply(response -> ResponseEntity.noContent().build());
    }
 
    @DeleteMapping(value = "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Eliminar un producto del catalogo.", description = "Elimina un producto existente del catalogo.")
    public CompletableFuture<ResponseEntity<Void>> deleteStockProduct(@NotBlank
                                                                      @Pattern(regexp = "^EXT-\\d{3}$")
                                                                      @Parameter(description = "Identificador del producto")
                                                                      @PathVariable @NonNull String productId) {
         return productService.deleteProductAsync(productId)
                .thenApply(voidResult -> ResponseEntity.noContent().build());
    }
}
