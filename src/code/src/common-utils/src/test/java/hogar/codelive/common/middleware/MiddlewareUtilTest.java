package hogar.codelive.common.middleware;

import java.util.Map;

import java.nio.charset.StandardCharsets;

import org.slf4j.MDC;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import hogar.codelive.common.constants.AppConstants;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("MiddlewareUtilTest - Unit Tests")
class MiddlewareUtilTest {

    @BeforeEach
    void setUp() {
        MDC.clear();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldReturnOriginalTextWhenLengthIsLowerThanMaxChars() {
        String input = "Hello World";

        String result = MiddlewareUtil.truncate(input);

        assertEquals(input, result);
    }

    @Test
    void shouldReturnSameTextWhenLengthEqualsMaxChars() {
        String input = "a".repeat(AppConstants.MAX_CHARS);

        String result = MiddlewareUtil.truncate(input);

        assertEquals(input, result);
    }

    @Test
    void shouldTruncateTextWhenLengthExceedsMaxChars() {
        String input = "a".repeat(AppConstants.MAX_CHARS + 10);

        String result = MiddlewareUtil.truncate(input);

        assertEquals(AppConstants.MAX_CHARS, result.length());
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {

        String result = MiddlewareUtil.truncate(null);

        assertNull(result);
    }

    @Test
    void shouldConvertUtf8BytesToReadableString() {

        byte[] bytes = "Hola Mundo".getBytes(StandardCharsets.UTF_8);

        String result = MiddlewareUtil.toReadableString(bytes, "UTF-8");

        assertEquals("Hola Mundo", result);
    }

    @Test
    void shouldDecodeUsingProvidedEncoding() {

        byte[] bytes = "áéíóú".getBytes(StandardCharsets.ISO_8859_1);

        String result = MiddlewareUtil.toReadableString(bytes, "ISO-8859-1");

        assertEquals("áéíóú", result);
    }

    @Test
    void shouldReturnNullWhenBytesAreNull() {

        String result = MiddlewareUtil.toReadableString(null, "UTF-8");

        assertNull(result);
    }

    @Test
    void shouldReturnNullWhenBytesAreEmpty() {

        String result = MiddlewareUtil.toReadableString(new byte[0], "UTF-8");

        assertNull(result);
    }

    @Test
    void shouldDecodeBytesWithValidCharset() {

        byte[] bytes = "Spring Boot".getBytes(StandardCharsets.UTF_8);

        String result = MiddlewareUtil.decodeBytes(bytes, "UTF-8");

        assertEquals("Spring Boot", result);
    }

    @Test
    void shouldUseUtf8WhenEncodingIsNull() {

        byte[] bytes = "áéí".getBytes(StandardCharsets.UTF_8);

        String result = MiddlewareUtil.decodeBytes(bytes, null);

        assertEquals("áéí", result);
    }

    @Test
    void shouldUseUtf8WhenEncodingIsInvalid() {

        byte[] bytes = "Hello".getBytes(StandardCharsets.UTF_8);

        String result = MiddlewareUtil.decodeBytes(bytes, "INVALID_ENCODING");

        assertEquals("Hello", result);
    }

    @Test
    void shouldTruncateDecodedContentWhenExceedsLimit() {

        String largeText = "x".repeat(AppConstants.MAX_CHARS + 20);

        byte[] bytes = largeText.getBytes(StandardCharsets.UTF_8);

        String result = MiddlewareUtil.decodeBytes(bytes, "UTF-8");

        assertEquals(AppConstants.MAX_CHARS, result.length());
    }
    
@Test
    @DisplayName("restoreMdc - Debería cubrir la rama cuando el mapa NO es nulo")
    void shouldRestoreMdcWhenContextMapNotNull() {
        Map<String, String> map = Map.of("testKey", "testValue");
        
        MiddlewareUtil.restoreMdc(map);

        assertThat(MDC.get("testKey")).isEqualTo("testValue");
    }

    @Test
    @DisplayName("restoreMdc - Debería cubrir la rama FALSE cuando el mapa ES nulo (Evita branch coverage incompleto)")
    void shouldDoNothingWhenContextMapIsNull() {
        MDC.put("existingKey", "value");

        MiddlewareUtil.restoreMdc(null);

        // Verifica que no rompió y mantuvo el estado anterior
        assertThat(MDC.get("existingKey")).isEqualTo("value");
    }

    @Test
    @DisplayName("withMdcCleanup - Debería ejecutar el Supplier con éxito y limpiar el MDC en el finally")
    void shouldExecuteSupplierAndTriggerFinallyCleanup() {
        MDC.put("persistentKey", "temp");

        String result = MiddlewareUtil.withMdcCleanup(() -> {
            assertThat(MDC.get("persistentKey")).isEqualTo("temp");
            return "OK";
        });

        assertThat(result).isEqualTo("OK");
        assertThat(MDC.getCopyOfContextMap()).isNullOrEmpty();
    }

    @Test
    @DisplayName("withMdcCleanup - Debería limpiar el MDC en el finally incluso si el Supplier lanza una excepción")
    void shouldCleanupMdcInFinallyWhenSupplierThrowsException() {
        MDC.put("persistentKey", "temp");

        assertThatThrownBy(() -> 
            MiddlewareUtil.withMdcCleanup(() -> {
                throw new RuntimeException("Error de prueba");
            })
        )
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Error de prueba");

        // Valida que el finally cubrió la limpieza a pesar de la excepción
        assertThat(MDC.getCopyOfContextMap()).isNullOrEmpty();
    }
}
