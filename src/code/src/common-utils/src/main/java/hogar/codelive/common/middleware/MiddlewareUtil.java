package hogar.codelive.common.middleware;

import java.util.Optional;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import lombok.experimental.UtilityClass;

import hogar.codelive.common.functions.TryUtil;
import hogar.codelive.common.constants.AppConstants;

@UtilityClass
public final class MiddlewareUtil {

    public static String truncate(String value) {
        return Optional.ofNullable(value)
            .map(valueString -> valueString.length() > AppConstants.MAX_CHARS
                    ? valueString.substring(0, AppConstants.MAX_CHARS)
                    : valueString)
            .orElse(null);
    }

    public static String toReadableString(byte[] contentBytes, String encodingString) {
        return Optional.ofNullable(contentBytes)
            .filter(bytesValues -> bytesValues.length > 0)
            .map(bytesValues -> decodeBytes(bytesValues, encodingString))
            .orElse(null);
    }

    public static String decodeBytes(byte[] byteContent, String encodingString) {
        Charset charset = TryUtil.of(() -> Optional.ofNullable(encodingString)
                .map(Charset::forName)
                .orElse(StandardCharsets.UTF_8), StandardCharsets.UTF_8);

        return truncate(new String(byteContent, charset));
    }

}
