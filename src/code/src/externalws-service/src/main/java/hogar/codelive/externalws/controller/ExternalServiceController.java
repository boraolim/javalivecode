package hogar.codelive.externalws.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.lang.NonNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hogar.codelive.externalws.service.CalculatorService;
import hogar.codelive.externalws.service.CountryInfoService;
import hogar.codelive.externalws.service.JsonPlaceholderService;
import hogar.codelive.externalws.request.CalculationRequest;
import hogar.codelive.externalws.response.PostDataResponse;
import hogar.codelive.externalws.wsdl.countryinfo.TCurrency;
import hogar.codelive.externalws.wsdl.countryinfo.TCountryInfo;
import hogar.codelive.externalws.wsdl.countryinfo.ArrayOftCurrency;
import hogar.codelive.externalws.wsdl.countryinfo.ArrayOftLanguage;
import hogar.codelive.externalws.wsdl.countryinfo.ArrayOftContinent;
import hogar.codelive.externalws.wsdl.countryinfo.ArrayOftCountryInfo;
import hogar.codelive.externalws.wsdl.countryinfo.ArrayOftCountryCodeAndName;
import hogar.codelive.externalws.wsdl.countryinfo.ArrayOftCountryCodeAndNameGroupedByContinent;

@RestController
@RequestMapping("/api/v1/externalsvc")
@RequiredArgsConstructor
@Tag(name = "External Service", description = "Listado de servicios externos")
public class ExternalServiceController {
     private final CalculatorService calculatorService;
     private final CountryInfoService countryInfoService;
     private final JsonPlaceholderService jsonPlaceholderService;

     @PostMapping(value = "/calculate-add", produces = MediaType.APPLICATION_JSON_VALUE)
     @Operation(summary = "Calcular la suma de dos numeros enteros utilizando el motor SOAP externo de manera asíncrona")
     public CompletableFuture<ResponseEntity<Integer>> calculateSum(@Valid @RequestBody CalculationRequest request) {
          return calculatorService.getAddValue(request.getValorA(), request.getValorB())
               .thenApply(ResponseEntity::ok);
     }

     @PostMapping(value = "/calculate-multiply", produces = MediaType.APPLICATION_JSON_VALUE)
     @Operation(summary = "Calcular el producto de dos numeros enteros utilizando el motor SOAP externo de manera asíncrona")
     public CompletableFuture<ResponseEntity<Integer>> calculateMultiply(@Valid @RequestBody CalculationRequest request) {
          return calculatorService.getMultiplyValue(request.getValorA(), request.getValorB())
               .thenApply(ResponseEntity::ok);
     }

     @PostMapping(value = "/calculate-divide", produces = MediaType.APPLICATION_JSON_VALUE)
     @Operation(summary = "Calcular el cociente de dos numeros enteros utilizando el motor SOAP externo de manera asíncrona")
     public CompletableFuture<ResponseEntity<Integer>> calculateDivide(@Valid @RequestBody CalculationRequest request) {
          return calculatorService.getDivideValue(request.getValorA(), request.getValorB())
               .thenApply(ResponseEntity::ok);
     }

     @GetMapping(value = "/fullCountryInfoAllCountries", produces = MediaType.APPLICATION_JSON_VALUE)
     @Operation(summary = "Obtiene toda la información de los países utilizando el motor SOAP externo de manera asíncrona")
     public CompletableFuture<ResponseEntity<ArrayOftCountryInfo>> fullCountryInfoAllCountries() {
          return countryInfoService.fullCountryInfoAllCountries()
               .thenApply(ResponseEntity::ok);
     }

     @GetMapping(value = "/fullCountryInfo/{sCountryCode}", produces = MediaType.APPLICATION_JSON_VALUE)
     @Operation(summary = "Obtiene toda la información de un país en especifico utilizando el motor SOAP externo de manera asíncrona")
     public CompletableFuture<ResponseEntity<TCountryInfo>> fullCountryInfo(@NotBlank
                                                                            @Parameter(description = "Identificador del país")
                                                                            @PathVariable @NonNull String sCountryCode) {
          return countryInfoService.fullCountryInfo(sCountryCode)
               .thenApply(ResponseEntity::ok);
     }

     @GetMapping(value = "/currencyName/{sCurrencyIsoCode}", produces = MediaType.APPLICATION_JSON_VALUE)
     @Operation(summary = "Obtiene toda la información de un país en especifico por codigo ISO de la moneda del país utilizando el motor SOAP externo de manera asíncrona")
     public CompletableFuture<ResponseEntity<String>> currencyName(@NotBlank
                                                                   @Parameter(description = "Identificador del país")
                                                                   @PathVariable @NonNull String sCurrencyIsoCode) {
          return countryInfoService.currencyName(sCurrencyIsoCode)
               .thenApply(ResponseEntity::ok);
     }

