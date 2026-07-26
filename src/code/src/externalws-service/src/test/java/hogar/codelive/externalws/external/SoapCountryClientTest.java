package hogar.codelive.externalws.external;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import hogar.codelive.externalws.wsdl.countryinfo.TCurrency;
import hogar.codelive.externalws.wsdl.countryinfo.TCountryInfo;
import hogar.codelive.externalws.wsdl.countryinfo.ArrayOftCurrency;
import hogar.codelive.externalws.wsdl.countryinfo.ArrayOftLanguage;
import hogar.codelive.externalws.wsdl.countryinfo.ArrayOftContinent;
import hogar.codelive.externalws.wsdl.countryinfo.ArrayOftCountryInfo;
import hogar.codelive.externalws.wsdl.countryinfo.ArrayOftCountryCodeAndName;
import hogar.codelive.externalws.wsdl.countryinfo.CountryInfoServiceSoapType;
import hogar.codelive.externalws.wsdl.countryinfo.ArrayOftCountryCodeAndNameGroupedByContinent;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@DisplayName("SoapCountryClientTest - Unit Tests")
class SoapCountryClientTest {

    @MockitoBean
    private CountryInfoServiceSoapType countryPort;

    @Autowired
    private SoapCountryClient soapCountryClient;

    // ==========================================
    // PRUEBAS DE ÉXITO - MÉTODOS SIN PARÁMETROS
    // ==========================================

    @Test
    @DisplayName("Éxito: fullCountryInfoAllCountries")
    void testFullCountryInfoAllCountriesSuccess() {
        ArrayOftCountryInfo mockResponse = new ArrayOftCountryInfo();
        when(countryPort.fullCountryInfoAllCountries()).thenReturn(mockResponse);

        CompletableFuture<ArrayOftCountryInfo> future = soapCountryClient.fullCountryInfoAllCountries();
        assertNotNull(future.join());
    }

    @Test
    @DisplayName("Éxito: listOfContinentsByCode")
    void testListOfContinentsByCodeSuccess() {
        ArrayOftContinent mockResponse = new ArrayOftContinent();
        when(countryPort.listOfContinentsByCode()).thenReturn(mockResponse);

        CompletableFuture<ArrayOftContinent> future = soapCountryClient.listOfContinentsByCode();
        assertNotNull(future.join());
    }

    @Test
    @DisplayName("Éxito: listOfContinentsByName")
    void testListOfContinentsByNameSuccess() {
        ArrayOftContinent mockResponse = new ArrayOftContinent();
        when(countryPort.listOfContinentsByName()).thenReturn(mockResponse);

        CompletableFuture<ArrayOftContinent> future = soapCountryClient.listOfContinentsByName();
        assertNotNull(future.join());
    }

    @Test
    @DisplayName("Éxito: listOfCountryNamesByCode")
    void testListOfCountryNamesByCodeSuccess() {
        ArrayOftCountryCodeAndName mockResponse = new ArrayOftCountryCodeAndName();
        when(countryPort.listOfCountryNamesByCode()).thenReturn(mockResponse);

        CompletableFuture<ArrayOftCountryCodeAndName> future = soapCountryClient.listOfCountryNamesByCode();
        assertNotNull(future.join());
    }

    @Test
    @DisplayName("Éxito: listOfCountryNamesByName")
    void testListOfCountryNamesByNameSuccess() {
        ArrayOftCountryCodeAndName mockResponse = new ArrayOftCountryCodeAndName();
        when(countryPort.listOfCountryNamesByName()).thenReturn(mockResponse);

        CompletableFuture<ArrayOftCountryCodeAndName> future = soapCountryClient.listOfCountryNamesByName();
        assertNotNull(future.join());
    }

    @Test
    @DisplayName("Éxito: listOfCountryNamesGroupedByContinent")
    void testListOfCountryNamesGroupedByContinentSuccess() {
        ArrayOftCountryCodeAndNameGroupedByContinent mockResponse = new ArrayOftCountryCodeAndNameGroupedByContinent();
        when(countryPort.listOfCountryNamesGroupedByContinent()).thenReturn(mockResponse);

        CompletableFuture<ArrayOftCountryCodeAndNameGroupedByContinent> future = soapCountryClient.listOfCountryNamesGroupedByContinent();
        assertNotNull(future.join());
    }

    @Test
    @DisplayName("Éxito: listOfCurrenciesByCode")
    void testListOfCurrenciesByCodeSuccess() {
        ArrayOftCurrency mockResponse = new ArrayOftCurrency();
        when(countryPort.listOfCurrenciesByCode()).thenReturn(mockResponse);

        CompletableFuture<ArrayOftCurrency> future = soapCountryClient.listOfCurrenciesByCode();
        assertNotNull(future.join());
    }

    @Test
    @DisplayName("Éxito: listOfCurrenciesByName")
    void testListOfCurrenciesByNameSuccess() {
        ArrayOftCurrency mockResponse = new ArrayOftCurrency();
        when(countryPort.listOfCurrenciesByName()).thenReturn(mockResponse);

        CompletableFuture<ArrayOftCurrency> future = soapCountryClient.listOfCurrenciesByName();
        assertNotNull(future.join());
    }

    @Test
    @DisplayName("Éxito: listOfLanguagesByCode")
    void testListOfLanguagesByCodeSuccess() {
        ArrayOftLanguage mockResponse = new ArrayOftLanguage();
        when(countryPort.listOfLanguagesByCode()).thenReturn(mockResponse);

        CompletableFuture<ArrayOftLanguage> future = soapCountryClient.listOfLanguagesByCode();
        assertNotNull(future.join());
    }

