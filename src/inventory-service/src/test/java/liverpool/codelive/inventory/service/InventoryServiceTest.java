package liverpool.codelive.inventory.service;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import liverpool.codelive.inventory.entity.InventoryEntity;
import liverpool.codelive.inventory.response.InventoryResponse;
import liverpool.codelive.inventory.repository.InventoryRepository;
import liverpool.codelive.inventory.exception.InventoryNotFoundException;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
@DisplayName("InventoryServiceTests - Integration Tests")
class InventoryServiceTest {
    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        // Nothing to set up before each test for now, but this method can be used for common setup if needed in the future.
    }

    @Test
    void shouldReturnStockWhenProductExists() {
        InventoryEntity item = new InventoryEntity("EXT-001", 10);
        when(inventoryRepository.findById("EXT-001")).thenReturn(Optional.of(item));

        InventoryResponse response = inventoryService.getStockByProductId("EXT-001");

        assertThat(response.getProductId()).isEqualTo("EXT-001");
        assertThat(response.getStock()).isEqualTo(10);
    }

    @Test
    void shouldThrowExceptionWhenProductDoesNotExist() {
        when(inventoryRepository.findById("UNKNOWN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventoryService.getStockByProductId("UNKNOWN"))
                .isInstanceOf(InventoryNotFoundException.class)
                .hasMessageContaining("UNKNOWN");
    }
}
