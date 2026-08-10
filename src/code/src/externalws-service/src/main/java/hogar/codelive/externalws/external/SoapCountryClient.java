package hogar.codelive.externalws.external;

import java.util.function.Supplier;
import java.util.concurrent.CompletableFuture;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import hogar.codelive.externalws.constants.SoapConstants;
import hogar.codelive.externalws.wsdl.countryinfo.TCurrency;
import hogar.codelive.externalws.wsdl.countryinfo.TCountryInfo;
import hogar.codelive.externalws.wsdl.countryinfo.ArrayOftCurrency;
import hogar.codelive.externalws.wsdl.countryinfo.ArrayOftLanguage;
import hogar.codelive.externalws.wsdl.countryinfo.ArrayOftContinent;
import hogar.codelive.externalws.wsdl.countryinfo.ArrayOftCountryInfo;
import hogar.codelive.externalws.wsdl.countryinfo.ArrayOftCountryCodeAndName;
import hogar.codelive.externalws.wsdl.countryinfo.CountryInfoServiceSoapType;
import hogar.codelive.externalws.wsdl.countryinfo.ArrayOftCountryCodeAndNameGroupedByContinent;

@Slf4j
@Component
@RequiredArgsConstructor
public class SoapCountryClient {
    private final CountryInfoServiceSoapType countryPort;

    // --- Métodos sin parámetros ---

