package hogar.codelive.externalws.config;

import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import hogar.codelive.common.functions.ConfigurePort;
import hogar.codelive.externalws.wsdl.calculator.Calculator;
import hogar.codelive.externalws.wsdl.calculator.CalculatorSoap;
import hogar.codelive.externalws.wsdl.countryinfo.CountryInfoService;
import hogar.codelive.externalws.wsdl.countryinfo.CountryInfoServiceSoapType;

@Configuration
public class SoapClientConfig {
    @Bean
    public CalculatorSoap calculatorPort(@Value("${soap.calculator.wsdl}") String wsdlUrl) {
        Calculator service = new Calculator();
        return ConfigurePort.configurePort(service.getCalculatorSoap(), wsdlUrl);
    }

    @Bean
    public CountryInfoServiceSoapType countryInfoPort(@Value("${soap.countryinfo.wsdl}") String wsdlUrl) {
        CountryInfoService service = new CountryInfoService();
        return ConfigurePort.configurePort(service.getCountryInfoServiceSoap(), wsdlUrl);
    }
}