package liverpool.codelive.inventory.exception;

public class InventoryNotFoundException extends RuntimeException {

    public InventoryNotFoundException(String productId) {
        super("No se encontro inventario para el producto con id: " + productId);
    }
}
