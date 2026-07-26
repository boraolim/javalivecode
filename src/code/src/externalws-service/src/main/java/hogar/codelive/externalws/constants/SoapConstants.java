package hogar.codelive.externalws.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SoapConstants {
    public static final String RESILIENCE_INSTANCE_COUNTRY = "soapCountryService";

    public static final String RESILIENCE_INSTANCE_CALCULATOR = "soapCalculatorService";
    public static final String FALLBACK_METHOD_CALCULATOR = "fallbackExecuteSum";
    public static final String FALLBACK_METHOD_MULTIPLY = "fallbackExecuteMultiply";
    public static final String FALLBACK_METHOD_DIVIDE = "fallbackExecuteDivide";
}
