package hogar.codelive.products;

import java.util.Map;
import java.util.List;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.context.SpringBootTest;

import hogar.codelive.products.entity.ProductEntity;
import hogar.codelive.products.constants.AppTestConstants;

@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseProductTest {   
    protected String targetId;
    protected String jsonInput;
    protected ProductEntity product1;
    protected ProductEntity product2;
    protected ProductEntity product3;
    protected ProductEntity product4;
    protected List<ProductEntity> inventoryList;

    @BeforeEach
    void setUpBase() throws Exception {
        product1 = ProductEntity.builder()
                .id(AppTestConstants.PRODUCT_FIRST_ID)
                .name("PlayStation 5 Slim")
                .description("Next-gen gaming console from Sony")
                .price(new BigDecimal("499.99"))
                .build();

        product2 = ProductEntity.builder()
                .id(AppTestConstants.PRODUCT_SECOND_ID)
                .name("Nintendo Switch OLED")
                .description("Portable family console")
                .price(new BigDecimal("349.99"))
                .build();

        product3 = ProductEntity.builder()
                .id(AppTestConstants.PRODUCT_THIRD_ID)
                .name("Xbox Series X")
                .description("Powerful console with GamePass")
                .price(new BigDecimal("499.00"))
                .build();

        product4 = ProductEntity.builder()
                .id(AppTestConstants.PRODUCT_FOURTH_ID)
                .name("PlayStation 5")
                .description("Standard gaming console from Sony")
                .price(new BigDecimal("2449.99"))
                .build();

        inventoryList = List.of(product1, product2, product3);

        ObjectMapper objectMapper = new ObjectMapper();

        jsonInput = objectMapper.writeValueAsString(List.of(
            Map.of("id", AppTestConstants.PRODUCT_FIRST_ID,
                "title", "PlayStation 5 Slim",
                "description", "Next-gen gaming console from Sony",
                "price", 499.99,
                "active", true)));
    }
}
