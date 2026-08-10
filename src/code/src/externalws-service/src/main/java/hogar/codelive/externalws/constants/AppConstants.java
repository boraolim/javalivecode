package hogar.codelive.externalws.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppConstants {   
    public static final String API_VERSION_V1              = "/api/v1";

    public static final String START_TIME_ATTR             = "HTTP_LOG_START_TIME";

    public static final String MSG_NOT_FOUND               = "El producto solicitado no existe en el inventario actual.";
    public static final String MSG_INTERNAL_ERROR          = "Ocurrió un error inesperado en el servidor.";
    public static final String MSG_VALIDATION_ERROR        = "La solicitud contiene campos inválidos o faltantes.";
    public static final String MSG_PRODUCT_NOT_NULL        = "El producto no puede ser nulo.";
    public static final String MSG_INVENTORY_NOT_EXISTS    = "No existe un producto con id: %s";

    public static final String MSG_EXCEPTION_INVALID_QUERY = "El parametro 'query' es obligatorio para realizar la busqueda.";
}
