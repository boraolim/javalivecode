package liverpool.codelive.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;

@EnableAsync
@SpringBootApplication
@OpenAPIDefinition(info = @Info(title       = "Liverpool Live Code API",
                                version     = "1.0",
                                description = "Liverpool Live Code MicroService",
                                license     = @License(name = "EULA El Puerto de Liverpool S.A. de C.V. México.",
                                                       url  = "https://www.scorpion.com.mx/politica-privacidad"),
                                contact     = @Contact(name  = "El Puerto de Liverpool S.A. de C.V. México.",
                                                       url   = "https://www.scorpion.com.mx/",
                                                       email = "ventasenlinea@liverpool.com.mx")))
public class InventoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryApplication.class, args);
	}

}
