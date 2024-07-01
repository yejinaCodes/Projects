package exception;

import dto.Product;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductExceptionList {

  public boolean isValidNumber(String number) {
    String regex = "^[0-9]*$";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(number);
    if (!matcher.matches()) {
      return true;
    }
    return false;
  }

  public boolean isValidName(String name) {
    String regex = "^[a-zA-Z]*$";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(name);
    if (!matcher.matches()) {
      return true;
    }
    return false;
  }

  public boolean isExistName(String name, List<Product> products) {
    for (Product product : products) {
      if (product.getName().equals(name)) {
        System.out.println(product.getName());
        System.out.println(name);
        return true;
      }
    }
    return false;
  }

  public boolean isExistProduct(int productNo, List<Product> products) {
    for (Product product : products) {
      if (product.getNo() == productNo) {
        return true;
      }
    }
    return false;
  }
}
