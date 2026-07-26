package hogar.codelive.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hogar.codelive.inventory.entity.InventoryEntity;

public interface InventoryRepository extends JpaRepository<InventoryEntity, String> {

}
