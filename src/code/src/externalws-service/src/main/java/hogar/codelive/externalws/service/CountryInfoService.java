package hogar.codelive.externalws.service;

import java.util.function.Supplier;
import java.util.concurrent.CompletableFuture;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;

import hogar.codelive.externalws.constants.LogConstants;
import hogar.codelive.externalws.external.SoapCountryClient;
import hogar.codelive.externalws.wsdl.countryinfo.TCurrency;
import hogar.codelive.externalws.wsdl.countryinfo.TCountryInfo;
import hogar.codelive.externalws.wsdl.countryinfo.ArrayOftCurrency;
import hogar.codelive.externalws.wsdl.countryinfo.ArrayOftLanguage;
import hogar.codelive.externalws.wsdl.countryinfo.ArrayOftContinent;
import hogar.codelive.externalws.wsdl.countryinfo.ArrayOftCountryInfo;
import hogar.codelive.externalws.wsdl.countryinfo.ArrayOftCountryCodeAndName;
import hogar.codelive.externalws.wsdl.countryinfo.ArrayOftCountryCodeAndNameGroupedByContinent;

@Slf4j
@Service
@RequiredArgsConstructor
public class CountryInfoService {
    private final SoapCountryClient soapCountryInfoClient;

    // --- Métodos sin parámetros ---

    @CacheEvict(value = "countryOperationCache", allEntries = true)
    public CompletableFuture<ArrayOftCountryInfo> fullCountryInfoAllCountries() {
        return executeAndLog(soapCountryInfoClient::fullCountryInfoAllCountries, LogConstants.LOG_MESSAGE_FULL_COUNTRY_ALL);
    }

    @CacheEvict(value = "countryOperationCache", allEntries = true)
    public CompletableFuture<ArrayOftContinent> listOfContinentsByCode() {
        return executeAndLog(soapCountryInfoClient::listOfContinentsByCode, LogConstants.LOG_MESSAGE_COUNTRY_OK);
    }

    @CacheEvict(value = "countryOperationCache", allEntries = true)
    public CompletableFuture<ArrayOftContinent> listOfContinentsByName() {
        return executeAndLog(soapCountryInfoClient::listOfContinentsByName, LogConstants.LOG_MESSAGE_COUNTRY_OK);
    }

    @CacheEvict(value = "countryOperationCache", allEntries = true)
    public CompletableFuture<ArrayOftCountryCodeAndName> listOfCountryNamesByCode() {
        return executeAndLog(soapCountryInfoClient::listOfCountryNamesByCode, LogConstants.LOG_MESSAGE_COUNTRY_OK);
    }

    @CacheEvict(value = "countryOperationCache", allEntries = true)
    public CompletableFuture<ArrayOftCountryCodeAndName> listOfCountryNamesByName() {
        return executeAndLog(soapCountryInfoClient::listOfCountryNamesByName, LogConstants.LOG_MESSAGE_COUNTRY_OK);
    }

    @CacheEvict(value = "countryOperationCache", allEntries = true)
    public CompletableFuture<ArrayOftCountryCodeAndNameGroupedByContinent> listOfCountryNamesGroupedByContinent() {
        return executeAndLog(soapCountryInfoClient::listOfCountryNamesGroupedByContinent, LogConstants.LOG_MESSAGE_COUNTRY_OK);
    }

    @CacheEvict(value = "countryOperationCache", allEntries = true)
    public CompletableFuture<ArrayOftCurrency> listOfCurrenciesByCode() {
        return executeAndLog(soapCountryInfoClient::listOfCurrenciesByCode, LogConstants.LOG_MESSAGE_COUNTRY_OK);
    }

    @CacheEvict(value = "countryOperationCache", allEntries = true)
    public CompletableFuture<ArrayOftCurrency> listOfCurrenciesByName() {
        return executeAndLog(soapCountryInfoClient::listOfCurrenciesByName, LogConstants.LOG_MESSAGE_COUNTRY_OK);
    }

    @CacheEvict(value = "countryOperationCache", allEntries = true)
    public CompletableFuture<ArrayOftLanguage> listOfLanguagesByCode() {
        return executeAndLog(soapCountryInfoClient::listOfLanguagesByCode, LogConstants.LOG_MESSAGE_COUNTRY_OK);
    }

