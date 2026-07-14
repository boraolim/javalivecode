package liverpool.codelive.inventory.loader;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import liverpool.codelive.inventory.entity.InventoryEntity;
import liverpool.codelive.inventory.repository.InventoryRepository;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.anyList;

@ExtendWith(MockitoExtension.class)
@DisplayName("InventoryDataLoaderTest - Integration Tests")
class InventoryDataLoaderTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private InventoryDataLoader inventoryDataLoader;

    @Test
    @DisplayName("Debería omitir la carga de datos si el repositorio ya contiene registros")
    void shouldSkipLoadingWhenDataAlreadyExists() throws Exception {
        // Given
        when(inventoryRepository.count()).thenReturn(5L); // El repositorio ya tiene 5 registros

        // When
        inventoryDataLoader.run();

        // Then
        verify(inventoryRepository).count();
        // Verificamos que nunca se intente leer el archivo ni guardar datos
        verify(objectMapper, never()).readerFor(InventoryEntity.class);
        verify(inventoryRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("Debería leer el JSON e insertar los registros cuando el repositorio está vacío")
    void shouldLoadDataWhenRepositoryIsEmpty() throws Exception {
        // Given
        when(inventoryRepository.count()).thenReturn(0L); // Repositorio vacío

        // Usamos un ObjectMapper real para generar un ObjectReader real
        ObjectMapper realMapper = new ObjectMapper();
        ObjectReader realReader = realMapper.readerFor(InventoryEntity.class);

        // Hacemos que el mock de tu ObjectMapper devuelva el Reader real
        when(objectMapper.readerFor(InventoryEntity.class)).thenReturn(realReader);

        // When
        inventoryDataLoader.run();

        // Then
        verify(inventoryRepository).count();
        verify(objectMapper).readerFor(InventoryEntity.class);
        verify(inventoryRepository).saveAll(anyList());
    }
}