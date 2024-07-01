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
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ProductClient {

  private static final String HOST = "127.0.0.1";
  private static final int PORT_NUMBER = 8080;
  private static final List<Product> products = new ArrayList<>();
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

        //success -> 리스트 만듦
        ResponseDto response = parseJson(clientReader.readLine());
        System.out.println("response Client!! = " + response.getStatus());
        if (response.getStatus().equals("success")) {

          Product newProduct = new Product(response.getData().getNo(), response.getData().getName(),
              response.getData().getPrice(), response.getData().getStock());
          products.add(newProduct);
        }
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

        if (error.isExistName(name, products)) {
          throw new ProductException(ErrorCode.EXIST_ALREADY_NAME);
        }
        if (error.isValidName(name)) {
          throw new ProductException(ErrorCode.INVALID_INPUT_CHARACTER);
        }

        System.out.print("상품 가격: ");
        int price = Integer.parseInt(br.readLine());
        if (error.isValidNumber(String.valueOf(price))) {
          throw new ProductException(ErrorCode.INVALID_INPUT_NUMBER);
        }

        System.out.print("상품 재고: ");
        int stock = Integer.parseInt(br.readLine());
        if (error.isValidNumber(String.valueOf(stock))) {
          throw new ProductException(ErrorCode.INVALID_INPUT_NUMBER);
        }
        return makeJson(new RequestDto(1, new Product(0, name, price, stock)));
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
        int productNo = Integer.parseInt(br.readLine());
        if (!error.isExistProduct(productNo, products)) {
          throw new ProductException(ErrorCode.PRODUCT_NO_INFORMATION);
        }

        System.out.print("상품 이름: ");
        String name = br.readLine();
        if (error.isExistName(name, products)) {
          throw new ProductException(ErrorCode.EXIST_ALREADY_NAME);
        }
        if (!error.isValidName(name)) {
          throw new ProductException(ErrorCode.INVALID_INPUT_CHARACTER);
        }

        System.out.print("상품 가격: ");
        int price = Integer.parseInt(br.readLine());
        if (error.isValidNumber(String.valueOf(price))) {
          throw new ProductException(ErrorCode.INVALID_INPUT_NUMBER);
        }

        System.out.print("상품 재고: ");
        int stock = Integer.parseInt(br.readLine());
        if (error.isValidNumber(String.valueOf(stock))) {
          throw new ProductException(ErrorCode.INVALID_INPUT_NUMBER);
        }

        return makeJson(new RequestDto(3, new Product(productNo, name, price, stock)));
      } catch (Exception e) {
        e.getStackTrace();
      }
    }
  }

  private static String deleteProduct(BufferedReader br) throws IOException {
    while (true) {

    }
  }

  private static void printMenu() {
    System.out.println("---------------------------------------------");
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


