package hogar.codelive.inventory.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import hogar.codelive.inventory.dto.InventoryDto;
import hogar.codelive.inventory.entity.InventoryEntity;
import hogar.codelive.inventory.response.InventoryResponse;
import hogar.codelive.inventory.request.InventoryExistentRequest;
import hogar.codelive.inventory.request.InventoryNewProductRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InventoryMapper {

    @Mapping(source = "productId", target = "productId")
    @Mapping(source = "stock", target = "stock")
    InventoryEntity toEntity(InventoryDto inventoryProductDto);

    @Mapping(source = "idProduct", target = "productId")
    @Mapping(source = "productStock", target = "stock")    
    InventoryDto toDto(InventoryNewProductRequest requestInventory);

    @Mapping(source = "productId", target = "productId")
    @Mapping(source = "stock", target = "stock")    
    InventoryDto fromEntity(InventoryEntity inventoryProductEntity);

    @Mapping(source = "productId", target = "productId")
    @Mapping(source = "stock", target = "stock")    
    InventoryResponse fromDto(InventoryDto inventoryProductDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "productStock", target = "stock")
    void updateEntity(InventoryExistentRequest request, @MappingTarget InventoryDto dto);
}