    @CacheEvict(value = "countryOperationCache", allEntries = true)
    public CompletableFuture<ArrayOftLanguage> listOfLanguagesByName() {
        return executeAndLog(soapCountryInfoClient::listOfLanguagesByName, LogConstants.LOG_MESSAGE_COUNTRY_OK);
    }


    // --- Métodos con un parámetro (String) ---

    @CacheEvict(value = "countryOperationCache", allEntries = true)
    public CompletableFuture<TCountryInfo> fullCountryInfo(String sCountryCode) {
        return executeAndLog(() -> soapCountryInfoClient.fullCountryInfo(sCountryCode), LogConstants.LOG_MESSAGE_COUNTRY_OK);
    }

    @CacheEvict(value = "countryOperationCache", allEntries = true)
    public CompletableFuture<String> currencyName(String sCurrencyIsoCode) {
        return executeAndLog(() -> soapCountryInfoClient.currencyName(sCurrencyIsoCode), LogConstants.LOG_MESSAGE_COUNTRY_OK);
    }

    @CacheEvict(value = "countryOperationCache", allEntries = true)
    public CompletableFuture<String> countryName(String sCountryIsoCode) {
        return executeAndLog(() -> soapCountryInfoClient.countryName(sCountryIsoCode), LogConstants.LOG_MESSAGE_COUNTRY_OK);
    }

    @CacheEvict(value = "countryOperationCache", allEntries = true)
    public CompletableFuture<String> countryISOCode(String sCountryName) {
        return executeAndLog(() -> soapCountryInfoClient.countryISOCode(sCountryName), LogConstants.LOG_MESSAGE_COUNTRY_OK);
    }

    @CacheEvict(value = "countryOperationCache", allEntries = true)
    public CompletableFuture<String> countryIntPhoneCode(String sCountryIsoCode) {
        return executeAndLog(() -> soapCountryInfoClient.countryIntPhoneCode(sCountryIsoCode), LogConstants.LOG_MESSAGE_COUNTRY_OK);
    }

    @CacheEvict(value = "countryOperationCache", allEntries = true)
    public CompletableFuture<String> countryFlag(String sCountryIsoCode) {
        return executeAndLog(() -> soapCountryInfoClient.countryFlag(sCountryIsoCode), LogConstants.LOG_MESSAGE_COUNTRY_OK);
    }

    @CacheEvict(value = "countryOperationCache", allEntries = true)
    public CompletableFuture<TCurrency> countryCurrency(String sCountryIsoCode) {
        return executeAndLog(() -> soapCountryInfoClient.countryCurrency(sCountryIsoCode), LogConstants.LOG_MESSAGE_COUNTRY_OK);
    }

    @CacheEvict(value = "countryOperationCache", allEntries = true)
    public CompletableFuture<ArrayOftCountryCodeAndName> countriesUsingCurrency(String sISOCurrencyCode) {
        return executeAndLog(() -> soapCountryInfoClient.countriesUsingCurrency(sISOCurrencyCode), LogConstants.LOG_MESSAGE_COUNTRY_OK);
    }

    @CacheEvict(value = "countryOperationCache", allEntries = true)
    public CompletableFuture<String> capitalCity(String sCountryIsoCode) {
        return executeAndLog(() -> soapCountryInfoClient.capitalCity(sCountryIsoCode), LogConstants.LOG_MESSAGE_COUNTRY_OK);
    }

    @CacheEvict(value = "countryOperationCache", allEntries = true)
    public CompletableFuture<String> languageISOCode(String sLanguageName) {
        return executeAndLog(() -> soapCountryInfoClient.languageISOCode(sLanguageName), LogConstants.LOG_MESSAGE_COUNTRY_OK);
    }

    @CacheEvict(value = "countryOperationCache", allEntries = true)
    public CompletableFuture<String> languageName(String sISOCode) {
        return executeAndLog(() -> soapCountryInfoClient.languageName(sISOCode), LogConstants.LOG_MESSAGE_COUNTRY_OK);
    }


    // --- Método auxiliar genérico para centralizar log y llamada ---

    private <T> CompletableFuture<T> executeAndLog(Supplier<CompletableFuture<T>> action, String logTemplate) {
        log.info(LogConstants.LOG_START_COUNTRY_OPERATION);
        return action.get().thenApply(resultado -> {
            log.info(logTemplate, resultado);
            return resultado;
        });
    }
}