package hogar.codelive.common.middleware;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;

import hogar.codelive.common.constants.AppConstants;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("MiddlewareUtilTest - Unit Tests")
class MiddlewareUtilTest {
    
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
}
