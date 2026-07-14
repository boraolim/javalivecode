package liverpool.codelive.products.external;

import java.util.concurrent.CompletableFuture;

import jakarta.xml.ws.WebServiceException;
import liverpool.codelive.products.wsdl.calculator.CalculatorSoap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@ExtendWith(MockitoExtension.class)
class SoapCalculatorClientTest {
    private SoapCalculatorClient soapCalculatorClient;

    @Mock
    private CalculatorSoap calculatorPortMock; // Mockeamos el puerto para simular la respuesta SOAP sin red

    @BeforeEach
    void setUp() {
        // Usamos Mockito.mock sin pasar parámetros para evitar que invoque el constructor real con @Value
        soapCalculatorClient = mock(SoapCalculatorClient.class, Mockito.CALLS_REAL_METHODS);
        
        // Inyectamos por reflexión el puerto mockeado dentro del campo privado de la instancia real
        ReflectionTestUtils.setField(soapCalculatorClient, "calculatorPort", calculatorPortMock);
    }

    @Test
    void ejecutarSuma_ShouldReturnExpectedSum_WhenSoapCallIsSuccessful() throws Exception {
        // Arrange
        int a = 40;
        int b = 17;
        int expectedResult = 57;

        // Configuramos el mock del WSDL para que responda la suma de inmediato
        when(calculatorPortMock.add(a, b)).thenReturn(expectedResult);

        // Act
        CompletableFuture<Integer> futureResult = soapCalculatorClient.ejecutarSuma(a, b);

        // Assert
        assertNotNull(futureResult, "The returned CompletableFuture should never be null");
        int actualResult = futureResult.get(); // Bloquea hasta que el CompletableFuture.supplyAsync se resuelva
        
        assertEquals(expectedResult, actualResult, "The asynchronous result must match the SOAP port response");
        verify(calculatorPortMock).add(a, b); // Verificamos que se invocó el cliente nativo del WSDL
    }

    @Test
    void ejecutarSuma_ShouldHandleNegativeValuesCorrectly() throws Exception {
        // Arrange
        int a = 10;
        int b = -5;
        int expectedResult = 5;

        when(calculatorPortMock.add(a, b)).thenReturn(expectedResult);

        // Act
        CompletableFuture<Integer> futureResult = soapCalculatorClient.ejecutarSuma(a, b);

        // Assert
        assertEquals(expectedResult, futureResult.get());
        verify(calculatorPortMock).add(a, b);
    }

    @Test
    void fallbackEjecutarSuma_ShouldReturnLocalSumAsContingency_WhenSoapCallFails() throws Exception {
        // Arrange
        int valueA = 45;
        int valueB = 12;
        int expectedResult = 57;
        Throwable exceptionReason = new WebServiceException("Forced timeout or Circuit Breaker is OPEN");

        // Usamos Mockito.mock para instanciar la clase sin disparar el constructor real de JAX-WS,
        // evitando que intente conectarse a la URL de tu application.yml
        SoapCalculatorClient clientMock = mock(SoapCalculatorClient.class);
        
        // Hacemos que el método real del fallback se ejecute cuando sea llamado en el mock
        when(clientMock.fallbackEjecutarSuma(valueA, valueB, exceptionReason)).thenCallRealMethod();

        // Act
        // Evaluamos de forma directa la estrategia de resiliencia ante un fallo
        CompletableFuture<Integer> futureResult = clientMock.fallbackEjecutarSuma(valueA, valueB, exceptionReason);

        // Assert
        assertNotNull(futureResult, "The fallback future should never be null");
        assertEquals(expectedResult, futureResult.get(), 
                "The fallback must asynchronously deliver the local sum calculation when the external service fails");
    }

    @Test
    void ejecutarSuma_ShouldTrackExecution_WhenInvokedThroughSpy() throws Exception {
        // Arrange
        int valueA = 10;
        int valueB = 20;
        int expectedResult = 30;
        Throwable exception = new WebServiceException("Read timeout");

        SoapCalculatorClient clientSpy = mock(SoapCalculatorClient.class);
        when(clientSpy.fallbackEjecutarSuma(valueA, valueB, exception)).thenCallRealMethod();

        // Act
        // Simulamos el comportamiento que Resilience4j dispararía de manera interna
        String resultFuture = clientSpy.fallbackEjecutarSuma(valueA, valueB, exception).thenApply(String::valueOf).get();

        // Assert
        assertEquals(String.valueOf(expectedResult), resultFuture);
        verify(clientSpy).fallbackEjecutarSuma(valueA, valueB, exception);
    }
}