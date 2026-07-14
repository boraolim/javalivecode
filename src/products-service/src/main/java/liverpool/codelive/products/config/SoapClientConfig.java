package liverpool.codelive.products.config;

import jakarta.xml.ws.BindingProvider;

import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import liverpool.codelive.products.wsdl.calculator.Calculator;
import liverpool.codelive.products.wsdl.calculator.CalculatorSoap;
import liverpool.codelive.products.wsdl.countryinfo.CountryInfoService;
import liverpool.codelive.products.wsdl.countryinfo.CountryInfoServiceSoapType;

@Configuration
public class SoapClientConfig {
    @Bean
    public CalculatorSoap calculatorPort(@Value("${soap.calculator.wsdl}") String wsdlUrl) {
        Calculator service = new Calculator();
        return configurePort(service.getCalculatorSoap(), wsdlUrl);
    }

    @Bean
    public CountryInfoServiceSoapType countryInfoPort(@Value("${soap.countryinfo.wsdl}") String wsdlUrl) {
        CountryInfoService service = new CountryInfoService();
        return configurePort(service.getCountryInfoServiceSoap(), wsdlUrl);
    }

    private <T> T configurePort(T port, String url) {
        if (port instanceof BindingProvider bp) {
            bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
        }
        return port;
    }
}