    @Retry(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY)
    @CircuitBreaker(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackGeneric")
    @TimeLimiter(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackGeneric")
    public CompletableFuture<ArrayOftCountryInfo> fullCountryInfoAllCountries() {
        return executeAsync("fullCountryInfoAllCountries", countryPort::fullCountryInfoAllCountries);
    }

    @Retry(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY)
    @CircuitBreaker(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackGeneric")
    @TimeLimiter(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackGeneric")
    public CompletableFuture<ArrayOftContinent> listOfContinentsByCode() {
        return executeAsync("listOfContinentsByCode", countryPort::listOfContinentsByCode);
    }

    @Retry(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY)
    @CircuitBreaker(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackGeneric")
    @TimeLimiter(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackGeneric")
    public CompletableFuture<ArrayOftContinent> listOfContinentsByName() {
        return executeAsync("listOfContinentsByName", countryPort::listOfContinentsByName);
    }

    @Retry(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY)
    @CircuitBreaker(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackGeneric")
    @TimeLimiter(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackGeneric")
    public CompletableFuture<ArrayOftCountryCodeAndName> listOfCountryNamesByCode() {
        return executeAsync("listOfCountryNamesByCode", countryPort::listOfCountryNamesByCode);
    }

    @Retry(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY)
    @CircuitBreaker(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackGeneric")
    @TimeLimiter(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackGeneric")
    public CompletableFuture<ArrayOftCountryCodeAndName> listOfCountryNamesByName() {
        return executeAsync("listOfCountryNamesByName", countryPort::listOfCountryNamesByName);
    }

    @Retry(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY)
    @CircuitBreaker(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackGeneric")
    @TimeLimiter(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackGeneric")
    public CompletableFuture<ArrayOftCountryCodeAndNameGroupedByContinent> listOfCountryNamesGroupedByContinent() {
        return executeAsync("listOfCountryNamesGroupedByContinent", countryPort::listOfCountryNamesGroupedByContinent);
    }

    @Retry(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY)
    @CircuitBreaker(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackGeneric")
    @TimeLimiter(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackGeneric")
    public CompletableFuture<ArrayOftCurrency> listOfCurrenciesByCode() {
        return executeAsync("listOfCurrenciesByCode", countryPort::listOfCurrenciesByCode);
    }

    @Retry(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY)
    @CircuitBreaker(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackGeneric")
    @TimeLimiter(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackGeneric")
    public CompletableFuture<ArrayOftCurrency> listOfCurrenciesByName() {
        return executeAsync("listOfCurrenciesByName", countryPort::listOfCurrenciesByName);
    }

    @Retry(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY)
    @CircuitBreaker(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackGeneric")
    @TimeLimiter(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackGeneric")
    public CompletableFuture<ArrayOftLanguage> listOfLanguagesByCode() {
        return executeAsync("listOfLanguagesByCode", countryPort::listOfLanguagesByCode);
    }

    @Retry(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY)
    @CircuitBreaker(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackGeneric")
    @TimeLimiter(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackGeneric")
    public CompletableFuture<ArrayOftLanguage> listOfLanguagesByName() {
        return executeAsync("listOfLanguagesByName", countryPort::listOfLanguagesByName);
    }


    // --- Métodos con un parámetro (String) ---

    @Retry(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY)
    @CircuitBreaker(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackWithParam")
    @TimeLimiter(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackWithParam")
    public CompletableFuture<TCountryInfo> fullCountryInfo(String code) {
        return executeAsync("fullCountryInfo", () -> countryPort.fullCountryInfo(code));
    }

    @Retry(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY)
    @CircuitBreaker(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackWithParam")
    @TimeLimiter(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackWithParam")
    public CompletableFuture<String> currencyName(String code) {
        return executeAsync("currencyName", () -> countryPort.currencyName(code));
    }

    @Retry(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY)
    @CircuitBreaker(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackWithParam")
    @TimeLimiter(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackWithParam")
    public CompletableFuture<String> countryName(String code) {
        return executeAsync("countryName", () -> countryPort.countryName(code));
    }

    @Retry(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY)
    @CircuitBreaker(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackWithParam")
    @TimeLimiter(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackWithParam")
    public CompletableFuture<String> countryISOCode(String name) {
        return executeAsync("countryISOCode", () -> countryPort.countryISOCode(name));
    }

    @Retry(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY)
    @CircuitBreaker(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackWithParam")
    @TimeLimiter(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackWithParam")
    public CompletableFuture<String> countryIntPhoneCode(String code) {
        return executeAsync("countryIntPhoneCode", () -> countryPort.countryIntPhoneCode(code));
    }

    @Retry(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY)
    @CircuitBreaker(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackWithParam")
    @TimeLimiter(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackWithParam")
    public CompletableFuture<String> countryFlag(String code) {
        return executeAsync("countryFlag", () -> countryPort.countryFlag(code));
    }

    @Retry(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY)
    @CircuitBreaker(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackWithParam")
    @TimeLimiter(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackWithParam")
    public CompletableFuture<TCurrency> countryCurrency(String code) {
        return executeAsync("countryCurrency", () -> countryPort.countryCurrency(code));
    }

    @Retry(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY)
    @CircuitBreaker(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackWithParam")
    @TimeLimiter(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackWithParam")
    public CompletableFuture<ArrayOftCountryCodeAndName> countriesUsingCurrency(String code) {
        return executeAsync("countriesUsingCurrency", () -> countryPort.countriesUsingCurrency(code));
    }

    @Retry(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY)
    @CircuitBreaker(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackWithParam")
    @TimeLimiter(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackWithParam")
    public CompletableFuture<String> capitalCity(String code) {
        return executeAsync("capitalCity", () -> countryPort.capitalCity(code));
    }

    @Retry(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY)
    @CircuitBreaker(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackWithParam")
    @TimeLimiter(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackWithParam")
    public CompletableFuture<String> languageISOCode(String name) {
        return executeAsync("languageISOCode", () -> countryPort.languageISOCode(name));
    }

    @Retry(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY)
    @CircuitBreaker(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackWithParam")
    @TimeLimiter(name = SoapConstants.RESILIENCE_INSTANCE_COUNTRY, fallbackMethod = "fallbackWithParam")
    public CompletableFuture<String> languageName(String code) {
        return executeAsync("languageName", () -> countryPort.languageName(code));
    }


    // --- Métodos auxiliares de ejecución asíncrona ---

    private <T> CompletableFuture<T> executeAsync(String methodName, Supplier<T> soapAction) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Executing SOAP Call (WSDL) to Country Service -> Method: '{}'.", methodName);
            return soapAction.get();
        });
    }


    // --- Fallbacks unificados (Genéricos) ---

    public <T> CompletableFuture<T> fallbackGeneric(Throwable ex) {
        log.error("SOAP Country Service unavailable or failed. Executing local fallback. Cause: {}", ex.getMessage(), ex);
        return CompletableFuture.failedFuture(new RuntimeException("Servicio SOAP de países no disponible temporalmente", ex));
    }

    public <T> CompletableFuture<T> fallbackWithParam(String valueString, Throwable ex) {
        log.error("SOAP Country Service unavailable or failed. Executing local fallback. Value: [{}]. Cause: {}", valueString, ex.getMessage(), ex);
        return CompletableFuture.failedFuture(new RuntimeException("Servicio SOAP de la información del país no disponible temporalmente", ex));
    }
}