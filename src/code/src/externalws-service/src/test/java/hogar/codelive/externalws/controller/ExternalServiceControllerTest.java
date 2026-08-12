package hogar.codelive.externalws.controller;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import hogar.codelive.externalws.service.CalculatorService;
import hogar.codelive.externalws.config.AsyncConfiguration;
import hogar.codelive.externalws.response.PostDataResponse;
import hogar.codelive.externalws.wsdl.countryinfo.TCurrency;
import hogar.codelive.externalws.request.CalculationRequest;
import hogar.codelive.externalws.service.CountryInfoService;
import hogar.codelive.externalws.wsdl.countryinfo.TCountryInfo;
import hogar.codelive.externalws.service.JsonPlaceholderService;
import hogar.codelive.externalws.middleware.HttpLoggingInterceptor;
import hogar.codelive.externalws.wsdl.countryinfo.ArrayOftCurrency;
import hogar.codelive.externalws.wsdl.countryinfo.ArrayOftLanguage;
import hogar.codelive.externalws.wsdl.countryinfo.ArrayOftContinent;
import hogar.codelive.externalws.wsdl.countryinfo.ArrayOftCountryInfo;
import hogar.codelive.externalws.wsdl.countryinfo.ArrayOftCountryCodeAndName;
import hogar.codelive.externalws.wsdl.countryinfo.ArrayOftCountryCodeAndNameGroupedByContinent;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;

@ExtendWith(MockitoExtension.class)
@Import({AsyncConfiguration.class})
@DisplayName("ExternalServiceController - Unit Tests")
class ExternalServiceControllerCountryTest {

    private MockMvc mockMvc;

    @Mock
    private CalculatorService calculatorService;

    @Mock
    private CountryInfoService countryInfoService;

    @Mock
    private JsonPlaceholderService jsonPlaceholderService;

