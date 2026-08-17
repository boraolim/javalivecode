package hogar.codelive.products;

import java.util.Map;
import java.util.List;
import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.test.context.ActiveProfiles;

import org.springframework.boot.test.context.SpringBootTest;

import hogar.codelive.products.dto.InventoryDto;
import hogar.codelive.products.entity.ProductEntity;
import hogar.codelive.products.enums.InventoryStatus;
import hogar.codelive.products.request.ProductNewRequest;
import hogar.codelive.products.constants.AppTestConstants;
import hogar.codelive.products.request.ProductExistentRequest;
import hogar.codelive.products.response.EnrichedProductResponse;

@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseProductTest {   
    protected String targetId;
    protected String jsonInput;
    protected String queryStringFor;
    protected ProductEntity productFirst;
    protected ProductEntity productSecond;
    protected ProductEntity productThird;
    protected ProductEntity productFourth;
    protected ProductEntity productSeventh;
    protected ProductEntity productEighth;
    protected ObjectMapper commonMapper;
    protected List<ProductEntity> inventoryList;

    protected InventoryDto inventoryProductDto;
    protected InventoryDto inventorySixthProductDto;

    protected ProductNewRequest newProductRequest;

    protected ProductExistentRequest existentProductRequest;
    
    protected EnrichedProductResponse responseProduct;

    @BeforeEach
    void setUpBase() throws Exception {
        commonMapper = new ObjectMapper();

        productFirst = ProductEntity.builder()
                .id(AppTestConstants.PRODUCT_FIRST_ID)
                .name("PlayStation 5 Slim")
                .description("Next-gen gaming console from Sony")
                .price(new BigDecimal("499.99"))
                .build();

        productSecond = ProductEntity.builder()
                .id(AppTestConstants.PRODUCT_SECOND_ID)
                .name("Nintendo Switch OLED")
                .description("Portable family console")
                .price(new BigDecimal("349.99"))
                .build();

        productThird = ProductEntity.builder()
                .id(AppTestConstants.PRODUCT_THIRD_ID)
                .name("Xbox Series X")
                .description("Powerful console with GamePass")
                .price(new BigDecimal("499.00"))
                .build();

        productFourth = ProductEntity.builder()
                .id(AppTestConstants.PRODUCT_FOURTH_ID)
                .name("Laptop Gamer")
                .description("Gaming laptop")
                .price(new BigDecimal("1000.00"))
                .build();                

        productSeventh = ProductEntity.builder()
                .id(AppTestConstants.PRODUCT_SEVENTH_ID)
                .name("Laptop HP OmniBook 3 16t-bw000")
                .description("Laptop HP OmniBook 3 AI 16t-bw000 16 AMD Ryzen 5")
                .price(new BigDecimal("7830.20"))
                .build();

        productEighth = ProductEntity.builder()
                .id(AppTestConstants.PRODUCT_EIGHTH_ID)
                .name("Tablet Huawei MatePad 11.5")
                .description("Tablet de 11.5 pulgadas con 8 GB de RAM y 128 GB de almacenamiento.")
                .price(new BigDecimal("7499.00"))
                .build();                

        responseProduct = new EnrichedProductResponse();
        responseProduct.setId(AppTestConstants.PRODUCT_FOURTH_ID);
        responseProduct.setName("Laptop Gamer");
        responseProduct.setDescription(("Gaming laptop"));
        responseProduct.setPrice(new BigDecimal("2449.99"));
        responseProduct.setInventoryStatus(InventoryStatus.IN_STOCK);
        responseProduct.setStock(100);

        inventoryProductDto = new InventoryDto();
        inventoryProductDto.setProductId(AppTestConstants.PRODUCT_FOURTH_ID);
        inventoryProductDto.setStock(100);
        
        inventorySixthProductDto = new InventoryDto();
        inventorySixthProductDto.setProductId(AppTestConstants.PRODUCT_SIXTH_ID);
        inventorySixthProductDto.setStock(0);
        
        newProductRequest = new ProductNewRequest();
        newProductRequest.setProductId("EXT-018");
        newProductRequest.setNameProduct("Leche Alpura");
        newProductRequest.setDescriptionProduct("Leche Alpura de cuarto de litro con alta lactosa");
        newProductRequest.setPriceProduct(new BigDecimal("35.00"));

        existentProductRequest = new ProductExistentRequest();
        existentProductRequest.setNameProduct("Updated Name");
        existentProductRequest.setDescriptionProduct("Cambio de la descripción del artículo EXT-001");

        inventoryList = List.of(productFirst, productSecond, productThird);

        jsonInput = commonMapper.writeValueAsString(List.of(
            Map.of("id", AppTestConstants.PRODUCT_FIRST_ID,
                "title", "PlayStation 5 Slim",
                "description", "Next-gen gaming console from Sony",
                "price", 499.99,
                "active", true)));
    }
}
