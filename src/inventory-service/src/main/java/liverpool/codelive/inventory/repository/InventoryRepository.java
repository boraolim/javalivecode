package liverpool.codelive.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import liverpool.codelive.inventory.entity.InventoryEntity;

public interface InventoryRepository extends JpaRepository<InventoryEntity, String> {
    
}
