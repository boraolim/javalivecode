package hogar.codelive.common.functions;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;

import hogar.codelive.common.constants.AppTestConstants;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("TryUtilTest - Unit Tests")
class TryUtilTest {

    @Test
    void shouldReturnSupplierValue() {

        String result = TryUtil.of(() -> AppTestConstants.API_SAY_HELLO, AppTestConstants.API_SAY_ERROR);
        
        assertEquals(AppTestConstants.API_SAY_HELLO, result);
    }

    @Test
    void shouldReturnFallbackWhenSupplierThrowsException() {

        String result = TryUtil.of(() -> { throw new RuntimeException(); }, AppTestConstants.API_SAY_FALLBACK);

        assertEquals(AppTestConstants.API_SAY_FALLBACK, result);
    }

    @Test
    void shouldReturnNullWhenSupplierReturnsNull() {

        String result = TryUtil.of(() -> null, AppTestConstants.API_SAY_FALLBACK_CURRENT);

        assertNull(result);
    }

    @Test
    void shouldReturnNullFallback() {

        String result = TryUtil.of(() -> { throw new RuntimeException(); }, null);

        assertNull(result);
    }

    @Test
    void shouldReturnOptionalWithValue() {

        Optional<String> result = TryUtil.optional(() -> AppTestConstants.API_SAY_OPEN_AI);

        assertTrue(result.isPresent());
        assertEquals(AppTestConstants.API_SAY_OPEN_AI, result.get());
    }

    @Test
    void shouldReturnEmptyOptionalWhenSupplierReturnsNull() {

        Optional<String> result = TryUtil.optional(() -> null);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyOptionalWhenExceptionOccurs() {

        Optional<String> result = TryUtil.optional(() -> { throw new RuntimeException(); });

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnTrueWhenRunnableExecutesSuccessfully() {

        boolean result = TryUtil.execute(() -> { });

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenRunnableThrowsException() {

        boolean result = TryUtil.execute(() -> { throw new RuntimeException(); });

        assertFalse(result);
    }

    @Test
    void shouldExecuteRunnable() {

        AtomicBoolean executed = new AtomicBoolean(false);

        boolean result = TryUtil.execute(() -> executed.set(true));

        assertTrue(result);
        assertTrue(executed.get());
    }    
}
