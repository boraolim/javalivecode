package hogar.codelive.inventory.middleware;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.function.Predicate;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.util.WebUtils;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import hogar.codelive.inventory.records.LogRule;
import hogar.codelive.inventory.constants.AppConstants;
import hogar.codelive.inventory.constants.LogConstants;
import hogar.codelive.common.middleware.MiddlewareUtil;
import hogar.codelive.inventory.records.RequestLogContext;
import hogar.codelive.inventory.constants.SecurityConstants;

@Slf4j
@Component
@RequiredArgsConstructor
public class HttpLoggingInterceptor implements AsyncHandlerInterceptor {

    @Value("${app.logging.slow-request-threshold-ms:1000}")
    private long slowThresholdMs;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        request.setAttribute(AppConstants.START_TIME_ATTR, System.currentTimeMillis());
        log.info(LogConstants.LOG_START_REQUEST, request.getMethod(), request.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler,
                                @Nullable Exception ex) {
        var ctx = new RequestLogContext(
            System.currentTimeMillis() - getStartTime(request),
            response.getStatus(),
            request.getMethod(),
            request.getRequestURI(),
            maskQueryString(request.getQueryString()),
            maskSensitiveData(extractRequestBody(request)),
            maskSensitiveData(extractResponseBody(response)),
            ex
        );

        List.of(
            // Regla 1: Error (Excepción o Status >= 400)
            new LogRule(
                contextLog -> contextLog.ex() != null || contextLog.status() >= 400,
                contextLog -> log.error(LogConstants.LOG_RESPONSE_ERROR, contextLog.method(), contextLog.path(), contextLog.status(), 
                    contextLog.elapsed(), contextLog.query(), MiddlewareUtil.truncate(contextLog.requestBody()), MiddlewareUtil.truncate(contextLog.responseBody()), 
                    contextLog.ex() != null ? MiddlewareUtil.truncate(rootCause(contextLog.ex())) : AppConstants.MSG_INTERNAL_ERROR)),
            // Regla 2: Advertencia por Lenta ejecución
            new LogRule(
                contextLog -> contextLog.elapsed() >= slowThresholdMs,
                contextLog -> log.warn("[SLOW REQUEST (>{}ms)] " + LogConstants.LOG_RESPONSE_WARN, slowThresholdMs, 
                    contextLog.method(), contextLog.path(), contextLog.status(), contextLog.elapsed(), contextLog.query(), 
                    MiddlewareUtil.truncate(contextLog.requestBody()), MiddlewareUtil.truncate(contextLog.responseBody()))),
            // Regla 3: Caso por defecto (OK)
            new LogRule(
                contextLog -> true,
                contextLog -> log.info(LogConstants.LOG_RESPONSE_OK, contextLog.method(), contextLog.path(), contextLog.status(), 
                    contextLog.elapsed(), contextLog.query(), MiddlewareUtil.truncate(contextLog.requestBody()), MiddlewareUtil.truncate(contextLog.responseBody()))))
            .stream()
            .filter(rule -> rule.predicate().test(ctx))
            .findFirst()
            .ifPresent(rule -> rule.action().accept(ctx));
    }

    private long getStartTime(HttpServletRequest request) {
        return Optional.ofNullable(request.getAttribute(AppConstants.START_TIME_ATTR))
            .filter(Long.class::isInstance)
            .map(Long.class::cast)
            .orElseGet(System::currentTimeMillis);
    }

    private String rootCause(Throwable throwable) {
        return Stream.iterate(throwable, Objects::nonNull, Throwable::getCause)
            .reduce((first, second) -> second)
            .map(ex -> Optional.ofNullable(ex.getMessage())
                .orElse(ex.getClass().getSimpleName()))
            .orElse(AppConstants.MSG_INTERNAL_ERROR);
    }

    private String extractRequestBody(HttpServletRequest request) {
        return Optional.ofNullable(WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class))
            .filter(wrapper -> isLoggableContentType(wrapper.getContentType()))
            .map(wrapper -> MiddlewareUtil.toReadableString(wrapper.getContentAsByteArray(), wrapper.getCharacterEncoding()))
            .orElse(null);
    }

    private String extractResponseBody(HttpServletResponse response) {
        return Optional.ofNullable(WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class))
            .filter(wrapper -> isLoggableContentType(wrapper.getContentType()))
            .map(this::extractResponseBody)
            .orElse(null);
    }

    private String extractResponseBody(ContentCachingResponseWrapper wrapper) {
        String body = MiddlewareUtil.toReadableString(wrapper.getContentAsByteArray(), wrapper.getCharacterEncoding());
        copyBodySafely(wrapper);
        return body;
    }

    private void copyBodySafely(ContentCachingResponseWrapper wrapper) {
        try {
            wrapper.copyBodyToResponse();
        } catch (Exception e) {
            log.error("Error copying cached response body back to response", e);
        }
    }

    private boolean isLoggableContentType(String contentType) {
        return Optional.ofNullable(contentType)
            .map(String::toLowerCase)
            .filter(Predicate.not(String::isBlank))
            .map(type -> Stream.of("json","xml","text").anyMatch(type::contains))
            .orElse(false);
    }

    private String maskSensitiveData(String contentString) {
        return Optional.ofNullable(contentString)
            .filter(Predicate.not(String::isBlank))
            .map(valueString -> SecurityConstants.JSON_FIELD_PATTERN.matcher(valueString).replaceAll("$1" + SecurityConstants.MASK + "$3"))
            .orElse(contentString);
    }

    private String maskQueryString(String queryString) {
        return Optional.ofNullable(queryString)
            .filter(Predicate.not(String::isBlank))
            .map(valueString -> SecurityConstants.QUERY_PARAM_PATTERN.matcher(valueString).replaceAll("$1" + SecurityConstants.MASK))
            .orElse(queryString);
    }
}
