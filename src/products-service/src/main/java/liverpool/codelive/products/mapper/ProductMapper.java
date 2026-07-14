package liverpool.codelive.products.mapper;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.mapstruct.Named;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import liverpool.codelive.products.entity.ProductEntity;
import liverpool.codelive.products.dto.ExternalProductDto;
import liverpool.codelive.products.response.EnrichedProductResponse;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    Logger LOGGER = LoggerFactory.getLogger(ProductMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "price", target = "price", qualifiedByName = "stringToBigDecimal")
    ProductEntity toEntity(ExternalProductDto externalProductDto);

    @Named("stringToBigDecimal")
    default BigDecimal stringToBigDecimal(String value) {
        if (value == null || value.isBlank()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            LOGGER.warn("Valor de precio invalido: '{}'. Se usara BigDecimal.ZERO por defecto.", value);
            return BigDecimal.ZERO;
        }
    }

    @Mapping(target = "stock", ignore = true)
    @Mapping(target = "inventoryStatus", ignore = true)
    EnrichedProductResponse toEnrichedResponse(ProductEntity product);
}