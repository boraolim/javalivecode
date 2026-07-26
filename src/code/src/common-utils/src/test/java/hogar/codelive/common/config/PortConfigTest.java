package hogar.codelive.common.config;

import java.util.Map;
import java.util.Collections;
import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import jakarta.xml.ws.BindingProvider;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("PortConfigTest - Unit Tests")
class PortConfigTest {

    @Test
    void shouldConfigureEndpointWhenPortIsBindingProvider() {
        BindingProvider bindingProvider = mock(BindingProvider.class);

        Map<String, Object> context = new HashMap<>();

        when(bindingProvider.getRequestContext()).thenReturn(context);

        Object result = PortConfig.configurePort(bindingProvider, "http://localhost");

        assertSame(bindingProvider, result);
        assertEquals("http://localhost", context.get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY));
    }

    @Test
    void shouldReturnSameInstance() {
        BindingProvider bindingProvider = mock(BindingProvider.class);

        when(bindingProvider.getRequestContext()).thenReturn(new HashMap<>());

        Object result = PortConfig.configurePort(bindingProvider, "url");

        assertSame(bindingProvider, result);
    }

    @Test
    void shouldReturnPortWithoutConfigurationWhenNotBindingProvider() {

        Object port = new Object();

        Object result = PortConfig.configurePort(port, "url");

        assertSame(port, result);
    }

    @Test
    void shouldStoreNullEndpoint() {

        BindingProvider bindingProvider = mock(BindingProvider.class);

        Map<String, Object> context = new HashMap<>();

        when(bindingProvider.getRequestContext()).thenReturn(context);

        PortConfig.configurePort(bindingProvider, null);

        assertTrue(context.containsKey(BindingProvider.ENDPOINT_ADDRESS_PROPERTY));

        assertNull(context.get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY));
    }

    @Test
    void shouldPropagateExceptionWhenGettingRequestContextFails() {

        BindingProvider bindingProvider = mock(BindingProvider.class);

        when(bindingProvider.getRequestContext()).thenThrow(new RuntimeException("Error"));

        assertThrows(RuntimeException.class, () -> PortConfig.configurePort(bindingProvider, "url"));
    }

    @Test
    void shouldPropagateExceptionWhenContextCannotBeModified() {

        BindingProvider bindingProvider = mock(BindingProvider.class);

        Map<String, Object> context = Collections.unmodifiableMap(new HashMap<>());

        when(bindingProvider.getRequestContext()).thenReturn(context);

        assertThrows(UnsupportedOperationException.class,
            () -> PortConfig.configurePort(bindingProvider, "url"));
    }
}
