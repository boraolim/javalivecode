package hogar.codelive.products;

import org.springframework.test.context.ActiveProfiles;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseProductTest extends BaseTestData {
}
