package hogar.codelive.common.functions;

import java.util.Optional;
import java.util.function.Supplier;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class TryUtil {

    public static <T> T of(Supplier<T> supplier, T fallback) {
        try {
            return supplier.get();
        } catch (Exception ex) {
            return fallback;
        }
    }

    public <T> Optional<T> optional(Supplier<T> supplier) {
        try {
            return Optional.ofNullable(supplier.get());
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    public boolean execute(Runnable runnable) {
        try {
            runnable.run();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
