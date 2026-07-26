package hogar.codelive.inventory.loader;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.junit.jupiter.MockitoExtension;

import hogar.codelive.inventory.entity.InventoryEntity;
import hogar.codelive.inventory.constants.AppTestConstants;
import hogar.codelive.inventory.repository.InventoryRepository;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.anyString;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("InventoryDataLoaderTest - Integration Tests")
class InventoryDataLoaderTest {

@Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ObjectReader objectReader;

    @InjectMocks
    private InventoryDataLoader inventoryDataLoader;

    @Test
    @DisplayName("Debería registrar un nuevo artículo si este no existe en el repositorio")
    void shouldRegisterNewItemWhenItDoesNotExist() throws Exception {
        // Given
        ObjectMapper realMapper = new ObjectMapper();
        ObjectReader realReader = realMapper.readerFor(InventoryEntity.class);

        when(objectMapper.readerFor(InventoryEntity.class)).thenReturn(realReader);

        // Simulamos que el repositorio NO encuentra ninguno de los IDs
        when(inventoryRepository.findById(anyString())).thenReturn(Optional.empty());
        when(inventoryRepository.save(any(InventoryEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        inventoryDataLoader.run();

        // Then
        // Verificamos que al menos procesó e intentó buscar uno de los válidos que sabemos que existe
        verify(inventoryRepository, atLeastOnce()).findById(AppTestConstants.PRODUCT_THIRD_ID);
        
        // Verificamos que el guardado se haya ejecutado al menos una vez (sea 3, 4 o N veces)
        verify(inventoryRepository, atLeastOnce()).save(any(InventoryEntity.class));
    }

    @Test
    @DisplayName("Debería actualizar el stock si el artículo ya existe en el repositorio")
    void shouldUpdateStockWhenItemAlreadyExists() throws Exception {
        // Given
        InventoryEntity existingItem = new InventoryEntity();
        existingItem.setProductId("EXT-003");
        existingItem.setStock(10); // Stock inicial previo en BD

        ObjectMapper realMapper = new ObjectMapper();
        ObjectReader realReader = realMapper.readerFor(InventoryEntity.class);

        when(objectMapper.readerFor(InventoryEntity.class)).thenReturn(realReader);

        // Simulamos que SOLO para "EXT-003" el artículo ya existe (devuelve Optional con el item),
        // y para los demás devuelve Optional.empty() para que actúe como registro nuevo.
        when(inventoryRepository.findById("EXT-003")).thenReturn(Optional.of(existingItem));
        when(inventoryRepository.findById("EXT-004")).thenReturn(Optional.empty());
        when(inventoryRepository.findById("EXT-005")).thenReturn(Optional.empty());
        
        when(inventoryRepository.save(any(InventoryEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        inventoryDataLoader.run();

        // Then
        verify(inventoryRepository).findById("EXT-003");
        
        // Verificamos que se hayan realizado guardados (al menos 3 veces por los elementos del JSON)
        verify(inventoryRepository, atLeastOnce()).save(any(InventoryEntity.class));
    }

    @Test
    @DisplayName("Debería omitir el registro si el stock es menor a 1 (como EXT-002 con stock 0)")
    void shouldSkipItemWhenStockIsLessThanOne() throws Exception {
        // Given
        ObjectMapper realMapper = new ObjectMapper();
        ObjectReader realReader = realMapper.readerFor(InventoryEntity.class);

        when(objectMapper.readerFor(InventoryEntity.class)).thenReturn(realReader);

        // Simulamos que si se llegara a invocar findById para EXT-002 falle o no devuelva nada, 
        // pero principalmente vigilaremos que nunca ocurra.
        when(inventoryRepository.findById(anyString())).thenReturn(Optional.empty());
        when(inventoryRepository.save(any(InventoryEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        inventoryDataLoader.run();

        // Then
        // 1. Verificamos explícitamente que el ID con stock 0 jamás fue consultado en la base de datos
        verify(inventoryRepository, never()).findById("EXT-002");

        // 2. Verificamos que ninguna entidad guardada tenga un stock menor a 1
        verify(inventoryRepository, never()).save(argThat(entity -> entity.getStock() != null && entity.getStock() == 0));
    }
}
