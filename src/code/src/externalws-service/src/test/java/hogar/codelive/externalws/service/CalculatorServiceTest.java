package hogar.codelive.externalws.service;

import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import hogar.codelive.externalws.external.SoapCalculatorClient;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@DisplayName("CalculatorService - Unit Tests")
class CalculatorServiceTest {

    @Mock
    private SoapCalculatorClient soapCalculatorClient;

    @InjectMocks
    private CalculatorService calculatorService;

    @Test
    @DisplayName("EXITO: Debería calcular y retornar la suma de manera asíncrona")
    void getAddValue_debeRetornarSumaExitosasAsync() throws Exception {
        // Given
        int a = 10;
        int b = 5;
        when(soapCalculatorClient.ejecutarSuma(a, b))
                .thenReturn(CompletableFuture.completedFuture(15));

        // When
        CompletableFuture<Integer> futureResult = calculatorService.getAddValue(a, b);
        Integer result = futureResult.get();

        // Then
        assertNotNull(result);
        assertEquals(15, result);
        verify(soapCalculatorClient, times(1)).ejecutarSuma(a, b);
    }

    @Test
    @DisplayName("EXITO: Debería calcular y retornar la multiplicación de manera asíncrona")
    void getMultiplyValue_debeRetornarMultiplicacionExitosasAsync() throws Exception {
        // Given
        int a = 4;
        int b = 3;
        when(soapCalculatorClient.executeMultiply(a, b))
                .thenReturn(CompletableFuture.completedFuture(12));

        // When
        CompletableFuture<Integer> futureResult = calculatorService.getMultiplyValue(a, b);
        Integer result = futureResult.get();

        // Then
        assertNotNull(result);
        assertEquals(12, result);
        verify(soapCalculatorClient, times(1)).executeMultiply(a, b);
    }

    @Test
    @DisplayName("EXITO: Debería calcular y retornar la división de manera asíncrona")
    void getDivideValue_debeRetornarDivisionExitosasAsync() throws Exception {
        // Given
        int a = 20;
        int b = 4;
        when(soapCalculatorClient.executeDivide(a, b))
                .thenReturn(CompletableFuture.completedFuture(5));

        // When
        CompletableFuture<Integer> futureResult = calculatorService.getDivideValue(a, b);
        Integer result = futureResult.get();

        // Then
        assertNotNull(result);
        assertEquals(5, result);
        verify(soapCalculatorClient, times(1)).executeDivide(a, b);
    }
}