package liverpool.codelive.inventory.controller;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import liverpool.codelive.inventory.entity.InventoryEntity;
import liverpool.codelive.inventory.service.InventoryService;
import liverpool.codelive.inventory.repository.InventoryRepository;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
@DisplayName("InventoryControllerTest - Integration Tests")
class InventoryControllerTest {
    private MockMvc mockMvc;

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private InventoryEntity mockEntity;

    @BeforeEach
    void setUp() {
        // Inicializamos MockMvc asociándolo al controlador que consume el productService real
        InventoryController inventoryController = new InventoryController(inventoryService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(inventoryController).build();

        mockEntity = InventoryEntity.builder()
                .productId("PROD-100")
                .stock(15)
                .build();               
    }

    @Test
    @DisplayName("Debería retornar 200 OK y el stock cuando el producto existe")
    void shouldReturnStockWhenProductExists() throws Exception {
        // Given - Solo mockeamos el repositorio (el servicio real usará este mock)
        when(inventoryRepository.findById("PROD-100")).thenReturn(Optional.of(mockEntity));

        // When & Then
        mockMvc.perform(get("/api/v1/inventory/{productId}", "PROD-100")
                        .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.productId").value("PROD-100"))
            .andExpect(jsonPath("$.stock").value(15)); // O $.stock, dependiendo de tu DTO

        // Verificamos que el flujo llegó al repositorio a través del servicio
        verify(inventoryRepository).findById("PROD-100");
    }
}