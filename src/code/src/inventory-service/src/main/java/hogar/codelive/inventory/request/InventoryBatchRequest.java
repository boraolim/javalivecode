package hogar.codelive.inventory.request;

import java.util.List;

public record InventoryBatchRequest
(
    List<String> productIds
) { }