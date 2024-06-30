package vo;

public class Product {
    private int no;
    private String name;
    private int price;
    private int stock;

    public Product(int no, String name, int price, int stock) {
        this.no = no;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public int getNo() {
        return no;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public void updateProduct(String name, int price, int stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }
}