     @GetMapping(value = "/countryName/{sCountryIsoCode}", produces = MediaType.APPLICATION_JSON_VALUE)
     @Operation(summary = "Obtiene toda la información de un país en especifico por codigo ISO del país utilizando el motor SOAP externo de manera asíncrona")
     public CompletableFuture<ResponseEntity<String>> countryName(@NotBlank
                                                                  @Parameter(description = "Identificador del país")
                                                                  @PathVariable @NonNull String sCountryIsoCode) {
          return countryInfoService.countryName(sCountryIsoCode)
               .thenApply(ResponseEntity::ok);
     }
     
     @GetMapping(value = "/countryISOCode/{sCountryName}", produces = MediaType.APPLICATION_JSON_VALUE)
     @Operation(summary = "Obtiene el codigo ISO de un país en especifico por nombre de un país utilizando el motor SOAP externo de manera asíncrona")
     public CompletableFuture<ResponseEntity<String>> countryISOCode(@NotBlank
                                                                     @Parameter(description = "Nombre del país")
                                                                     @PathVariable @NonNull String sCountryName) {
          return countryInfoService.countryISOCode(sCountryName)
               .thenApply(ResponseEntity::ok);
     }

     @GetMapping(value = "/countryIntPhoneCode/{sCountryIsoCode}", produces = MediaType.APPLICATION_JSON_VALUE)
     @Operation(summary = "Obtiene el número telefonico ISO de un país en especifico por codigo ISO del país utilizando el motor SOAP externo de manera asíncrona")
     public CompletableFuture<ResponseEntity<String>> countryIntPhoneCode(@NotBlank
                                                                          @Parameter(description = "Identificador del país")
                                                                          @PathVariable @NonNull String sCountryIsoCode) {
          return countryInfoService.countryIntPhoneCode(sCountryIsoCode)
               .thenApply(ResponseEntity::ok);
     }
     
     @GetMapping(value = "/countryFlag/{sCountryIsoCode}", produces = MediaType.APPLICATION_JSON_VALUE)
     @Operation(summary = "Obtiene la dirección URL de la bandera de un país en especifico por codigo ISO del país utilizando el motor SOAP externo de manera asíncrona")
     public CompletableFuture<ResponseEntity<String>> countryFlag(@NotBlank
                                                                  @Parameter(description = "Identificador del país")
                                                                  @PathVariable @NonNull String sCountryIsoCode) {
          return countryInfoService.countryFlag(sCountryIsoCode)
               .thenApply(ResponseEntity::ok);
     }
     
     @GetMapping(value = "/countryCurrency/{sCountryIsoCode}", produces = MediaType.APPLICATION_JSON_VALUE)
     @Operation(summary = "Obtiene la información de la unidad monetaria de un país en especifico por codigo ISO del país utilizando el motor SOAP externo de manera asíncrona")
     public CompletableFuture<ResponseEntity<TCurrency>> countryCurrency(@NotBlank
                                                                        @Parameter(description = "Identificador del país")
                                                                        @PathVariable @NonNull String sCountryIsoCode) {
         return countryInfoService.countryCurrency(sCountryIsoCode)
               .thenApply(ResponseEntity::ok);
     }
     
     @GetMapping(value = "/countriesUsingCurrency/{sIsoCurrencyCode}", produces = MediaType.APPLICATION_JSON_VALUE)
     @Operation(summary = "Obtiene la información de la lista de países que usan una moneda por codigo ISO de la moneda del país utilizando el motor SOAP externo de manera asíncrona")
     public CompletableFuture<ResponseEntity<ArrayOftCountryCodeAndName>> countriesUsingCurrency(@NotBlank
                                                                                                 @Parameter(description = "Identificador de la moneda del país")
                                                                                                 @PathVariable @NonNull String sIsoCurrencyCode) {
         return countryInfoService.countriesUsingCurrency(sIsoCurrencyCode)
               .thenApply(ResponseEntity::ok);
     }
     
     @GetMapping(value = "/capitalCity/{sCountryIsoCode}", produces = MediaType.APPLICATION_JSON_VALUE)
     @Operation(summary = "Obtiene el nombre de la capital de un país en especifico por codigo ISO del país utilizando el motor SOAP externo de manera asíncrona")
     public CompletableFuture<ResponseEntity<String>> capitalCity(@NotBlank
                                                                  @Parameter(description = "Identificador del país")
                                                                  @PathVariable @NonNull String sCountryIsoCode) {
          return countryInfoService.capitalCity(sCountryIsoCode)
               .thenApply(ResponseEntity::ok);
     }
     
