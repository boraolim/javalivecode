package hogar.codelive.common.config;

import java.util.Optional;

import jakarta.xml.ws.BindingProvider;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class PortConfig {
    public static <T> T configurePort(T port, String url) {
        Optional.ofNullable(port)
            .filter(BindingProvider.class::isInstance)
            .map(BindingProvider.class::cast)
            .ifPresent(bp -> configureEndpoint(bp, url));

        return port;
    }

    private void configureEndpoint(BindingProvider bindingProvider, String url) {
        bindingProvider.getRequestContext()
            .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
    }
}
