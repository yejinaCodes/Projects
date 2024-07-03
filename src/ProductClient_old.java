import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ProductClient_old {

  public static Socket createSocket() throws IOException {
    Socket socket;
    socket = new Socket("172.30.1.52", 9999); //상대방 ip와 열린 port 지정해주기
    return socket;
  }

  public static void main(String[] args) {
    //List<Product>
    Scanner sc = new Scanner(System.in);

    System.out.println("Connect to Server? Type 'Y' or 'y' for YES");
    String c = sc.next();
    if(c.equals("Y") || c.equals("y")){
      try {
        Socket socket = createSocket();
        InputStream input = socket.getInputStream();
        OutputStream output = socket.getOutputStream();

        //String message = sc.next();
        String message = "connection with yejin";

        JSONObject message2 = new JSONObject();
        message2.put("name", "Television");
        message2.put("price($)", 100);
        message2.put("stock", 10);

        String message2_string = message2.toJSONString();
        //output.write(message2_string.getBytes());
        //output.write(message.getBytes());

        //Product type으로 name, price, stock 보내기
        Product product = new Product();
        product.setName("TV");
        product.setPrice(100);
        product.setStock(11);

        JSONObject message3 = product.string_to_json(product);
        String message3_string = message3.toJSONString();
        output.write(message3_string.getBytes());
        ////

        byte[] data = new byte[4096];
        int data_length = input.read(data); //read outputs data length in int type.
        String result = new String(data, 0, data_length); //data type이 byte이기 때문에 string으로 바꿔줘야 함. 0~data의 길이까지
        //JSON형태로 변환하기
        JSONParser parser = new JSONParser();
        JSONObject json_obj = (JSONObject)parser.parse(result);

        String feedback = (String)json_obj.get("status");
        System.out.println(feedback);

        //String을 JSON형태로 변환하기
//        Product response_server = new Product();
//        response_server.string_to_json(result);
//

        System.out.println();

        //System.out.println(result);

        //Exit 조건이면 socket 끊기.
        // socket.close();

      } catch (IOException e) {
        throw new RuntimeException(e);

      } catch (ParseException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