     @GetMapping(value = "/languageISOCode/{sLanguageName}", produces = MediaType.APPLICATION_JSON_VALUE)
     @Operation(summary = "Obtiene el codigo ISO de un idioma o dialecto especifico por el nombre del idioma o dialecto utilizando el motor SOAP externo de manera asíncrona")
     public CompletableFuture<ResponseEntity<String>> languageISOCode(@NotBlank
                                                                      @Parameter(description = "Nombre del idioma o dialecto")
                                                                      @PathVariable @NonNull String sLanguageName) {
          return countryInfoService.languageISOCode(sLanguageName)
               .thenApply(ResponseEntity::ok);
     }

     @GetMapping(value = "/languageName/{sISOCode}", produces = MediaType.APPLICATION_JSON_VALUE)
     @Operation(summary = "Obtiene el nombre del idioma o dialecto especifico por el codigo ISO de un idioma o dialecto utilizando el motor SOAP externo de manera asíncrona")
     public CompletableFuture<ResponseEntity<String>> languageName(@NotBlank
                                                                   @Parameter(description = "Nombre del idioma o dialecto")
                                                                   @PathVariable @NonNull String sISOCode) {
          return countryInfoService.languageName(sISOCode)
               .thenApply(ResponseEntity::ok);
     }
     
     @GetMapping(value = "/listOfContinentsByCode", produces = MediaType.APPLICATION_JSON_VALUE)
     @Operation(summary = "Obtiene toda la información de los continentes por codigo ISO utilizando el motor SOAP externo de manera asíncrona")
     public CompletableFuture<ResponseEntity<ArrayOftContinent>> listOfContinentsByCode() {
          return countryInfoService.listOfContinentsByCode()
               .thenApply(ResponseEntity::ok);
     }
     
     @GetMapping(value = "/listOfContinentsByName", produces = MediaType.APPLICATION_JSON_VALUE)
     @Operation(summary = "Obtiene toda la información de los continentes por nombre utilizando el motor SOAP externo de manera asíncrona")
     public CompletableFuture<ResponseEntity<ArrayOftContinent>> listOfContinentsByName() {
          return countryInfoService.listOfContinentsByName()
               .thenApply(ResponseEntity::ok);
     }
     
     @GetMapping(value = "/listOfCountryNamesGroupedByContinent", produces = MediaType.APPLICATION_JSON_VALUE)
     @Operation(summary = "Obtiene toda la información de los paises agrupados por continentes utilizando el motor SOAP externo de manera asíncrona")
     public CompletableFuture<ResponseEntity<ArrayOftCountryCodeAndNameGroupedByContinent>> listOfCountryNamesGroupedByContinent() {
          return countryInfoService.listOfCountryNamesGroupedByContinent()
               .thenApply(ResponseEntity::ok);
     }
     
     @GetMapping(value = "/listOfCurrenciesByCode", produces = MediaType.APPLICATION_JSON_VALUE)
     @Operation(summary = "Obtiene toda la información de las divisas con su código ISO utilizando el motor SOAP externo de manera asíncrona")
     public CompletableFuture<ResponseEntity<ArrayOftCurrency>> listOfCurrenciesByCode() {
          return countryInfoService.listOfCurrenciesByCode()
               .thenApply(ResponseEntity::ok);
     }
     
     @GetMapping(value = "/listOfCurrenciesByName", produces = MediaType.APPLICATION_JSON_VALUE)
     @Operation(summary = "Obtiene toda la información de las divisas con su nombre utilizando el motor SOAP externo de manera asíncrona")
     public CompletableFuture<ResponseEntity<ArrayOftCurrency>> listOfCurrenciesByName() {
          return countryInfoService.listOfCurrenciesByName()
               .thenApply(ResponseEntity::ok);
     }
     
     @GetMapping(value = "/listOfLanguagesByCode", produces = MediaType.APPLICATION_JSON_VALUE)
     @Operation(summary = "Obtiene toda la información de los continentes por codigo ISO y su codigo ISO utilizando el motor SOAP externo de manera asíncrona")
     public CompletableFuture<ResponseEntity<ArrayOftLanguage>> listOfLanguagesByCode() {
          return countryInfoService.listOfLanguagesByCode()
               .thenApply(ResponseEntity::ok);
     }
     
     @GetMapping(value = "/listOfLanguagesByName", produces = MediaType.APPLICATION_JSON_VALUE)
     @Operation(summary = "Obtiene toda la información de los continentes por nombre y su codigo ISO utilizando el motor SOAP externo de manera asíncrona")
     public CompletableFuture<ResponseEntity<ArrayOftLanguage>> listOfLanguagesByName() {
          return countryInfoService.listOfLanguagesByName()
               .thenApply(ResponseEntity::ok);
     }
     
     @GetMapping(value = "/getApiPost", produces = MediaType.APPLICATION_JSON_VALUE)
     @Operation(summary = "Obtiene toda la información de post de un sitio API externo")
     public CompletableFuture<ResponseEntity<List<PostDataResponse>>> getApiPost() {
          return jsonPlaceholderService.getAllPosts()
               .thenApply(ResponseEntity::ok);
     } 
}