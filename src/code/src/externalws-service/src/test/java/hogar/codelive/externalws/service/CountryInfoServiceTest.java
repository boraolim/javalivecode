package hogar.codelive.externalws.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import hogar.codelive.externalws.external.SoapCountryClient;
import hogar.codelive.externalws.wsdl.countryinfo.TCurrency;
import hogar.codelive.externalws.wsdl.countryinfo.TCountryInfo;
import hogar.codelive.externalws.wsdl.countryinfo.ArrayOftCurrency;
import hogar.codelive.externalws.wsdl.countryinfo.ArrayOftLanguage;
import hogar.codelive.externalws.wsdl.countryinfo.ArrayOftContinent;
import hogar.codelive.externalws.wsdl.countryinfo.ArrayOftCountryInfo;
import hogar.codelive.externalws.wsdl.countryinfo.ArrayOftCountryCodeAndName;
import hogar.codelive.externalws.wsdl.countryinfo.ArrayOftCountryCodeAndNameGroupedByContinent;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@DisplayName("CountryInfoServiceTest - Unit Tests")
class CountryInfoServiceTest {

    @Mock
    private SoapCountryClient soapCountryClient;

    @InjectMocks
    private CountryInfoService countryInfoService;

    // ==========================================
    // PRUEBAS DE ÉXITO - MÉTODOS SIN PARÁMETROS
    // ==========================================

    @Test
    @DisplayName("Éxito: fullCountryInfoAllCountries")
    void testFullCountryInfoAllCountriesSuccess() {
        ArrayOftCountryInfo mockData = new ArrayOftCountryInfo();
        when(soapCountryClient.fullCountryInfoAllCountries()).thenReturn(CompletableFuture.completedFuture(mockData));

        CompletableFuture<ArrayOftCountryInfo> future = countryInfoService.fullCountryInfoAllCountries();
        assertNotNull(future.join());
    }

    @Test
    @DisplayName("Éxito: listOfContinentsByCode")
    void testListOfContinentsByCodeSuccess() {
        ArrayOftContinent mockData = new ArrayOftContinent();
        when(soapCountryClient.listOfContinentsByCode()).thenReturn(CompletableFuture.completedFuture(mockData));

        CompletableFuture<ArrayOftContinent> future = countryInfoService.listOfContinentsByCode();
        assertNotNull(future.join());
    }

    @Test
    @DisplayName("Éxito: listOfContinentsByName")
    void testListOfContinentsByNameSuccess() {
        ArrayOftContinent mockData = new ArrayOftContinent();
        when(soapCountryClient.listOfContinentsByName()).thenReturn(CompletableFuture.completedFuture(mockData));

        CompletableFuture<ArrayOftContinent> future = countryInfoService.listOfContinentsByName();
        assertNotNull(future.join());
    }

    @Test
    @DisplayName("Éxito: listOfCountryNamesByCode")
    void testListOfCountryNamesByCodeSuccess() {
        ArrayOftCountryCodeAndName mockData = new ArrayOftCountryCodeAndName();
        when(soapCountryClient.listOfCountryNamesByCode()).thenReturn(CompletableFuture.completedFuture(mockData));

        CompletableFuture<ArrayOftCountryCodeAndName> future = countryInfoService.listOfCountryNamesByCode();
        assertNotNull(future.join());
    }

    @Test
    @DisplayName("Éxito: listOfCountryNamesByName")
    void testListOfCountryNamesByNameSuccess() {
        ArrayOftCountryCodeAndName mockData = new ArrayOftCountryCodeAndName();
        when(soapCountryClient.listOfCountryNamesByName()).thenReturn(CompletableFuture.completedFuture(mockData));

        CompletableFuture<ArrayOftCountryCodeAndName> future = countryInfoService.listOfCountryNamesByName();
        assertNotNull(future.join());
    }

