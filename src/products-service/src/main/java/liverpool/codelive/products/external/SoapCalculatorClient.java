package liverpool.codelive.products.external;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.util.concurrent.CompletableFuture;

import liverpool.codelive.products.wsdl.calculator.CalculatorSoap;

@Slf4j
@Component
@RequiredArgsConstructor
public class SoapCalculatorClient {

    private static final String RESILIENCE_INSTANCE = "soapCalculatorService";
    
    // Inyección limpia por constructor gestionada por Spring
    private final CalculatorSoap calculatorPort;

    @Retry(name = RESILIENCE_INSTANCE)
    @CircuitBreaker(name = RESILIENCE_INSTANCE, fallbackMethod = "fallbackEjecutarSuma")
    @TimeLimiter(name = RESILIENCE_INSTANCE, fallbackMethod = "fallbackEjecutarSuma")
    public CompletableFuture<Integer> ejecutarSuma(int a, int b) {
        // Ejecutamos la petición bloqueante del WSDL en un hilo asíncrono gestionado por Spring
        return CompletableFuture.supplyAsync(() -> {
            log.info("Executing SOAP Call (WSDL) to Calculator Service for values: [{}], [{}]", a, b);
            return calculatorPort.add(a, b);
        });
    }

    // Firma simétrica para capturar errores de red, timeouts o circuitos abiertos
    CompletableFuture<Integer> fallbackEjecutarSuma(int a, int b, Throwable ex) {
        log.warn("Fallback de calculadora SOAP activado para valores [{} y {}]. Motivo: {}", a, b, ex.getMessage());
        
        // Estrategia de contingencia local idéntica al estándar reactivo que manejas
        return CompletableFuture.completedFuture(a + b);
    }
}