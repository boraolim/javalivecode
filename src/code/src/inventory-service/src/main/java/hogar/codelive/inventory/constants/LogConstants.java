package hogar.codelive.inventory.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LogConstants {
    public static final String LOG_START_REQUEST            = "Starting request in: ({}) -> {}";
    public static final String LOG_RESPONSE_OK              = "Finished OK in: ({}) -> {} | StatusCode: {} | Elapsed: {}ms | Query: {} | RequestBody: {} | ResponseBody: {}";
    public static final String LOG_RESPONSE_WARN            = "Finished WARN in: ({}) -> {} | StatusCode: {} | Elapsed: {}ms | Query: {} | RequestBody: {} | ResponseBody: {}";
    public static final String LOG_RESPONSE_ERROR           = "Finished ERROR in: ({}) -> {} | StatusCode: {} | Elapsed: {}ms | Query: {} | RequestBody: {} | ResponseBody: {} | Error: {}";
}
