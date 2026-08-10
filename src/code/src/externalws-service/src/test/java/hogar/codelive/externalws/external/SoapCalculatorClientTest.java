package hogar.codelive.externalws.external;

import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import hogar.codelive.externalws.wsdl.calculator.CalculatorSoap;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@DisplayName("SoapCalculatorClient - Unit Tests")
class SoapCalculatorClientTest {

    @Mock
    private CalculatorSoap calculatorPort;

    @InjectMocks
    private SoapCalculatorClient soapCalculatorClient;

    @Test
    @DisplayName("EXITO: Debería ejecutar la suma correctamente llamando al puerto SOAP")
    void ejecutarSuma_debeRetornarResultadoExitoso() throws Exception {
        // Given
        int a = 5;
        int b = 3;
        when(calculatorPort.add(a, b)).thenReturn(8);

        // When
        CompletableFuture<Integer> futureResult = soapCalculatorClient.ejecutarSuma(a, b);
        Integer result = futureResult.get(); // Esperamos la resolución del CompletableFuture

        // Then
        assertNotNull(result);
        assertEquals(8, result);
        verify(calculatorPort, times(1)).add(a, b);
    }

    @Test
    @DisplayName("EXITO: Debería ejecutar la multiplicación correctamente llamando al puerto SOAP")
    void executeMultiply_debeRetornarResultadoExitoso() throws Exception {
        // Given
        int a = 4;
        int b = 5;
        when(calculatorPort.multiply(a, b)).thenReturn(20);

        // When
        CompletableFuture<Integer> futureResult = soapCalculatorClient.executeMultiply(a, b);
        Integer result = futureResult.get();

        // Then
        assertNotNull(result);
        assertEquals(20, result);
        verify(calculatorPort, times(1)).multiply(a, b);
    }

    @Test
    @DisplayName("EXITO: Debería ejecutar la división correctamente llamando al puerto SOAP")
    void executeDivide_debeRetornarResultadoExitoso() throws Exception {
        // Given
        int a = 10;
        int b = 2;
        when(calculatorPort.divide(a, b)).thenReturn(5);

        // When
        CompletableFuture<Integer> futureResult = soapCalculatorClient.executeDivide(a, b);
        Integer result = futureResult.get();

        // Then
        assertNotNull(result);
        assertEquals(5, result);
        verify(calculatorPort, times(1)).divide(a, b);
    }

    @Test
    @DisplayName("FALLBACK: Debería ejecutar el fallback de suma correctamente al ocurrir un fallo")
    void fallbackExecuteSum_debeRetornarSumaLocal() throws Exception {
        // Given
        int a = 10;
        int b = 15;
        Throwable ex = new RuntimeException("Servicio SOAP caído");

        // When
        CompletableFuture<Integer> futureResult = soapCalculatorClient.fallbackExecuteSum(a, b, ex);
        Integer result = futureResult.get();

        // Then
        assertNotNull(result);
        assertEquals(25, result); // El fallback ejecuta a + b (10 + 15)
    }

    @Test
    @DisplayName("FALLBACK: Debería ejecutar el fallback de multiplicación correctamente al ocurrir un fallo")
    void fallbackExecuteMultiply_debeRetornarMultiplicacionLocal() throws Exception {
        // Given
        int a = 6;
        int b = 7;
        Throwable ex = new RuntimeException("Timeout en servicio SOAP");

        // When
        CompletableFuture<Integer> futureResult = soapCalculatorClient.fallbackExecuteMultiply(a, b, ex);
        Integer result = futureResult.get();

        // Then
        assertNotNull(result);
        assertEquals(42, result); // El fallback ejecuta a * b (6 * 7)
    }

    @Test
    @DisplayName("FALLBACK: Debería ejecutar el fallback de división correctamente y retornar el resultado")
    void fallbackExecuteDivide_debeRetornarDivisionLocal() throws Exception {
        // Given
        int a = 20;
        int b = 4;
        Throwable ex = new RuntimeException("Circuit Breaker Abierto");

        // When
        CompletableFuture<Integer> futureResult = soapCalculatorClient.fallbackExecuteDivide(a, b, ex);
        Integer result = futureResult.get();

        // Then
        assertNotNull(result);
        assertEquals(5, result); // El fallback ejecuta a / b (20 / 4)
    }

    @Test
    @DisplayName("FALLBACK: Debería lanzar ArithmeticException si en el fallback de división el divisor es cero")
    void fallbackExecuteDivide_cuandoDivisorEsCero_debeLanzarExcepcion() {
        // Given
        int a = 10;
        int b = 0;
        Throwable ex = new RuntimeException("Error de conexión");

        // When & Then
        ArithmeticException exception = assertThrows(ArithmeticException.class, () -> {
            soapCalculatorClient.fallbackExecuteDivide(a, b, ex);
        });

        assertEquals("Division by zero", exception.getMessage());
    }
}