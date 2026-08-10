package hogar.codelive.externalws;

import org.springframework.boot.SpringApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;

@EnableAsync
@EnableCaching
@SpringBootApplication
@OpenAPIDefinition(info = @Info(title       = "Demo Live Code API",
                                version     = "1.0",
                                description = "Demo Live Code MicroService",
                                license     = @License( name  = "EULA Hogar S.A. de C.V. México.",
                                                        url   = "https://www.scorpion.com.mx/politica-privacidad"),
                                contact     = @Contact( name  = "Hogar S.A. de C.V. México.",
                                                        url   = "https://www.hogar.com.mx/",
                                                        email = "ventasenlinea@hogar.com.mx")))
public class ExternalServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExternalServiceApplication.class, args);
	}
}
