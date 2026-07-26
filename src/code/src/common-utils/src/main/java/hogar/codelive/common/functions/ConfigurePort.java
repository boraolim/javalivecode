package hogar.codelive.common.functions;

import lombok.experimental.UtilityClass;

import jakarta.xml.ws.BindingProvider;

@UtilityClass
public final class ConfigurePort {

    public static <T> T configurePort(T port, String url) {
        if (port instanceof BindingProvider bp) {
            bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
        }
        return port;
    }
}
