package liverpool.codelive.products.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AppConstants {
    public final String ROLE_ADMIN = "ADMIN";
    public final String ROLE_USER = "USER";
    
    public final int MAX_ATTEMPTS_LOGIN = 3;
    public final String API_VERSION_V1 = "/api/v1";

    public final String MSG_EXCEPTION_INVALID_QUERY = "El parametro 'query' es obligatorio para realizar la busqueda.";
}
