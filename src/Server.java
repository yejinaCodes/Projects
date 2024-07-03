import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Server {

  public static List<Product> database = Collections.synchronizedList(new ArrayList<Product>()); //Multi thread 의 synchronization을 위함
  public static int db_index = 0;

  public static void main(String[] args) throws IOException {
    ServerSocket serverSocket = new ServerSocket(9999);
    //System.out.println(serverSocket);

    while (true) {
      try {
        Socket socket = serverSocket.accept();
        System.out.println("socket check" + socket);

        //각 menu 핸들링 thread로 처리
        Thread t = new ClientHandler(socket);
        System.out.println("t check: " + t);
        t.start();

      } catch (Exception e) {
        serverSocket.close();
      }
    }
  }

  //중첩 클래스로 구현하기
  static class ClientHandler extends Thread{
    private JSONParser jsonParser = new JSONParser();
    private Socket socket;
    private BufferedReader input;
    private BufferedWriter out;

    //Constructor 생성하기
    ClientHandler() throws IOException {}
    ClientHandler(Socket socket) throws IOException {
      this.socket = socket;
      input =  new BufferedReader(new InputStreamReader(socket.getInputStream()));
     out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

      System.out.println("from server");
      System.out.println(socket);
    }
    //Thread에서의 run()을 override하게 됨.
    @Override
      public void run() {
        String received;

        //하나의 thread를 하나의 client로 생각해야 함.
        while(true) {
          try {
            //client의 입력값 읽기
            received = input.readLine();
            if(received == null){
              socket.close();
              out.flush();
              break;
            }
            System.out.println(received);
            JSONObject jsobj = (JSONObject) jsonParser.parse(received);

            String menu = jsobj.getOrDefault("menu", "").toString();


            //menu에 따라 처리하기
            switch (menu) {
              case "1":
                //Product type으로 만들기
                String name1 = (String) jsobj.getOrDefault("name", "");
                int price = ((Long) jsobj.getOrDefault("price", 0)).intValue();
                int stock = ((Long) jsobj.getOrDefault("stock", 0)).intValue();

                //product를 database에 저장하기
                Product product = new Product(db_index, name1, price, stock);
                database.add(product);
                db_index++;
                JSONArray jsa_tmp1 = new JSONArray();
                jsa_tmp1.add("Success");

                //database에 있는 모든 product -> jsonobject -> jsonString으로 해서 보내기
                for (Product p : database) {
                  Product tmp = new Product();
                  JSONObject one_jsonobj = tmp.string_to_json(p);
                  jsa_tmp1.add(one_jsonobj);
                }
                out.write((jsa_tmp1.toJSONString()+ '\n'));
                //checking
                System.out.println("checking: " + jsa_tmp1.toJSONString());
                out.flush();
                break;

              case "2":
                int id2 = ((Long) jsobj.getOrDefault("no", 0)).intValue();
                String name2 = (String) jsobj.getOrDefault("name", "");
                int price2 = ((Long) jsobj.getOrDefault("price", 0)).intValue();
                int stock2 = ((Long) jsobj.getOrDefault("stock", 0)).intValue();

                boolean flag = false;
                for (Product p : database) {
                  if (p.getNo() == id2) {
                    p.setName(name2);
                    p.setPrice(price2);
                    p.setStock(stock2);
                    flag = true;
                  }
                }
                JSONArray jsa_tmp2 = new JSONArray();
//                if (!flag) {
//                  jsa_tmp2.add ("Failure\n");
//                }
                  //jsa_tmp2.add("Success"); //status 항목 어떻게 추가함?
                  for (Product p : database) {
                    Product tmp = new Product();
                    JSONObject one_jsonobj = tmp.string_to_json(p);
                    jsa_tmp2.add(one_jsonobj);
                  }
                out.write((jsa_tmp2.toJSONString())+'\n');
                out.flush();
                break;

              case "3":
                int id3 = ((Long) jsobj.getOrDefault("no", 0)).intValue();
                boolean flag3 = false;

                JSONArray jsa_tmp3 = new JSONArray();

                for (Product p : database) {
                  if (p.getNo() == id3) {
                    database.remove(p);
                    flag3 = true;
                  }
                }
//                if (!flag3) {
//                  jsa_tmp3.add("Failure\n");
//                }
                //jsa_tmp3.add("Success");
                  for (Product p : database) {
                    Product tmp = new Product();
                    JSONObject one_jsonobj = tmp.string_to_json(p);
                    jsa_tmp3.add(one_jsonobj);
                  }
                out.write((jsa_tmp3.toJSONString())+'\n');
                out.flush();
                break;

              case "4":
                socket.close();
                out.flush();
                break;
            }
          } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
          }
        }
    }
  }
}

/*
0. server에서 update, delete 반영안됨.
  update-
  delete-
1. status 전달
2. client에서 input값 처리해주기
3. 처음 화면에 default 상품 목록 프린트해주기
4. 예외처리해주기
 */

/*
중요)
1. out.flush() 를 꼭해줘야 함
2. readLine()으로 읽을 경우 보내는 string에 대해서 \n 인 개행문자를 추가해줘야 한다.
3. server 쪽에서 string 보낼때 각 product마다 보내는 것이 아니라 json array 사용하기
 */