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

          //클라이언트에게 보낼 응답
          ResponseDto response = productService(request);

          // makeJson
          String jsonString = makeJson(productService(request));

          // response 반환
          serverWriter.println(jsonString);

          //success -> 수정된 list 보냄
          if (response.getStatus().equals("success")) {
            for (Product product : products) {
              serverWriter.println(product.getNo());
              serverWriter.println(product.getName());
              serverWriter.println(product.getPrice());
              serverWriter.println(product.getStock());
            }
          }
          serverWriter.flush();
        }
      } catch (IOException e) {
        e.getStackTrace();
      }
    }

    // 유효성 검사, 상품 리스트 생성, 수정, 삭제
    private static ResponseDto productService(RequestDto request) {
      Product checkProduct = request.getData();
      //1. request를 보고 리스트에 넣을지 말지 본다.
      //2. 넣을 수 있든 아니든 ResponseDto -> Json 해서 클라이언트에게 전달
      switch (request.getMenu()) {
        case 1 -> { //상품 생성
          try {
            if (error.isExistName(checkProduct.getName(), products)) {
              throw new ProductException(ErrorCode.EXIST_ALREADY_NAME);
            }
            if (error.isValidName(checkProduct.getName())) {
              throw new ProductException(ErrorCode.INVALID_INPUT_CHARACTER);
            }
            if (error.isValidNumber(String.valueOf(checkProduct.getPrice()))) {
              throw new ProductException(ErrorCode.INVALID_INPUT_NUMBER);
            }
            if (error.isValidNumber(String.valueOf(checkProduct.getStock()))) {
              throw new ProductException(ErrorCode.INVALID_INPUT_NUMBER);
            }

            //유효성 검사 모두 통과하면 상품 리스트에 추가
            products.add(checkProduct);

          } catch (Exception e) {
            return new ResponseDto("fail", checkProduct);
          }
        }
        case 2 -> { //상품 수정
          try {
            if (!error.isExistProduct(checkProduct.getNo(), products)) {
              throw new ProductException(ErrorCode.PRODUCT_NO_INFORMATION);
            }
            if (error.isExistName(checkProduct.getName(), products)) {
              throw new ProductException(ErrorCode.EXIST_ALREADY_NAME);
            }
            if (error.isValidName(checkProduct.getName())) {
              throw new ProductException(ErrorCode.INVALID_INPUT_CHARACTER);
            }
            if (error.isValidNumber(String.valueOf(checkProduct.getPrice()))) {
              throw new ProductException(ErrorCode.INVALID_INPUT_NUMBER);
            }
            if (error.isValidNumber(String.valueOf(checkProduct.getStock()))) {
              throw new ProductException(ErrorCode.INVALID_INPUT_NUMBER);
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

          } catch (Exception e) {
            return new ResponseDto("fail", checkProduct);
          }
        }
        case 3 -> { //상품 삭제

        }
      }
      return new ResponseDto("success", checkProduct);
    }

    private static String makeJson(ResponseDto response) {
      Gson json = new Gson();
      return json.toJson(response);
    }

    private static RequestDto parseJson(String jsonString) {
      Gson json = new Gson();
      return json.fromJson(jsonString, RequestDto.class);
    }
  }
}
