package hogar.codelive.externalws.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApiConstants {
    public static final String API_ALL_PATTERN     = "/**";
    public static final String API_SWAGGER_UI      = "/swagger-ui/**";
    public static final String API_SWAGGER_HTML    = "/swagger-ui.html";
    public static final String API_SWAGGER_DOCS    = "/api/v1/api-docs/**";
    public static final String API_SWAGGER_RESC    = "/swagger-resources/**";
    public static final String API_SWAGGER_WEBJARS = "/webjars/**";
    public static final String API_SWAGGER_FAVICON = "/favicon.ico";
    public static final String API_ERROR_URL       = "/error";
}
