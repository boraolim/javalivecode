package hogar.codelive.inventory.loader;

import java.io.InputStream;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.MappingIterator;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;

import hogar.codelive.inventory.entity.InventoryEntity;
import hogar.codelive.inventory.repository.InventoryRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryDataLoader implements CommandLineRunner {
    private final @NonNull InventoryRepository inventoryRepository;
    private final @NonNull ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        try (InputStream is = new ClassPathResource("inventory-input.json").getInputStream()) {
            MappingIterator<InventoryEntity> iterator = objectMapper.readerFor(InventoryEntity.class)
                    .readValues(is);

            StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false)
                .filter(item -> item.getStock() != null && item.getStock() > 0)
                .map(this::processInventoryItem)
                .forEach(inventoryRepository::save);

            log.info("Proceso de carga y actualización del inventario completado.");
        }
    }

    private InventoryEntity processInventoryItem(InventoryEntity newItem) {
        return inventoryRepository.findById(Objects.requireNonNull(newItem.getProductId()))
            .map(existingItem -> updateExistingItem(existingItem, newItem))
            .orElseGet(() -> createNewItem(newItem));
    }

    private InventoryEntity updateExistingItem(InventoryEntity existingItem, InventoryEntity newItem) {
        existingItem.setStock(existingItem.getStock() + newItem.getStock());
        log.info("Actualizando stock para ID {}: nuevo total {}", existingItem.getProductId(), existingItem.getStock());
        return existingItem;
    }

    private InventoryEntity createNewItem(InventoryEntity newItem) {
        log.info("Registrando nuevo artículo con ID {} y stock {}", newItem.getProductId(), newItem.getStock());
        return newItem;
    }
}
