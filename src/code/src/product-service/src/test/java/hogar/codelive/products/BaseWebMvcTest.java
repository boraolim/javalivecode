package hogar.codelive.products;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@ActiveProfiles("test")
@WebMvcTest(excludeAutoConfiguration = {SecurityAutoConfiguration.class}) // Nota: No se especifica el controlador aquí para que sea reutilizable
public abstract class BaseWebMvcTest extends BaseTestData {
}
