package hogar.codelive.products.dto;

import java.math.BigDecimal;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ExternalProductDto {

    @JsonProperty("id")
    private String id;
    private String title;
    private String description;
    private BigDecimal price;
    private String currency;
    private String category;
    private boolean active;
}
