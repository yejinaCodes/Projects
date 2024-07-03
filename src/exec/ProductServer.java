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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProductServer {

  private static final int PORT_NUMBER = 8080;
  private static final List<Product> products = Collections.synchronizedList(new ArrayList<>());
  private static final ProductExceptionList error = new ProductExceptionList();
  private static int productNo = 1;
  private static int userNo = 1;

  public static void main(String[] args) {
    ServerSocket serverSocket = null;

    try {
      serverSocket = new ServerSocket(PORT_NUMBER);

      while (true) {
        Socket socket = serverSocket.accept();

        //클라이언트가 접속한 수만큼 쓰레드 생성
        SocketClient socketClient = new SocketClient(socket);
        socketClient.start();
      }
    } catch (IOException e) {
      e.getStackTrace();
    } finally {
      try {
        assert serverSocket != null;
        serverSocket.close();
      } catch (IOException e) {
        e.getStackTrace();
      }
    }
  }

  static class SocketClient extends Thread {

    private final Socket socket;
    private PrintWriter serverWriter;
    private BufferedReader serverReader;

    public SocketClient(Socket socket) {
      this.socket = socket;
      try {
        serverWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      } catch (IOException e) {
        e.getStackTrace();
      }
    }

    @Override
    public void run() {
      try {
        System.out.println(">> user" + userNo++ + " enter << " + socket.getLocalSocketAddress());
        while (true) {
          //클라이언트의 요청을 json으로 받는다.
          RequestDto request = parseJson(serverReader.readLine());

          if (request == null) {
            System.out.println("user out");
            break;
          }
          //클라이언트에게 보낼 응답
          ResponseDto response = productService(request);

          // makeJson
          String jsonString = makeJson(response);

          // response 반환
          serverWriter.println(jsonString);
          serverWriter.flush();
        }
      } catch (IOException e) {
        e.getStackTrace();
      } finally {
        try {
          socket.close();
        } catch (IOException e) {
          e.getStackTrace();
        }
      }
    }

    // 상품 리스트 생성, 수정, 삭제 진행 (유효성 검사)
    private static ResponseDto productService(RequestDto request) {
      Product checkProduct = request.getData();

      switch (request.getMenu()) {
        case 1 -> { //상품 생성
          try {
            // name 검사
            if (checkProduct.getName().isEmpty()) {
              throw new ProductException(ErrorCode.INVALID_INPUT_BLANK);
            }
            if (error.isValidName(checkProduct.getName())) {
              throw new ProductException(ErrorCode.INVALID_INPUT_CHARACTER);
            }
            if (error.isExistName(checkProduct.getName(), products)) {
              throw new ProductException(ErrorCode.EXIST_ALREADY_NAME);
            }

            // price 검사
            if (String.valueOf(checkProduct.getPrice()).isEmpty()) {
              throw new ProductException(ErrorCode.INVALID_INPUT_BLANK);
            }
            if (error.isValidNumber(String.valueOf(checkProduct.getPrice()))) {
              throw new ProductException(ErrorCode.INVALID_INPUT_NUMBER);
            }
            if (error.isValidPrice(String.valueOf(checkProduct.getPrice()))) {
              throw new ProductException(ErrorCode.INVALID_INPUT_PRICE);
            }

            // stock 검사
            if (String.valueOf(checkProduct.getStock()).isEmpty()) {
              throw new ProductException(ErrorCode.INVALID_INPUT_STOCK);
            }
            if (error.isValidNumber(String.valueOf(checkProduct.getStock()))) {
              throw new ProductException(ErrorCode.INVALID_INPUT_NUMBER);
            }
            if (error.isValidStock(String.valueOf(checkProduct.getStock()))) {
              throw new ProductException(ErrorCode.INVALID_INPUT_STOCK);
            }

            //유효성 검사 모두 통과하면 상품 리스트에 추가
            products.add(new Product(productNo++, checkProduct.getName(), checkProduct.getPrice(),
                checkProduct.getStock()));
            return new ResponseDto("success", products);

          } catch (Exception e) {
            return new ResponseDto("fail", products);
          }
        }

        // 상품 수정
        case 2 -> {
          try {
            // productNo 검사
            if (String.valueOf(checkProduct.getNo()).isEmpty()) {
              throw new ProductException(ErrorCode.INVALID_INPUT_BLANK);
            }
            if (error.isValidNumber(String.valueOf(checkProduct.getNo()))) {
              throw new ProductException(ErrorCode.INVALID_INPUT_NUMBER);
            }
            if (!error.isExistProduct(checkProduct.getNo(), products)) {
              throw new ProductException(ErrorCode.PRODUCT_NO_INFORMATION);
            }

            // name 검사
            if (checkProduct.getName().isEmpty()) {
              throw new ProductException(ErrorCode.INVALID_INPUT_BLANK);
            }
            if (error.isValidName(checkProduct.getName())) {
              throw new ProductException(ErrorCode.INVALID_INPUT_CHARACTER);
            }
            if (error.isExistName(checkProduct.getName(), products)) {
              throw new ProductException(ErrorCode.EXIST_ALREADY_NAME);
            }

            // price 검사
            if (String.valueOf(checkProduct.getPrice()).isEmpty()) {
              throw new ProductException(ErrorCode.INVALID_INPUT_BLANK);
            }
            if (error.isValidNumber(String.valueOf(checkProduct.getPrice()))) {
              throw new ProductException(ErrorCode.INVALID_INPUT_NUMBER);
            }
            if (error.isValidPrice(String.valueOf(checkProduct.getPrice()))) {
              throw new ProductException(ErrorCode.INVALID_INPUT_PRICE);
            }

            // stock 검사
            if (String.valueOf(checkProduct.getStock()).isEmpty()) {
              throw new ProductException(ErrorCode.INVALID_INPUT_STOCK);
            }
            if (error.isValidNumber(String.valueOf(checkProduct.getStock()))) {
              throw new ProductException(ErrorCode.INVALID_INPUT_NUMBER);
            }
            if (error.isValidStock(String.valueOf(checkProduct.getStock()))) {
              throw new ProductException(ErrorCode.INVALID_INPUT_STOCK);
            }

            //유효성 검사 모두 통과하면 상품 리스트 수정
            Product deleteProduct = null;
            for (Product product : products) {
              if (product.getNo() == checkProduct.getNo()) {
                deleteProduct = product;
                break;
              }
            }
            products.remove(deleteProduct);
            products.add(checkProduct);
            products.sort((Comparator.comparingInt(Product::getNo)));
            return new ResponseDto("success", products);

          } catch (Exception e) {
            return new ResponseDto("fail", products);
          }
        }
        case 3 -> { //상품 삭제
          try {
            // productNo 검사
            if (String.valueOf(productNo).isEmpty()) {
              throw new ProductException(ErrorCode.INVALID_INPUT_BLANK);
            }
            if (error.isValidNumber(String.valueOf(productNo))) {
              throw new ProductException(ErrorCode.INVALID_INPUT_NUMBER);
            }
            if (!error.isExistProduct(checkProduct.getNo(), products)) {
              throw new ProductException(ErrorCode.PRODUCT_NO_INFORMATION);
            }

            //유효성 검사 모두 통과하면 상품 리스트 수정
            Product deleteProduct = null;
            for (Product product : products) {
              if (product.getNo() == checkProduct.getNo()) {
                deleteProduct = product;
                break;
              }
            }
            products.remove(deleteProduct);
            return new ResponseDto("success", products);

          } catch (Exception e) {
            return new ResponseDto("fail", products);
          }
        }
      }
      return null;
    }

    private static String makeJson(ResponseDto response) {
      Gson json = new Gson();
      String jsonString = json.toJson(response);
      System.out.println("request = " + jsonString);
      System.out.println();
      return jsonString;
    }

    private static RequestDto parseJson(String jsonString) {
      Gson json = new Gson();
      System.out.println("response = " + jsonString);
      return json.fromJson(jsonString, RequestDto.class);
    }
  }
}
