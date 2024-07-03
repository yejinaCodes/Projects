import java.util.List;
import org.json.simple.JSONObject;

public class Product {
  private int no;
  private String name;
  private int price;
  private int stock;

  public Product() {}

  public Product(int no, String name, int price, int stock) {

    this.no = (Integer) no;
    this.name = name;
    this.price = price;
    this.stock = stock;
  }

  public int getNo() {
    return no;
  }

  public void setNo(int no) {
    this.no = no;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getPrice() {
    return price;
  }

  public void setPrice(int price) {
    this.price = price;
  }

  public int getStock() {
    return stock;
  }

  public void setStock(int stock) {
    this.stock = stock;
  }

  //Product를 Jsonobject로 만들기
  public JSONObject string_to_json(Product product){

    JSONObject jsonobj = new JSONObject();
//    jsonobj.put("no", product.no);
    jsonobj.put("name", product.name);
    jsonobj.put("price" , product.price);
    jsonobj.put("stock", product.stock);
    return jsonobj;
  }

//  public JSONObject array_to_jsonString(List<Product> array_list){
//    JSONObject jsonobj = new JSONObject();
//    for(Product p : array_list){
//      jsonobj.put("no", p.no);
//      jsonobj.put("name", p.name);
//      jsonobj.put("price", p.price);
//      jsonobj.put("stock", p.stock);
//    }
//    return jsonobj;
//  }

}
