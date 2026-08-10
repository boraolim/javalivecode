package hogar.codelive.externalws.external;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.util.concurrent.CompletableFuture;

import hogar.codelive.externalws.constants.SoapConstants;
import hogar.codelive.externalws.wsdl.calculator.CalculatorSoap;

@Slf4j
@Component
@RequiredArgsConstructor
public class SoapCalculatorClient {
    private final CalculatorSoap calculatorPort;

    @Retry(name = SoapConstants.RESILIENCE_INSTANCE_CALCULATOR)
    @CircuitBreaker(name = SoapConstants.RESILIENCE_INSTANCE_CALCULATOR, fallbackMethod = SoapConstants.FALLBACK_METHOD_CALCULATOR)
    @TimeLimiter(name = SoapConstants.RESILIENCE_INSTANCE_CALCULATOR, fallbackMethod = SoapConstants.FALLBACK_METHOD_CALCULATOR)
    public CompletableFuture<Integer> ejecutarSuma(int a, int b) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Executing SOAP Call (WSDL) to Calculator Service 'add' for values: [{}], [{}]", a, b);
            return calculatorPort.add(a, b);
        });
    }

    @Retry(name = SoapConstants.RESILIENCE_INSTANCE_CALCULATOR)
    @CircuitBreaker(name = SoapConstants.RESILIENCE_INSTANCE_CALCULATOR, fallbackMethod = SoapConstants.FALLBACK_METHOD_MULTIPLY)
    @TimeLimiter(name = SoapConstants.RESILIENCE_INSTANCE_CALCULATOR, fallbackMethod = SoapConstants.FALLBACK_METHOD_MULTIPLY)
    public CompletableFuture<Integer> executeMultiply(int a, int b) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Executing SOAP Call (WSDL) to Calculator Service 'multiply' for values: [{}], [{}]", a, b);
            return calculatorPort.multiply(a, b);
        });
    }

    @Retry(name = SoapConstants.RESILIENCE_INSTANCE_CALCULATOR)
    @CircuitBreaker(name = SoapConstants.RESILIENCE_INSTANCE_CALCULATOR, fallbackMethod = SoapConstants.FALLBACK_METHOD_DIVIDE)
    @TimeLimiter(name = SoapConstants.RESILIENCE_INSTANCE_CALCULATOR, fallbackMethod = SoapConstants.FALLBACK_METHOD_DIVIDE)
    public CompletableFuture<Integer> executeDivide(int a, int b) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Executing SOAP Call (WSDL) to Calculator Service 'divide' for values: [{}], [{}]", a, b);
            return calculatorPort.divide(a, b);
        });
    }

    CompletableFuture<Integer> fallbackExecuteSum(int a, int b, Throwable ex) {
        log.warn("SOAP Calculator 'add' unavailable. Executing local fallback. Values: [{}, {}]. Cause: {}", a, b, ex.getMessage());
        return CompletableFuture.completedFuture(a + b);
    }

    CompletableFuture<Integer> fallbackExecuteMultiply(int a, int b, Throwable ex) {
        log.warn("SOAP Calculator 'multiply' unavailable. Executing local fallback. Values: [{}, {}]. Cause: {}", a, b, ex.getMessage());
        return CompletableFuture.completedFuture(a * b);
    }

    CompletableFuture<Integer> fallbackExecuteDivide(int a, int b, Throwable ex) {
        log.warn("SOAP Calculator 'divide' unavailable. Executing local fallback. Values: [{}, {}]. Cause: {}", a, b, ex.getMessage());
        if (b == 0) {
            throw new ArithmeticException("Division by zero");
        }

        return CompletableFuture.completedFuture(a / b);
    }
}