    @InjectMocks
    private ExternalServiceController externalServiceController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(externalServiceController)
            .addInterceptors(new HttpLoggingInterceptor())
            .build();
    }

    // ==========================================
    // MÉTODOS SIN PARÁMETROS
    // ==========================================

    @Test
    @DisplayName("EXITO: Debería retornar información de todos los países de forma asíncrona")
    void fullCountryInfoAllCountries_debeRetornarExitosoAsync() throws Exception {
        ArrayOftCountryInfo expectedResult = new ArrayOftCountryInfo();
        when(countryInfoService.fullCountryInfoAllCountries())
            .thenReturn(CompletableFuture.completedFuture(expectedResult));

        var mvcResult = mockMvc.perform(get("/api/v1/externalsvc/fullCountryInfoAllCountries")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk());

        verify(countryInfoService, times(1)).fullCountryInfoAllCountries();
    }

    @Test
    @DisplayName("EXITO: Debería retornar continentes por código de forma asíncrona")
    void listOfContinentsByCode_debeRetornarExitosoAsync() throws Exception {
        ArrayOftContinent expectedResult = new ArrayOftContinent();
        when(countryInfoService.listOfContinentsByCode())
            .thenReturn(CompletableFuture.completedFuture(expectedResult));

        var mvcResult = mockMvc.perform(get("/api/v1/externalsvc/listOfContinentsByCode")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk());

        verify(countryInfoService, times(1)).listOfContinentsByCode();
    }

    @Test
    @DisplayName("EXITO: Debería retornar continentes por nombre de forma asíncrona")
    void listOfContinentsByName_debeRetornarExitosoAsync() throws Exception {
        ArrayOftContinent expectedResult = new ArrayOftContinent();
        when(countryInfoService.listOfContinentsByName())
            .thenReturn(CompletableFuture.completedFuture(expectedResult));

        var mvcResult = mockMvc.perform(get("/api/v1/externalsvc/listOfContinentsByName")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk());

        verify(countryInfoService, times(1)).listOfContinentsByName();
    }

    @Test
    @DisplayName("EXITO: Debería retornar nombres de países agrupados por continente de forma asíncrona")
    void listOfCountryNamesGroupedByContinent_debeRetornarExitosoAsync() throws Exception {
        ArrayOftCountryCodeAndNameGroupedByContinent expectedResult = new ArrayOftCountryCodeAndNameGroupedByContinent();
        when(countryInfoService.listOfCountryNamesGroupedByContinent())
            .thenReturn(CompletableFuture.completedFuture(expectedResult));

        var mvcResult = mockMvc.perform(get("/api/v1/externalsvc/listOfCountryNamesGroupedByContinent")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk());

        verify(countryInfoService, times(1)).listOfCountryNamesGroupedByContinent();
    }

    @Test
    @DisplayName("EXITO: Debería retornar monedas por código de forma asíncrona")
    void listOfCurrenciesByCode_debeRetornarExitosoAsync() throws Exception {
        ArrayOftCurrency expectedResult = new ArrayOftCurrency();
        when(countryInfoService.listOfCurrenciesByCode())
            .thenReturn(CompletableFuture.completedFuture(expectedResult));

        var mvcResult = mockMvc.perform(get("/api/v1/externalsvc/listOfCurrenciesByCode")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk());

        verify(countryInfoService, times(1)).listOfCurrenciesByCode();
    }

    @Test
    @DisplayName("EXITO: Debería retornar monedas por nombre de forma asíncrona")
    void listOfCurrenciesByName_debeRetornarExitosoAsync() throws Exception {
        ArrayOftCurrency expectedResult = new ArrayOftCurrency();
        when(countryInfoService.listOfCurrenciesByName())
            .thenReturn(CompletableFuture.completedFuture(expectedResult));

        var mvcResult = mockMvc.perform(get("/api/v1/externalsvc/listOfCurrenciesByName")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk());

        verify(countryInfoService, times(1)).listOfCurrenciesByName();
    }

    @Test
    @DisplayName("EXITO: Debería retornar lenguajes por código de forma asíncrona")
    void listOfLanguagesByCode_debeRetornarExitosoAsync() throws Exception {
        ArrayOftLanguage expectedResult = new ArrayOftLanguage();
        when(countryInfoService.listOfLanguagesByCode())
            .thenReturn(CompletableFuture.completedFuture(expectedResult));

        var mvcResult = mockMvc.perform(get("/api/v1/externalsvc/listOfLanguagesByCode")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk());

        verify(countryInfoService, times(1)).listOfLanguagesByCode();
    }

    @Test
    @DisplayName("EXITO: Debería retornar lenguajes por nombre de forma asíncrona")
    void listOfLanguagesByName_debeRetornarExitosoAsync() throws Exception {
        ArrayOftLanguage expectedResult = new ArrayOftLanguage();
        when(countryInfoService.listOfLanguagesByName())
            .thenReturn(CompletableFuture.completedFuture(expectedResult));

        var mvcResult = mockMvc.perform(get("/api/v1/externalsvc/listOfLanguagesByName")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk());

        verify(countryInfoService, times(1)).listOfLanguagesByName();
    }


    // ==========================================
    // MÉTODOS CON UN PARÁMETRO (PATH VARIABLE)
    // ==========================================

    @Test
    @DisplayName("EXITO: Debería retornar fullCountryInfo por código de país de forma asíncrona")
    void fullCountryInfo_debeRetornarExitosoAsync() throws Exception {
        String countryCode = "MX";
        TCountryInfo expectedResult = new TCountryInfo();
        when(countryInfoService.fullCountryInfo(countryCode))
            .thenReturn(CompletableFuture.completedFuture(expectedResult));

        var mvcResult = mockMvc.perform(get("/api/v1/externalsvc/fullCountryInfo/" + countryCode)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk());

        verify(countryInfoService, times(1)).fullCountryInfo(countryCode);
    }

    @Test
    @DisplayName("EXITO: Debería retornar el nombre de la moneda por código ISO de forma asíncrona")
    void currencyName_debeRetornarExitosoAsync() throws Exception {
        String currencyCode = "USD";
        String expectedResult = "US Dollar";
        when(countryInfoService.currencyName(currencyCode))
            .thenReturn(CompletableFuture.completedFuture(expectedResult));

        var mvcResult = mockMvc.perform(get("/api/v1/externalsvc/currencyName/" + currencyCode)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk());

        verify(countryInfoService, times(1)).currencyName(currencyCode);
    }

    @Test
    @DisplayName("EXITO: Debería retornar el nombre del país por código ISO de forma asíncrona")
    void countryName_debeRetornarExitosoAsync() throws Exception {
        String countryCode = "MX";
        String expectedResult = "Mexico";
        when(countryInfoService.countryName(countryCode))
            .thenReturn(CompletableFuture.completedFuture(expectedResult));

        var mvcResult = mockMvc.perform(get("/api/v1/externalsvc/countryName/" + countryCode)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk());

        verify(countryInfoService, times(1)).countryName(countryCode);
    }

    @Test
    @DisplayName("EXITO: Debería retornar el código ISO por nombre de país de forma asíncrona")
    void countryISOCode_debeRetornarExitosoAsync() throws Exception {
        String countryName = "Mexico";
        String expectedResult = "MX";
        when(countryInfoService.countryISOCode(countryName))
            .thenReturn(CompletableFuture.completedFuture(expectedResult));

        var mvcResult = mockMvc.perform(get("/api/v1/externalsvc/countryISOCode/" + countryName)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk());

        verify(countryInfoService, times(1)).countryISOCode(countryName);
    }

    @Test
    @DisplayName("EXITO: Debería retornar el código telefónico internacional por código ISO de forma asíncrona")
    void countryIntPhoneCode_debeRetornarExitosoAsync() throws Exception {
        String countryCode = "MX";
        String expectedResult = "52";
        when(countryInfoService.countryIntPhoneCode(countryCode))
            .thenReturn(CompletableFuture.completedFuture(expectedResult));

        var mvcResult = mockMvc.perform(get("/api/v1/externalsvc/countryIntPhoneCode/" + countryCode)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk());

        verify(countryInfoService, times(1)).countryIntPhoneCode(countryCode);
    }

    @Test
    @DisplayName("EXITO: Debería retornar la URL de la bandera por código ISO de forma asíncrona")
    void countryFlag_debeRetornarExitosoAsync() throws Exception {
        String countryCode = "MX";
        String expectedResult = "http://example.com/flag.png";
        when(countryInfoService.countryFlag(countryCode))
            .thenReturn(CompletableFuture.completedFuture(expectedResult));

        var mvcResult = mockMvc.perform(get("/api/v1/externalsvc/countryFlag/" + countryCode)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk());

        verify(countryInfoService, times(1)).countryFlag(countryCode);
    }

    @Test
    @DisplayName("EXITO: Debería retornar la moneda del país por código ISO de forma asíncrona")
    void countryCurrency_debeRetornarExitosoAsync() throws Exception {
        String countryCode = "MX";
        TCurrency expectedResult = new TCurrency();
        when(countryInfoService.countryCurrency(countryCode))
            .thenReturn(CompletableFuture.completedFuture(expectedResult));

        var mvcResult = mockMvc.perform(get("/api/v1/externalsvc/countryCurrency/" + countryCode)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk());

        verify(countryInfoService, times(1)).countryCurrency(countryCode);
    }

    @Test
    @DisplayName("EXITO: Debería retornar países usando una moneda por código de moneda de forma asíncrona")
    void countriesUsingCurrency_debeRetornarExitosoAsync() throws Exception {
        String currencyCode = "USD";
        ArrayOftCountryCodeAndName expectedResult = new ArrayOftCountryCodeAndName();
        when(countryInfoService.countriesUsingCurrency(currencyCode))
            .thenReturn(CompletableFuture.completedFuture(expectedResult));

        var mvcResult = mockMvc.perform(get("/api/v1/externalsvc/countriesUsingCurrency/" + currencyCode)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk());

        verify(countryInfoService, times(1)).countriesUsingCurrency(currencyCode);
    }

    @Test
    @DisplayName("EXITO: Debería retornar la capital del país por código ISO de forma asíncrona")
    void capitalCity_debeRetornarExitosoAsync() throws Exception {
        String countryCode = "MX";
        String expectedResult = "Mexico City";
        when(countryInfoService.capitalCity(countryCode))
            .thenReturn(CompletableFuture.completedFuture(expectedResult));

        var mvcResult = mockMvc.perform(get("/api/v1/externalsvc/capitalCity/" + countryCode)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk());

        verify(countryInfoService, times(1)).capitalCity(countryCode);
    }

    @Test
    @DisplayName("EXITO: Debería retornar el código ISO del lenguaje por nombre de forma asíncrona")
    void languageISOCode_debeRetornarExitosoAsync() throws Exception {
        String languageName = "Spanish";
        String expectedResult = "es";
        when(countryInfoService.languageISOCode(languageName))
            .thenReturn(CompletableFuture.completedFuture(expectedResult));

        var mvcResult = mockMvc.perform(get("/api/v1/externalsvc/languageISOCode/" + languageName)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk());

        verify(countryInfoService, times(1)).languageISOCode(languageName);
    }

    @Test
    @DisplayName("EXITO: Debería retornar el nombre del lenguaje por código ISO de forma asíncrona")
    void languageName_debeRetornarExitosoAsync() throws Exception {
        String isoCode = "es";
        String expectedResult = "Spanish";
        when(countryInfoService.languageName(isoCode))
            .thenReturn(CompletableFuture.completedFuture(expectedResult));

        var mvcResult = mockMvc.perform(get("/api/v1/externalsvc/languageName/" + isoCode)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk());

        verify(countryInfoService, times(1)).languageName(isoCode);
    }

    // ==========================================
    // PRUEBAS DE ÉXITO - CALCULATOR
    // ==========================================

    @Test
    @DisplayName("EXITO: Debería calcular la suma de forma asíncrona y retornar estado 200 con el resultado")
    void calculateSum_debeRetornarSumaExitosasAsync() throws Exception {
        CalculationRequest requestObj = new CalculationRequest();
        requestObj.setValorA(10);
        requestObj.setValorB(5);
        int expectedResult = 15;

        when(calculatorService.getAddValue(10, 5))
            .thenReturn(CompletableFuture.completedFuture(expectedResult));

        var mvcResult = mockMvc.perform(post("/api/v1/externalsvc/calculate-add")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
            .content(Objects.requireNonNull(objectMapper.writeValueAsString(requestObj))))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk())
            .andExpect(content().string(Objects.requireNonNull(String.valueOf(expectedResult))));

        verify(calculatorService, times(1)).getAddValue(10, 5);
    }

    @Test
    @DisplayName("EXITO: Debería calcular la multiplicación de forma asíncrona y retornar estado 200 con el resultado")
    void calculateMultiply_debeRetornarMultiplicacionExitosasAsync() throws Exception {
        CalculationRequest requestObj = new CalculationRequest();
        requestObj.setValorA(4);
        requestObj.setValorB(3);
        int expectedResult = 12;

        when(calculatorService.getMultiplyValue(4, 3))
            .thenReturn(CompletableFuture.completedFuture(expectedResult));

        var mvcResult = mockMvc.perform(post("/api/v1/externalsvc/calculate-multiply")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
            .content(Objects.requireNonNull(objectMapper.writeValueAsString(requestObj))))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk())
            .andExpect(content().string(Objects.requireNonNull(String.valueOf(expectedResult))));

        verify(calculatorService, times(1)).getMultiplyValue(4, 3);
    }

    @Test
    @DisplayName("EXITO: Debería calcular la división de forma asíncrona y retornar estado 200 con el resultado")
    void calculateDivide_debeRetornarDivisionExitosasAsync() throws Exception {
        CalculationRequest requestObj = new CalculationRequest();
        requestObj.setValorA(20);
        requestObj.setValorB(4);
        int expectedResult = 5;

        when(calculatorService.getDivideValue(20, 4))
            .thenReturn(CompletableFuture.completedFuture(expectedResult));

        var mvcResult = mockMvc.perform(post("/api/v1/externalsvc/calculate-divide")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(requestObj))))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk())
            .andExpect(content().string(Objects.requireNonNull(String.valueOf(expectedResult))));

        verify(calculatorService, times(1)).getDivideValue(20, 4);
    }


    // ==========================================
    // PRUEBAS DE ERROR - CALCULATOR
    // ==========================================

    @Test
    @DisplayName("ERROR: Debería propagar fallo cuando el servicio de suma falla de forma asíncrona")
    void calculateSum_debeRetornarErrorCuandoServicioFallaAsync() throws Exception {
        CalculationRequest requestObj = new CalculationRequest();
        requestObj.setValorA(10);
        requestObj.setValorB(5);

        RuntimeException expectedException = new RuntimeException("Error en motor SOAP de suma");
        when(calculatorService.getAddValue(10, 5))
            .thenReturn(CompletableFuture.failedFuture(expectedException));

        var mvcResult = mockMvc.perform(post("/api/v1/externalsvc/calculate-add")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
            .content(Objects.requireNonNull(objectMapper.writeValueAsString(requestObj))))
            .andExpect(request().asyncStarted())
            .andReturn();

        assertThrows(Exception.class, () -> { mockMvc.perform(asyncDispatch(mvcResult)); });

        verify(calculatorService, times(1)).getAddValue(10, 5);
    }

    @Test
    @DisplayName("ERROR: Debería propagar fallo cuando el servicio de multiplicación falla de forma asíncrona")
    void calculateMultiply_debeRetornarErrorCuandoServicioFallaAsync() throws Exception {
        CalculationRequest requestObj = new CalculationRequest();
        requestObj.setValorA(4);
        requestObj.setValorB(3);

        RuntimeException expectedException = new RuntimeException("Error en motor SOAP de multiplicación");
        when(calculatorService.getMultiplyValue(4, 3))
            .thenReturn(CompletableFuture.failedFuture(expectedException));

        var mvcResult = mockMvc.perform(post("/api/v1/externalsvc/calculate-multiply")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
            .content(Objects.requireNonNull(objectMapper.writeValueAsString(requestObj))))
            .andExpect(request().asyncStarted())
            .andReturn();

        assertThrows(Exception.class, () -> { mockMvc.perform(asyncDispatch(mvcResult)); });            

        verify(calculatorService, times(1)).getMultiplyValue(4, 3);
    }

    @Test
    @DisplayName("ERROR: Debería propagar fallo cuando el servicio de división falla de forma asíncrona")
    void calculateDivide_debeRetornarErrorCuandoServicioFallaAsync() throws Exception {
        CalculationRequest requestObj = new CalculationRequest();
        requestObj.setValorA(20);
        requestObj.setValorB(0); // Simulando división por cero o error externo

        RuntimeException expectedException = new RuntimeException("División por cero en SOAP externo");
        when(calculatorService.getDivideValue(20, 0))
            .thenReturn(CompletableFuture.failedFuture(expectedException));

        var mvcResult = mockMvc.perform(post("/api/v1/externalsvc/calculate-divide")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
            .content(Objects.requireNonNull(objectMapper.writeValueAsString(requestObj))))
            .andExpect(request().asyncStarted())
            .andReturn();

        assertThrows(Exception.class, () -> { mockMvc.perform(asyncDispatch(mvcResult)); });            

        verify(calculatorService, times(1)).getDivideValue(20, 0);
    }

    // ==========================================
    // PRUEBAS - JSONPLACEHOLDER
    // ==========================================

    @Test
    @DisplayName("EXITO: Debería obtener los posts del servicio externo de forma asíncrona y retornar estado 200")
    void getApiPost_debeRetornarPostsExitososAsync() throws Exception {
        PostDataResponse mockPost = new PostDataResponse();
        mockPost.setId(1);
        mockPost.setUserId(1);
        mockPost.setTitle("Title Test");
        mockPost.setBody("Body Test");

        List<PostDataResponse> expectedPosts = List.of(mockPost);

        when(jsonPlaceholderService.getAllPosts())
            .thenReturn(CompletableFuture.completedFuture(expectedPosts));

        var mvcResult = mockMvc.perform(get("/api/v1/externalsvc/getApiPost")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk())
            .andExpect(content().contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON)))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].title").value("Title Test"));

        verify(jsonPlaceholderService, times(1)).getAllPosts();
    }

    @Test
    @DisplayName("ERROR: Debería propagar error cuando el servicio externo de posts falla de forma asíncrona")
    void getApiPost_debeRetornarErrorCuandoServicioFallaAsync() throws Exception {
        RuntimeException expectedException = new RuntimeException("Error al conectar con API externa de posts");

        when(jsonPlaceholderService.getAllPosts())
            .thenReturn(CompletableFuture.failedFuture(expectedException));

        var mvcResult = mockMvc.perform(get("/api/v1/externalsvc/getApiPost")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(request().asyncStarted())
            .andReturn();

        assertThrows(Exception.class, () -> {
            mockMvc.perform(asyncDispatch(mvcResult));
        });

        verify(jsonPlaceholderService, times(1)).getAllPosts();
    }
}