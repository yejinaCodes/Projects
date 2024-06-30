package dto;

public class RequestDto {

    private final String menu;
    private ProductDto product;

    public RequestDto(String menu) {
        this.menu = menu;
    }

    public RequestDto(String menu, ProductDto data) {
        this.menu = menu;
        this.product = data;
    }

    public String getMenu() {
        return menu;
    }

    public ProductDto getProduct() {
        return product;
    }
}
