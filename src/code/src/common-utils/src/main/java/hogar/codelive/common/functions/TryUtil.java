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

    public static <T> Optional<T> optional(Supplier<T> supplier) {
        return Optional.ofNullable(of(supplier, null));
    }

    public static boolean execute(Runnable runnable) {
        return of(() -> {
            runnable.run();
            return true;
        }, false);
    }
}
