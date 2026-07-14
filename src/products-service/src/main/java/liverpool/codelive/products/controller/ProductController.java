package liverpool.codelive.products.controller;

import java.util.List;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import liverpool.codelive.products.service.ProductService;
import liverpool.codelive.products.request.CalculationRequest;
import liverpool.codelive.products.response.EnrichedProductResponse;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Catalogo de productos enriquecido con inventario")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/search")
    @Operation(summary = "Buscar productos por nombre o descripcion, enriquecidos con stock actual")
    public ResponseEntity<List<EnrichedProductResponse>> search(
            @Parameter(description = "Palabra clave a buscar en nombre o descripcion")
            @RequestParam String query) {
        return ResponseEntity.ok(productService.search(query));
    }

    @PostMapping("/calculate-stock")
    @Operation(summary = "Calcular stock consolidado utilizando el motor SOAP externo")
    public ResponseEntity<Integer> calculateStock(@Valid @RequestBody(required = true) CalculationRequest request) {
        // Delegamos la petición directo al servicio de negocio
        int resultado = productService.getAddValue(request.getValorA(), request.getValorB());

        return ResponseEntity.ok(resultado);
    }
}