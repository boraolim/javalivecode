package liverpool.codelive.products.loader;

import java.util.List;
import java.io.InputStream;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.MappingIterator;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;

import liverpool.codelive.products.entity.ProductEntity;
import liverpool.codelive.products.mapper.ProductMapper;
import liverpool.codelive.products.dto.ExternalProductDto;
import liverpool.codelive.products.repository.ProductRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductDataLoader implements CommandLineRunner{
    
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() > 0) {
            log.info("El catalogo de productos ya tiene datos cargados, se omite la carga inicial.");
            return;
        }

        try (InputStream is = new ClassPathResource("products-input.json").getInputStream()) {
            MappingIterator<ExternalProductDto> iterator = objectMapper.readerFor(ExternalProductDto.class)
                    .readValues(is);

            List<ProductEntity> products = StreamSupport
                    .stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false)
                    .filter(ExternalProductDto::isActive)
                    .map(productMapper::toEntity)
                    .toList();

            productRepository.saveAll(products);
            log.info("Se cargaron {} productos en MariaDB (mapeados con MapStruct).", products.size());
        }
    }
}