    @Test
    @DisplayName("Éxito: listOfLanguagesByName")
    void testListOfLanguagesByNameSuccess() {
        ArrayOftLanguage mockResponse = new ArrayOftLanguage();
        when(countryPort.listOfLanguagesByName()).thenReturn(mockResponse);

        CompletableFuture<ArrayOftLanguage> future = soapCountryClient.listOfLanguagesByName();
        assertNotNull(future.join());
    }


    // ==========================================
    // PRUEBAS DE ÉXITO - MÉTODOS CON UN PARÁMETRO
    // ==========================================

    @Test
    @DisplayName("Éxito: fullCountryInfo")
    void testFullCountryInfoSuccess() {
        String code = "MX";
        TCountryInfo mockResponse = new TCountryInfo();
        when(countryPort.fullCountryInfo(code)).thenReturn(mockResponse);

        CompletableFuture<TCountryInfo> future = soapCountryClient.fullCountryInfo(code);
        assertNotNull(future.join());
    }

    @Test
    @DisplayName("Éxito: currencyName")
    void testCurrencyNameSuccess() {
        String code = "USD";
        when(countryPort.currencyName(code)).thenReturn("US Dollar");

        CompletableFuture<String> future = soapCountryClient.currencyName(code);
        assertEquals("US Dollar", future.join());
    }

    @Test
    @DisplayName("Éxito: countryName")
    void testCountryNameSuccess() {
        String code = "MX";
        when(countryPort.countryName(code)).thenReturn("Mexico");

        CompletableFuture<String> future = soapCountryClient.countryName(code);
        assertEquals("Mexico", future.join());
    }

    @Test
    @DisplayName("Éxito: countryISOCode")
    void testCountryISOCodeSuccess() {
        String name = "Mexico";
        when(countryPort.countryISOCode(name)).thenReturn("MX");

        CompletableFuture<String> future = soapCountryClient.countryISOCode(name);
        assertEquals("MX", future.join());
    }

    @Test
    @DisplayName("Éxito: countryIntPhoneCode")
    void testCountryIntPhoneCodeSuccess() {
        String code = "MX";
        when(countryPort.countryIntPhoneCode(code)).thenReturn("52");

        CompletableFuture<String> future = soapCountryClient.countryIntPhoneCode(code);
        assertEquals("52", future.join());
    }

    @Test
    @DisplayName("Éxito: countryFlag")
    void testCountryFlagSuccess() {
        String code = "MX";
        when(countryPort.countryFlag(code)).thenReturn("http://example.com/flag.png");

        CompletableFuture<String> future = soapCountryClient.countryFlag(code);
        assertEquals("http://example.com/flag.png", future.join());
    }

    @Test
    @DisplayName("Éxito: countryCurrency")
    void testCountryCurrencySuccess() {
        String code = "MX";
        TCurrency mockResponse = new TCurrency();
        when(countryPort.countryCurrency(code)).thenReturn(mockResponse);

        CompletableFuture<TCurrency> future = soapCountryClient.countryCurrency(code);
        assertNotNull(future.join());
    }

    @Test
    @DisplayName("Éxito: countriesUsingCurrency")
    void testCountriesUsingCurrencySuccess() {
        String code = "USD";
        ArrayOftCountryCodeAndName mockResponse = new ArrayOftCountryCodeAndName();
        when(countryPort.countriesUsingCurrency(code)).thenReturn(mockResponse);

        CompletableFuture<ArrayOftCountryCodeAndName> future = soapCountryClient.countriesUsingCurrency(code);
        assertNotNull(future.join());
    }

    @Test
    @DisplayName("Éxito: capitalCity")
    void testCapitalCitySuccess() {
        String code = "MX";
        when(countryPort.capitalCity(code)).thenReturn("Mexico City");

        CompletableFuture<String> future = soapCountryClient.capitalCity(code);
        assertEquals("Mexico City", future.join());
    }

    @Test
    @DisplayName("Éxito: languageISOCode")
    void testLanguageISOCodeSuccess() {
        String name = "Spanish";
        when(countryPort.languageISOCode(name)).thenReturn("es");

        CompletableFuture<String> future = soapCountryClient.languageISOCode(name);
        assertEquals("es", future.join());
    }

    @Test
    @DisplayName("Éxito: languageName")
    void testLanguageNameSuccess() {
        String code = "es";
        when(countryPort.languageName(code)).thenReturn("Spanish");

        CompletableFuture<String> future = soapCountryClient.languageName(code);
        assertEquals("Spanish", future.join());
    }


    // ==========================================
    // PRUEBAS DE ERROR Y FALLBACKS
    // ==========================================

    @Test
    @DisplayName("Error: Ejecución de fallback genérico (sin parámetros)")
    void testGenericFallbackExecution() {
        when(countryPort.fullCountryInfoAllCountries()).thenThrow(new RuntimeException("SOAP Connection Error"));

        CompletableFuture<ArrayOftCountryInfo> future = soapCountryClient.fullCountryInfoAllCountries();

        CompletionException exception = assertThrows(CompletionException.class, future::join);
        assertTrue(exception.getMessage().contains("Servicio SOAP de países no disponible temporalmente"));
    }

    @Test
    @DisplayName("Error: Ejecución de fallback con parámetro")
    void testFallbackWithParamExecution() {
        String invalidCode = "INVALID";
        when(countryPort.countryName(invalidCode)).thenThrow(new RuntimeException("SOAP Timeout"));

        CompletableFuture<String> future = soapCountryClient.countryName(invalidCode);

        CompletionException exception = assertThrows(CompletionException.class, future::join);
        assertTrue(exception.getMessage().contains("Servicio SOAP de la información del país no disponible temporalmente"));
    }
}