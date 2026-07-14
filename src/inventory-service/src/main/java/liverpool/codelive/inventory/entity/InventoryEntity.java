package liverpool.codelive.inventory.entity;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "INVENTORY", schema = "LIVERLIVECODE")
public class InventoryEntity {
    
    @Id
    @Column(name = "id_product")
    private String productId;

    @Column(name = "stock_product")
    private Integer stock;
}
