package hogar.codelive.products.exception;

import java.util.List;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import hogar.codelive.products.constants.AppConstants;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({InvalidSearchQueryException.class, HandlerMethodValidationException.class})
    public ResponseEntity<ApiError> handleInvalidQuery(InvalidSearchQueryException ex, HttpServletRequest request) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingParam(MissingServletRequestParameterException ex, HttpServletRequest request) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, AppConstants.MSG_EXCEPTION_INVALID_QUERY, request);
    }

    @ExceptionHandler({NoSuchElementException.class, EntityNotFoundException.class})
    public ResponseEntity<ApiError> handleNotFound(RuntimeException ex, HttpServletRequest request) {
        String message = ex.getMessage() != null ? ex.getMessage() : AppConstants.MSG_NOT_FOUND;
        return createErrorResponse(HttpStatus.NOT_FOUND, message, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        // Maneja cuando el JSON del body viene vacío, mal formado o falta por completo
        return createErrorResponse(HttpStatus.BAD_REQUEST, "Required request body is missing or malformed", request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> validationErrors = extractFieldErrors(ex);
        return createErrorResponse(HttpStatus.BAD_REQUEST, AppConstants.MSG_VALIDATION_ERROR, validationErrors, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception caught in GlobalExceptionHandler [URI: {}]", request.getRequestURI(), ex);
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, AppConstants.MSG_INTERNAL_ERROR, request);
    }

    private List<String> extractFieldErrors(MethodArgumentNotValidException ex) {
        return ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
                .toList();
    }

    private ResponseEntity<ApiError> createErrorResponse(HttpStatus status, String message, HttpServletRequest request) {
        return createErrorResponse(status, message, null, request);
    }

    private ResponseEntity<ApiError> createErrorResponse(HttpStatus status, String message, List<String> errors, HttpServletRequest request) {
        ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now(ZoneId.systemDefault()))
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .errors(errors)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(apiError);
    }
}