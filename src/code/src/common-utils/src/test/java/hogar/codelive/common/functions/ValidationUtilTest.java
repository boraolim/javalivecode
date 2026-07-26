package hogar.codelive.common.functions;

import java.util.Map;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import hogar.codelive.common.constants.AppConstants;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("ValidationUtilTest - Unit Tests")
class ValidationUtilTest {
    @Test
    void shouldReturnStringWhenNotNull() {

        String value = ValidationUtil.requireObjectNonNull("ChatGPT");

        assertEquals("ChatGPT", value);
    }

    @Test
    void shouldReturnIntegerWhenNotNull() {

        Integer value = ValidationUtil.requireObjectNonNull(25);

        assertEquals(25, value);
    }

    @Test
    void shouldReturnLongWhenNotNull() {

        Long value = ValidationUtil.requireObjectNonNull(100L);

        assertEquals(100L, value);
    }

    @Test
    void shouldReturnCustomObjectWhenNotNull() {

        var user = new Object() {
            String nameUser = "John Doe";
        };

        var result = ValidationUtil.requireObjectNonNull(user);

        assertSame(user, result);
    }

    @Test
    void shouldThrowExceptionWhenStringIsNull() {

        NullPointerException ex = assertThrows(NullPointerException.class,
            () -> ValidationUtil.requireObjectNonNull(null));

        assertEquals(AppConstants.MSG_MUST_NOT_BE_NULL, ex.getMessage());
    }

    @Test
    void shouldReturnList() {

        List<String> list = List.of("A", "B");

        List<String> result = ValidationUtil.requireObjectNonNull(list);

        assertEquals(2, result.size());
    }

    @Test
    void shouldReturnMap() {

        Map<String, Integer> map = Map.of("A", 1);

        Map<String, Integer> result = ValidationUtil.requireObjectNonNull(map);

        assertEquals(1, result.get("A"));
    }
}
