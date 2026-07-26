package hogar.codelive.common.functions;

import java.util.Objects;

import lombok.experimental.UtilityClass;

import hogar.codelive.common.constants.AppConstants;

@UtilityClass
public final class ValidationUtil {
    public static <T> T requireObjectNonNull(T value) {
        return Objects.requireNonNull(value, () -> AppConstants.MSG_MUST_NOT_BE_NULL);
    }
}
