package hogar.codelive.externalws.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LogConstants {
    public static final String LOG_START_REQUEST                = "Starting request in: ({}) -> {}";
    public static final String LOG_RESPONSE_OK                  = "Finished OK in: ({}) -> {} | StatusCode: {} | Elapsed: {}ms | Query: {} | RequestBody: {} | ResponseBody: {}";
    public static final String LOG_RESPONSE_WARN                = "Finished WARN in: ({}) -> {} | StatusCode: {} | Elapsed: {}ms | Query: {} | RequestBody: {} | ResponseBody: {}";
    public static final String LOG_RESPONSE_ERROR               = "Finished ERROR in: ({}) -> {} | StatusCode: {} | Elapsed: {}ms | Query: {} | RequestBody: {} | ResponseBody: {} | Error: {}";

    public static final String LOG_START_OPERATION              = "Iniciando cálculo de números enteros. Valor A: [{}], Valor B: [{}]";
    public static final String LOG_MESSAGE_SUME                 = "El servicio se ha consumido correctamente (o se resolvió mediante contingencia). Resultado de la suma: [{}]";
    public static final String LOG_MESSAGE_MULTIPLY             = "El servicio se ha consumido correctamente (o se resolvió mediante contingencia). Resultado de la multiplicación: [{}]";
    public static final String LOG_MESSAGE_DIVIDE               = "El servicio se ha consumido correctamente (o se resolvió mediante contingencia). Resultado de la división: [{}]";

    public static final String LOG_START_COUNTRY_OPERATION      = "Iniciando la conexión al servicio externo de países.";
    public static final String LOG_MESSAGE_FULL_COUNTRY_ALL     = "El servicio se ha consumido correctamente. Información de los paises obtenida correctamente.";
    public static final String LOG_MESSAGE_COUNTRY_OK           = "El servicio se ha consumido correctamente. Información del país obtenida correctamente.";
}
