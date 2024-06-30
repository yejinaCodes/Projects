package dto;

import vo.Product;

public class ProductDto {
    private final Integer no;
    private final String name;
    private final Integer price;
    private final Integer stock;

    public ProductDto(Integer no, String name, Integer price, Integer stock) {
        this.no = no;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public Product toCreate(int no) {
        return new Product(no, name, price, stock);
    }

    public Integer getNo() {
        return no;
    }

    public String getName() {
        return name;
    }

    public Integer getPrice() {
        return price;
    }

    public Integer getStock() {
        return stock;
    }
}