    @Test
    @DisplayName("Éxito: listOfCountryNamesGroupedByContinent")
    void testListOfCountryNamesGroupedByContinentSuccess() {
        ArrayOftCountryCodeAndNameGroupedByContinent mockData = new ArrayOftCountryCodeAndNameGroupedByContinent();
        when(soapCountryClient.listOfCountryNamesGroupedByContinent()).thenReturn(CompletableFuture.completedFuture(mockData));

        CompletableFuture<ArrayOftCountryCodeAndNameGroupedByContinent> future = countryInfoService.listOfCountryNamesGroupedByContinent();
        assertNotNull(future.join());
    }

    @Test
    @DisplayName("Éxito: listOfCurrenciesByCode")
    void testListOfCurrenciesByCodeSuccess() {
        ArrayOftCurrency mockData = new ArrayOftCurrency();
        when(soapCountryClient.listOfCurrenciesByCode()).thenReturn(CompletableFuture.completedFuture(mockData));

        CompletableFuture<ArrayOftCurrency> future = countryInfoService.listOfCurrenciesByCode();
        assertNotNull(future.join());
    }

    @Test
    @DisplayName("Éxito: listOfCurrenciesByName")
    void testListOfCurrenciesByNameSuccess() {
        ArrayOftCurrency mockData = new ArrayOftCurrency();
        when(soapCountryClient.listOfCurrenciesByName()).thenReturn(CompletableFuture.completedFuture(mockData));

        CompletableFuture<ArrayOftCurrency> future = countryInfoService.listOfCurrenciesByName();
        assertNotNull(future.join());
    }

    @Test
    @DisplayName("Éxito: listOfLanguagesByCode")
    void testListOfLanguagesByCodeSuccess() {
        ArrayOftLanguage mockData = new ArrayOftLanguage();
        when(soapCountryClient.listOfLanguagesByCode()).thenReturn(CompletableFuture.completedFuture(mockData));

        CompletableFuture<ArrayOftLanguage> future = countryInfoService.listOfLanguagesByCode();
        assertNotNull(future.join());
    }

    @Test
    @DisplayName("Éxito: listOfLanguagesByName")
    void testListOfLanguagesByNameSuccess() {
        ArrayOftLanguage mockData = new ArrayOftLanguage();
        when(soapCountryClient.listOfLanguagesByName()).thenReturn(CompletableFuture.completedFuture(mockData));

        CompletableFuture<ArrayOftLanguage> future = countryInfoService.listOfLanguagesByName();
        assertNotNull(future.join());
    }


    // ==========================================
    // PRUEBAS DE ÉXITO - MÉTODOS CON UN PARÁMETRO
    // ==========================================

    @Test
    @DisplayName("Éxito: fullCountryInfo")
    void testFullCountryInfoSuccess() {
        String code = "MX";
        TCountryInfo mockData = new TCountryInfo();
        when(soapCountryClient.fullCountryInfo(code)).thenReturn(CompletableFuture.completedFuture(mockData));

        CompletableFuture<TCountryInfo> future = countryInfoService.fullCountryInfo(code);
        assertNotNull(future.join());
    }

    @Test
    @DisplayName("Éxito: currencyName")
    void testCurrencyNameSuccess() {
        String code = "USD";
        when(soapCountryClient.currencyName(code)).thenReturn(CompletableFuture.completedFuture("US Dollar"));

        CompletableFuture<String> future = countryInfoService.currencyName(code);
        assertEquals("US Dollar", future.join());
    }

    @Test
    @DisplayName("Éxito: countryName")
    void testCountryNameSuccess() {
        String code = "MX";
        when(soapCountryClient.countryName(code)).thenReturn(CompletableFuture.completedFuture("Mexico"));

        CompletableFuture<String> future = countryInfoService.countryName(code);
        assertEquals("Mexico", future.join());
    }

    @Test
    @DisplayName("Éxito: countryISOCode")
    void testCountryISOCodeSuccess() {
        String name = "Mexico";
        when(soapCountryClient.countryISOCode(name)).thenReturn(CompletableFuture.completedFuture("MX"));

        CompletableFuture<String> future = countryInfoService.countryISOCode(name);
        assertEquals("MX", future.join());
    }

