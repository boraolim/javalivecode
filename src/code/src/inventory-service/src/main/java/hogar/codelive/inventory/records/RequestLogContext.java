package hogar.codelive.inventory.records;

public record RequestLogContext
(
    long elapsed, 
    int status, 
    String method, 
    String path, 
    String query, 
    String requestBody, 
    String responseBody, 
    Exception ex
) { }
