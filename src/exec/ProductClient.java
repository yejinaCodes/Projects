package exec;

import com.google.gson.Gson;
import dto.Product;
import dto.RequestDto;
import dto.ResponseDto;
import exception.ErrorCode;
import exception.ProductException;
import exception.ProductExceptionList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ProtocolException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ProductClient {

  private static final String HOST = "127.0.0.1";
  private static final int PORT_NUMBER = 8080;
  private static List<Product> products = new ArrayList<>();
  private static final ProductExceptionList error = new ProductExceptionList();

  public static void main(String[] args) {
    Socket socket = null;
    try {
      socket = new Socket(HOST, PORT_NUMBER);
      PrintWriter clientWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
      BufferedReader clientReader = new BufferedReader(
          new InputStreamReader(socket.getInputStream()));
      BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

      //클라이언트의 요청을 Json화하여 서버에 보낸다.
      while (true) {
        printMenu();
        printProductList();
        int inputNumber = Integer.parseInt(userInput.readLine());

        switch (inputNumber) {
          case 1 -> {
            clientWriter.println(createProduct(userInput));
            clientWriter.flush();
          }

          case 2 -> {
            clientWriter.println(updateProduct(userInput));
            clientWriter.flush();
          }

          case 3 -> {
            clientWriter.println(deleteProduct(userInput));
            clientWriter.flush();
          }

          case 4 -> {
          }
          default -> System.out.println("다시 입력해주세요.\n");
        }
        ResponseDto response = parseJson(clientReader.readLine());
        products = new ArrayList<>();
        products.addAll(response.getData());
      }

    } catch (IOException e) {
      e.getStackTrace();
    }
  }

  private static String createProduct(BufferedReader br) throws IOException {
    while (true) {
      try {
        System.out.println("[상품 생성]");
        System.out.print("상품 이름: ");
        String name = br.readLine();

        // 유효성 검사: 빈문자, 문자형식, 이미 존재하는 이름인지
        if (name.isEmpty()) {
          throw new ProductException(ErrorCode.INVALID_INPUT_BLANK);
        }
        if (error.isExistName(name, products)) {
          throw new ProductException(ErrorCode.EXIST_ALREADY_NAME);
        }
        if (error.isValidName(name)) {
          throw new ProductException(ErrorCode.INVALID_INPUT_CHARACTER);
        }

        System.out.print("상품 가격: ");
        String price = br.readLine();

        // 유효성 검사: 빈문자, 숫자형식, 1~9999원
        if (price.isEmpty()) {
          throw new ProductException(ErrorCode.INVALID_INPUT_BLANK);
        }
        if (error.isValidNumber(price)) {
          throw new ProductException(ErrorCode.INVALID_INPUT_NUMBER);
        }
        if (error.isValidPrice(price)) {
          throw new ProductException(ErrorCode.INVALID_INPUT_PRICE);
        }

        System.out.print("상품 재고: ");
        String stock = br.readLine();

        // 유효성 검사: 빈문자, 숫자형식, 1~99개
        if (stock.isEmpty()) {
          throw new ProductException(ErrorCode.INVALID_INPUT_BLANK);
        }
        if (error.isValidNumber(stock)) {
          throw new ProductException(ErrorCode.INVALID_INPUT_NUMBER);
        }
        if (error.isValidStock(stock)) {
          throw new ProductException(ErrorCode.INVALID_INPUT_STOCK);
        }
        return makeJson(new RequestDto(1, new Product(0, name, Integer.parseInt(price), Integer.parseInt(stock))));
      } catch (Exception e) {
        e.getStackTrace();
      }
    }
  }

  private static String updateProduct(BufferedReader br) throws IOException {
    while (true) {
      try {
        System.out.println("[상품 수정]");
        System.out.print("상품 번호: ");
        String productNo = br.readLine();

        // 유효성 검사: 빈문자, 숫자형식, 존재하는 상품인지
        if (productNo.isEmpty()) {
          throw new ProductException(ErrorCode.INVALID_INPUT_BLANK);
        }
        if (error.isValidNumber(productNo)) {
          throw new ProductException(ErrorCode.INVALID_INPUT_NUMBER);
        }
        if (!error.isExistProduct(Integer.parseInt(productNo), products)) {
          throw new ProductException(ErrorCode.PRODUCT_NO_INFORMATION);
        }

        System.out.print("상품 이름: ");
        String name = br.readLine();

        // 유효성 검사: 빈문자, 문자형식, 이미 존재하는 이름인지
        if (name.isEmpty()) {
          throw new ProductException(ErrorCode.INVALID_INPUT_BLANK);
        }
        if (error.isExistName(name, products)) {
          throw new ProductException(ErrorCode.EXIST_ALREADY_NAME);
        }
        if (error.isValidName(name)) {
          throw new ProductException(ErrorCode.INVALID_INPUT_CHARACTER);
        }

        System.out.print("상품 가격: ");
        String price = br.readLine();

        // 유효성 검사: 빈문자, 숫자형식, 1~9999원
        if (price.isEmpty()) {
          throw new ProductException(ErrorCode.INVALID_INPUT_BLANK);
        }
        if (error.isValidNumber(price)) {
          throw new ProductException(ErrorCode.INVALID_INPUT_NUMBER);
        }
        if (error.isValidPrice(price)) {
          throw new ProductException(ErrorCode.INVALID_INPUT_PRICE);
        }

        System.out.print("상품 재고: ");
        String stock = br.readLine();

        // 유효성 검사: 빈문자, 숫자형식, 1~99개
        if (stock.isEmpty()) {
          throw new ProductException(ErrorCode.INVALID_INPUT_BLANK);
        }
        if (error.isValidNumber(stock)) {
          throw new ProductException(ErrorCode.INVALID_INPUT_NUMBER);
        }
        if (error.isValidStock(stock)) {
          throw new ProductException(ErrorCode.INVALID_INPUT_STOCK);
        }

        return makeJson(new RequestDto(2, new Product(Integer.parseInt(productNo), name, Integer.parseInt(price), Integer.parseInt(stock))));
      } catch (Exception e) {
        e.getStackTrace();
      }
    }
  }

  private static String deleteProduct(BufferedReader br) throws IOException {
    while (true) {
      try {
        System.out.println("[상품 삭제]");
        System.out.print("상품 번호: ");
        Product deleteProduct = null;
        String productNo = br.readLine();

        // 유효성 검사: 빈문자, 숫자형식, 존재하는 상품인지
        if (productNo.isEmpty()) {
          throw new ProductException(ErrorCode.INVALID_INPUT_BLANK);
        }
        if (error.isValidNumber(productNo)) {
          throw new ProductException(ErrorCode.INVALID_INPUT_NUMBER);
        }
        if (!error.isExistProduct(Integer.parseInt(productNo), products)) {
          throw new ProductException(ErrorCode.PRODUCT_NO_INFORMATION);
        }

        for (Product product : products) {
          if (product.getNo() == Integer.parseInt(productNo)) {
            deleteProduct = new Product(Integer.parseInt(productNo), product.getName(), product.getPrice(),
                product.getStock());
            break;
          }
        }
        return makeJson(new RequestDto(3,
            new Product(Integer.parseInt(productNo), deleteProduct.getName(), deleteProduct.getPrice(),
                deleteProduct.getStock())));
      } catch (Exception e) {
        e.getStackTrace();
      }
    }
  }

  private static void printMenu() {
    System.out.println("\n---------------------------------------------");
    System.out.printf("%-5s %-15s %-10s %-5s\n", "no", "name", "price", "stock");
    System.out.println("---------------------------------------------");
  }

  private static void printProductList() {
    for (Product product : products) {
      System.out.printf("%-5s %-15s %-10s %-5s\n", product.getNo(), product.getName(),
          product.getPrice(), product.getStock());
      System.out.println("---------------------------------------------");
    }
    System.out.println("---------------------------------------------");
    System.out.println("메뉴: 1.Create | 2.Update | 3.Delete | 4.Exit");
    System.out.print("선택: ");
  }

  private static String makeJson(RequestDto request) {
    Gson json = new Gson();
    return json.toJson(request);
  }

  private static ResponseDto parseJson(String jsonString) {
    Gson json = new Gson();
    return json.fromJson(jsonString, ResponseDto.class);
  }
}


