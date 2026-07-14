package liverpool.codelive.products.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import liverpool.codelive.products.entity.ProductEntity;

public interface ProductRepository 
    extends JpaRepository<ProductEntity, String> {
    
    List<ProductEntity> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String nameKeyword, String descriptionKeyword);
}
