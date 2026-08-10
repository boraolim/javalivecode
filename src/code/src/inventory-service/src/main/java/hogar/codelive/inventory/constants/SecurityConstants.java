package hogar.codelive.inventory.constants;

import java.util.regex.Pattern;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityConstants {
    public static final int MAX_ATTEMPTS_LOGIN       = 3;
    public static final String MASK                  = "***";
    public static final String SENSITIVE_FIELD_NAMES = "password|pass|pwd|token|accessToken|access_token|refreshToken|refresh_token|authorization|secret|apiKey|api_key|clientSecret|client_secret|creditCard|credit_card|cardNumber|card_number|cvv|ssn|pin";

    public static final Pattern JSON_FIELD_PATTERN   = Pattern.compile("(?i)(\"(?:" + SecurityConstants.SENSITIVE_FIELD_NAMES + ")\"\\s*:\\s*\")([^\"]*)(\")");
    public static final Pattern QUERY_PARAM_PATTERN  = Pattern.compile("(?i)((?:" + SecurityConstants.SENSITIVE_FIELD_NAMES + ")=)([^&]*)");
}
