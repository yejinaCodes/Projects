import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ProductClient {
  //static Socket socket;
  static String receieved_msg="";
  static Socket clientSocket() throws IOException {
    Socket socket;
    socket = new Socket("localhost", 9999); //상대방 ip와 열린 port 지정해주기
    return socket;
  }

//  static void startClient() {
//    Thread thread = new Thread(new Runnable() {
//      @Override
//      public void run() {
//        try{
//          Socket socket = clientSocket();
//        }catch (Exception e){
//          System.out.println("서버와 연결 실패");
//        }
//        try{
//          receieved_msg = receiveData();
//        }catch (IOException e){
//          throw new RuntimeException(e);
//        }
//      }
//    });
//    thread.start();
//  }

//  static String receiveData() throws IOException { //server한데 status + product info 받기
//    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//    while(true){
//      try{
//        String db_products = input.readLine();
//        return db_products;
//      }catch(Exception e){
//        System.out.println("서버와 통신 불가능");
//      }
//    }
//  }


//  static void sendData(String data) throws IOException { //server한데 데이터 보내기
//    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//    Thread thread = new Thread(){
//      @Override
//      public void run(){
//        try{
//          out.write(data);
//        }catch(Exception e){
//          System.out.println("서버와 통신 불가능");
//          //추가 exception handling?
//        }
//      }
//    };
//    thread.start();
//  }


  public static void main(String[] args) throws IOException {

    boolean flag = true;
    //Socket socket = clientSocket();
    Socket socket = new Socket("192.168.70.8", 9999);
    System.out.println(socket);
    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));


    while(flag) {
      //Socket socket = startClient();
      //BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      //BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
      Scanner sc = new Scanner(System.in);
      //String db_products = in.readLine(); //상품 목록 가지고 오기
      //check
      System.out.println("check");
      //received_msg = in.readLine() 이 왜 안됨?

      //get Product object from db
      //receieved_msg = in.readLine();
      //System.out.println("please: " + receieved_msg);
      /*
      if(!receieved_msg.equals("")){
        receieved_msg = in.readLine();
      }*/

      System.out.println("[상품 목록]");
      System.out.println("-------------------------------------------------");
      System.out.println("no \t\t name               price \t\t stock");
      System.out.println("-------------------------------------------------");
      System.out.println(receieved_msg); //많을 경우 하나씩 프린트해야함


      System.out.println("-------------------------------------------------");
      System.out.println("메뉴:  1.Create | 2.Update | 3.Delete | 4.Exit");
      System.out.print("선택: ");


      int menu = sc.nextInt();

      String name;
      int no;
      int price;
      int stock;

      switch(menu) {
        case 1:
          System.out.println("[상품 생성]");
          System.out.print("상품 이름: ");
          name = sc.next();
          System.out.print("상품 가격: ");
          price = sc.nextInt();
          System.out.print("상품 재고: ");
          stock = sc.nextInt();

          Product product1 = new Product();
          product1.setName(name);
          product1.setPrice(price);
          product1.setStock(stock);

          JSONObject jsobj1 = product1.string_to_json(product1);
          System.out.println("jsobj1 check: " + jsobj1);
          out.write((jsobj1.toJSONString()+ '\n'));
          out.flush();

          break;

        case 2:
          System.out.println("[상품 수정]");
          System.out.print("상품 번호: ");
          no = sc.nextInt();
          System.out.print("이름 변경: ");
          name = sc.next();
          System.out.print("가격 변경: ");
          price = sc.nextInt();
          System.out.print("재고 변경: ");
          stock = sc.nextInt();

          Product product2 = new Product();
          product2.setName(name);
          product2.setPrice(price);
          product2.setStock(stock);

          JSONObject jsobj2 = product2.string_to_json(product2);

          out.write((jsobj2.toJSONString()+ "\n"));
          out.flush();
          break;

        case 3:
          System.out.println("[상품 삭제]");
          System.out.print("상품 번호: ");
          no = sc.nextInt();

          Product product3 = new Product();
          product3.setNo(no);
          JSONObject jsobj3 = product3.string_to_json(product3);
          out.write((jsobj3.toJSONString()+'\n'));

          out.flush();
          break;

        case 4:
          flag = false;
          System.out.println("프로그램을 종료합니다.");
          socket.close();
          break;
      }
      //receieved_msg = in.readLine();
      //System.out.println(receieved_msg);
    }


  }
}

