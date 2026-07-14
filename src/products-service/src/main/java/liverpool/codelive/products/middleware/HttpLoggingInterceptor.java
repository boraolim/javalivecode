package liverpool.codelive.products.middleware;

import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.util.WebUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class HttpLoggingInterceptor implements HandlerInterceptor {

    private static final String START_TIME_ATTR = "HTTP_LOG_START_TIME";
    private static final int    MAX_CHARS       = 6000;
    private static final String MASK            = "***";

    // Nombres de campos que se enmascaran, sin importar mayusculas/minusculas
    // ni en que nivel de anidamiento del JSON aparezcan.
    private static final String SENSITIVE_FIELD_NAMES =
            "password|pass|pwd|token|accessToken|access_token|refreshToken|refresh_token" +
            "|authorization|secret|apiKey|api_key|clientSecret|client_secret" +
            "|creditCard|credit_card|cardNumber|card_number|cvv|ssn|pin";

    // Coincide con: "campoSensible" : "valor"  (con o sin espacios alrededor de los dos puntos)
    private static final Pattern JSON_FIELD_PATTERN = Pattern.compile(
            "(?i)(\"(?:" + SENSITIVE_FIELD_NAMES + ")\"\\s*:\\s*\")([^\"]*)(\")");

    // Coincide con: password=valor  dentro de un query string (?a=1&password=1234&b=2)
    private static final Pattern QUERY_PARAM_PATTERN = Pattern.compile(
            "(?i)((?:" + SENSITIVE_FIELD_NAMES + ")=)([^&]*)");

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                              @NonNull HttpServletResponse response,
                              @NonNull Object handler) {
        request.setAttribute(START_TIME_ATTR, System.currentTimeMillis());
        log.info("Starting request in: ({}) -> {}", request.getMethod(), request.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler,
                                @Nullable Exception ex) {

        long   elapsed      = System.currentTimeMillis() - getStartTime(request);
        String method       = request.getMethod();
        String path         = request.getRequestURI();
        String query        = maskQueryString(request.getQueryString());
        int    status       = response.getStatus();
        String exDetail     = ex != null ? truncate(rootCause(ex)) : null;
        String requestBody  = maskSensitiveData(extractRequestBody(request));
        String responseBody = maskSensitiveData(extractResponseBody(response));

        if (ex != null || status >= 500) {
            log.error("Finished ERROR in: ({}) -> {} | StatusCode: {} | Elapsed: {}ms | Query: {} | RequestBody: {} | ResponseBody: {} | Error: {}",
                    method, path, status, elapsed, query, requestBody, responseBody,
                    exDetail != null ? exDetail : "Internal Server Error");
        } else if (status >= 400) {
            log.warn("Finished WARN in: ({}) -> {} | StatusCode: {} | Elapsed: {}ms | Query: {} | RequestBody: {} | ResponseBody: {}",
                    method, path, status, elapsed, query, requestBody, responseBody);
        } else {
            log.info("Finished OK in: ({}) -> {} | StatusCode: {} | Elapsed: {}ms | Query: {} | RequestBody: {} | ResponseBody: {}",
                    method, path, status, elapsed, query, requestBody, responseBody);
        }
    }

    private long getStartTime(HttpServletRequest request) {
        Object attr = request.getAttribute(START_TIME_ATTR);
        return attr instanceof Long t ? t : System.currentTimeMillis();
    }

    private String rootCause(Throwable t) {
        Throwable c = t;
        while (c.getCause() != null) c = c.getCause();
        return c.getMessage() != null ? c.getMessage() : c.getClass().getSimpleName();
    }

    private String truncate(String value) {
        if (value == null) return null;
        return value.length() > MAX_CHARS ? value.substring(0, MAX_CHARS) : value;
    }

    private String extractRequestBody(HttpServletRequest request) {
        ContentCachingRequestWrapper wrapper =
                WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);

        if (wrapper == null || !isLoggableContentType(wrapper.getContentType())) {
            return null;
        }

        return toReadableString(wrapper.getContentAsByteArray(), wrapper.getCharacterEncoding());
    }

    private String extractResponseBody(HttpServletResponse response) {
        ContentCachingResponseWrapper wrapper =
                WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);

        if (wrapper == null || !isLoggableContentType(wrapper.getContentType())) {
            return null;
        }

        return toReadableString(wrapper.getContentAsByteArray(), wrapper.getCharacterEncoding());
    }

    private boolean isLoggableContentType(String contentType) {
        if (contentType == null) {
            return false;
        }
        String lower = contentType.toLowerCase();
        // Evita intentar loguear binarios (imagenes, archivos, multipart, etc.)
        return lower.contains("json") || lower.contains("text") || lower.contains("xml");
    }

    private String toReadableString(byte[] content, String encoding) {
        if (content == null || content.length == 0) {
            return null;
        }
        try {
            String charset = encoding != null ? encoding : StandardCharsets.UTF_8.name();
            return truncate(new String(content, charset));
        } catch (Exception e) {
            return "[no se pudo leer el cuerpo]";
        }
    }

    /**
     * Reemplaza el valor de cualquier campo JSON sensible (password, token,
     * authorization, tarjetas, etc.) por "***", sin importar en que nivel
     * de anidamiento del JSON aparezca ni si esta en mayusculas/minusculas.
     */
    private String maskSensitiveData(String content) {
        if (content == null || content.isBlank()) {
            return content;
        }
        return JSON_FIELD_PATTERN.matcher(content).replaceAll("$1" + MASK + "$3");
    }

    /**
     * Enmascara valores sensibles que pudieran venir como query params
     * (ej. ?token=xyz), en caso de que alguna integracion los mande asi
     * en vez de en el body (mala practica, pero ocurre).
     */
    private String maskQueryString(String query) {
        if (query == null || query.isBlank()) {
            return query;
        }
        return QUERY_PARAM_PATTERN.matcher(query).replaceAll("$1" + MASK);
    }
}