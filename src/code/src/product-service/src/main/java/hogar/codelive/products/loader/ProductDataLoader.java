package hogar.codelive.products.loader;

import java.util.Set;
import java.util.List;
import java.io.InputStream;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.MappingIterator;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;

import hogar.codelive.products.entity.ProductEntity;
import hogar.codelive.products.mapper.ProductMapper;
import hogar.codelive.products.dto.ExternalProductDto;
import hogar.codelive.products.repository.ProductRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductDataLoader implements CommandLineRunner {
    
    private final @NonNull ProductRepository productRepository;
    private final @NonNull ProductMapper productMapper;
    private final @NonNull ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        try (InputStream is = new ClassPathResource("products-input.json").getInputStream()) {
            MappingIterator<ExternalProductDto> iterator = objectMapper.readerFor(ExternalProductDto.class)
                    .readValues(is);

            List<ProductEntity> incomingProducts = StreamSupport
                    .stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false)
                    .filter(ExternalProductDto::isActive)
                    .map(productMapper::toEntity)
                    .toList();

            if (incomingProducts.isEmpty()) {
                log.info("No hay productos activos para procesar en el archivo de carga inicial.");
                return;
            }

            // Enfoque funcional puro para detección, separación y persistencia selectiva
            Set<String> existingIds = fetchExistingIds(incomingProducts);
            
            List<ProductEntity> newProducts = filterNewProducts(incomingProducts, existingIds);

            log.info("Total de productos activos en el sistema: {}, Nuevos a insertar: {}, Omitidos por existir: {}", 
                    incomingProducts.size(), newProducts.size(), incomingProducts.size() - newProducts.size());

            persistNewProducts(newProducts);
        }
    }

    private Set<String> fetchExistingIds(List<ProductEntity> products) {
        List<String> ids = products.stream()
                .map(ProductEntity::getId)
                .toList();

        log.info("Total de ID's de artículos existentes en base de datos para: {}", ids.size());

        return productRepository.findAllById(ids).stream()
                .map(ProductEntity::getId)
                .collect(Collectors.toUnmodifiableSet());
    }

    private List<ProductEntity> filterNewProducts(List<ProductEntity> products, Set<String> existingIds) {
        return products.stream()
                .filter(product -> !existingIds.contains(product.getId()))
                .toList();
    }

    private void persistNewProducts(List<ProductEntity> newProducts) {
        if (newProducts.isEmpty()) {
            log.info("Todos los productos del archivo ya existen en la base de datos. Se omite la inserción.");
            return;
        }

        productRepository.saveAll(newProducts);
        log.info("Se cargaron exitosamente {} nuevos productos en la base de datos.", newProducts.size());
    }
}