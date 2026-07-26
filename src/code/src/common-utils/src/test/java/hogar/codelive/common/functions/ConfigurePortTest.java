package hogar.codelive.common.functions;

import java.util.Map;
import java.util.HashMap;
import jakarta.xml.ws.BindingProvider;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("ConfigurePort - Unit Tests")
class ConfigurePortTest {

    @Test
    @DisplayName("EXITO: Debería configurar la URL en el requestContext si el puerto implementa BindingProvider")
    void configurePort_cuandoEsBindingProvider_debeConfigurarUrl() {
        // Given
        String expectedUrl = "http://localhost:8080/soap-service";
        
        // Creamos un mock que implemente tanto una interfaz genérica simulada como BindingProvider
        BindingProviderMockPort portMock = mock(BindingProviderMockPort.class);
        Map<String, Object> contextMap = new HashMap<>();

        when(portMock.getRequestContext()).thenReturn(contextMap);

        // When
        BindingProviderMockPort result = ConfigurePort.configurePort(portMock, expectedUrl);

        // Then
        assertNotNull(result);
        assertSame(portMock, result);
        assertEquals(expectedUrl, contextMap.get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY));
    }

    @Test
    @DisplayName("EXITO: Debería retornar el objeto sin cambios si el puerto NO implementa BindingProvider")
    void configurePort_cuandoNoEsBindingProvider_debeRetornarSinModificar() {
        // Given
        String expectedUrl = "http://localhost:8080/soap-service";
        String plainObjectPort = "SimpleStringPortObject";

        // When
        String result = ConfigurePort.configurePort(plainObjectPort, expectedUrl);

        // Then
        assertNotNull(result);
        assertSame(plainObjectPort, result);
        assertEquals("SimpleStringPortObject", result);
    }

    // Interfaz auxiliar para simular un puerto SOAP que también actúa como BindingProvider
    private interface BindingProviderMockPort extends BindingProvider {
        // Métodos adicionales del puerto si fuesen necesarios
    }
}