package hogar.codelive.products.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.NullValuePropertyMappingStrategy;


import hogar.codelive.products.entity.ProductEntity;
import hogar.codelive.products.dto.ExternalProductDto;
import hogar.codelive.products.response.ProductResponse;
import hogar.codelive.products.request.ProductNewRequest;
import hogar.codelive.products.request.ProductExistentRequest;
import hogar.codelive.products.response.EnrichedProductResponse;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    Logger LOGGER = LoggerFactory.getLogger(ProductMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "price", target = "price")
    ProductEntity toEntity(ExternalProductDto externalProductDto);

    @Mapping(source = "productId", target = "id")
    @Mapping(source = "nameProduct", target = "title")
    @Mapping(source = "descriptionProduct", target = "description")
    @Mapping(source = "priceProduct", target = "price")
    ExternalProductDto toDto(ProductNewRequest requestProduct);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "title")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "price", target = "price")    
    ExternalProductDto fromEntity(ProductEntity entity);

    @Mapping(source = "id", target = "idProduct")
    @Mapping(source = "title", target = "nameProduct")
    @Mapping(source = "description", target = "descriptionProduct")
    @Mapping(source = "price", target = "priceProduct")    
    ProductResponse fromDto(ExternalProductDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "nameProduct", target = "title")
    @Mapping(source = "descriptionProduct", target = "description")
    @Mapping(source = "priceProduct", target = "price")
    void updateEntity(ProductExistentRequest request, @MappingTarget ExternalProductDto dto);

    @Mapping(target = "stock", ignore = true)
    @Mapping(target = "inventoryStatus", ignore = true)
    EnrichedProductResponse toEnrichedResponse(ProductEntity product);
}