    @Test
    @DisplayName("Éxito: countryIntPhoneCode")
    void testCountryIntPhoneCodeSuccess() {
        String code = "MX";
        when(soapCountryClient.countryIntPhoneCode(code)).thenReturn(CompletableFuture.completedFuture("52"));

        CompletableFuture<String> future = countryInfoService.countryIntPhoneCode(code);
        assertEquals("52", future.join());
    }

    @Test
    @DisplayName("Éxito: countryFlag")
    void testCountryFlagSuccess() {
        String code = "MX";
        when(soapCountryClient.countryFlag(code)).thenReturn(CompletableFuture.completedFuture("http://flag.com"));

        CompletableFuture<String> future = countryInfoService.countryFlag(code);
        assertEquals("http://flag.com", future.join());
    }

    @Test
    @DisplayName("Éxito: countryCurrency")
    void testCountryCurrencySuccess() {
        String code = "MX";
        TCurrency mockData = new TCurrency();
        when(soapCountryClient.countryCurrency(code)).thenReturn(CompletableFuture.completedFuture(mockData));

        CompletableFuture<TCurrency> future = countryInfoService.countryCurrency(code);
        assertNotNull(future.join());
    }

    @Test
    @DisplayName("Éxito: countriesUsingCurrency")
    void testCountriesUsingCurrencySuccess() {
        String code = "USD";
        ArrayOftCountryCodeAndName mockData = new ArrayOftCountryCodeAndName();
        when(soapCountryClient.countriesUsingCurrency(code)).thenReturn(CompletableFuture.completedFuture(mockData));

        CompletableFuture<ArrayOftCountryCodeAndName> future = countryInfoService.countriesUsingCurrency(code);
        assertNotNull(future.join());
    }

    @Test
    @DisplayName("Éxito: capitalCity")
    void testCapitalCitySuccess() {
        String code = "MX";
        when(soapCountryClient.capitalCity(code)).thenReturn(CompletableFuture.completedFuture("Mexico City"));

        CompletableFuture<String> future = countryInfoService.capitalCity(code);
        assertEquals("Mexico City", future.join());
    }

    @Test
    @DisplayName("Éxito: languageISOCode")
    void testLanguageISOCodeSuccess() {
        String name = "Spanish";
        when(soapCountryClient.languageISOCode(name)).thenReturn(CompletableFuture.completedFuture("es"));

        CompletableFuture<String> future = countryInfoService.languageISOCode(name);
        assertEquals("es", future.join());
    }

    @Test
    @DisplayName("Éxito: languageName")
    void testLanguageNameSuccess() {
        String code = "es";
        when(soapCountryClient.languageName(code)).thenReturn(CompletableFuture.completedFuture("Spanish"));

        CompletableFuture<String> future = countryInfoService.languageName(code);
        assertEquals("Spanish", future.join());
    }


    // ==========================================
    // PRUEBAS DE ERROR Y PROPAGACIÓN
    // ==========================================

    @Test
    @DisplayName("Error: Propagación de fallo cuando el cliente falla sin parámetros")
    void testServiceErrorPropagationWithoutParams() {
        RuntimeException expectedEx = new RuntimeException("Error en servicio SOAP");
        when(soapCountryClient.fullCountryInfoAllCountries()).thenReturn(CompletableFuture.failedFuture(expectedEx));

        CompletableFuture<ArrayOftCountryInfo> future = countryInfoService.fullCountryInfoAllCountries();

        CompletionException exception = assertThrows(CompletionException.class, future::join);
        assertEquals(expectedEx, exception.getCause());
    }

    @Test
    @DisplayName("Error: Propagación de fallo cuando el cliente falla con parámetro")
    void testServiceErrorPropagationWithParam() {
        String code = "XX";
        RuntimeException expectedEx = new RuntimeException("País no encontrado");
        when(soapCountryClient.countryName(code)).thenReturn(CompletableFuture.failedFuture(expectedEx));

        CompletableFuture<String> future = countryInfoService.countryName(code);

        CompletionException exception = assertThrows(CompletionException.class, future::join);
        assertEquals(expectedEx, exception.getCause());
    }
}