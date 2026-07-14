package liverpool.codelive.inventory.loader;

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

import liverpool.codelive.inventory.entity.InventoryEntity;
import liverpool.codelive.inventory.repository.InventoryRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryDataLoader implements CommandLineRunner {
    private final InventoryRepository inventoryRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        if (inventoryRepository.count() > 0) {
            log.info("El inventario ya tiene datos cargados, se omite la carga inicial.");
            return;
        }

        try (InputStream is = new ClassPathResource("inventory-input.json").getInputStream()) {
            MappingIterator<InventoryEntity> iterator = objectMapper.readerFor(InventoryEntity.class)
                    .readValues(is);

            List<InventoryEntity> items = StreamSupport
                    .stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false)
                    .toList();

            inventoryRepository.saveAll(items);
            log.info("Se cargaron {} registros de inventario en H2.", items.size());
        }
    }
}
