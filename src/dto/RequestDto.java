package dto;

public class RequestDto {

  private int menu;
  private Product data;

  public RequestDto(int menu, Product data) {
    this.menu = menu;
    this.data = data;
  }

  public int getMenu() {
    return menu;
  }

  public Product getData() {
    return data;
  }

  @Override
  public String toString() {
    return "RequestDto{" +
        "menu=" + menu +
        ", data=" + data +
        '}';
  }
}
