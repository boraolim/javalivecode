package liverpool.codelive.products.entity;

import java.math.BigDecimal;

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
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "PRODUCTS", schema = "LIVERLIVECODE")
public class ProductEntity {
    @Id
    @Column(name = "id_product")
    private String id;

    @Column(name = "name_product")
    private String name;

    @Column(name = "description_product")
    private String description;

    @Column(name = "price_product")
    private BigDecimal price;
}
