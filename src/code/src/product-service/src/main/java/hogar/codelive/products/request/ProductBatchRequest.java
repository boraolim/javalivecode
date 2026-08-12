package hogar.codelive.products.request;

import java.util.List;

public record ProductBatchRequest
(
    List<String> productIds
) { }
