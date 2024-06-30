package exec;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import util.ProductValidator;
import vo.Product;

import java.io.*;
import java.net.Socket;
import java.util.*;

/**
 * 상품 관리 프로그램 클라이언트
 */
public class ProductClient {

    private static final String SERVER_IP_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT_NUM = 8888;
    private final Scanner sc = new Scanner(System.in);
    private List<Product> productList = new ArrayList<>();

    public static void main(String[] args) {
        ProductClient client = new ProductClient();
        client.start();
    }

    public void start() {
        try(Socket socket = new Socket(SERVER_IP_ADDRESS, SERVER_PORT_NUM);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))){
            while(true) {
                printProductList();
                printMenus();
                System.out.print("선택: ");
                String menu = sc.nextLine();
                switch (menu) {
                    case "1" -> {
                        Map<String, Object> product = createProduct();
                        JSONObject request = createJsonRequest(menu, product);
                        writer.write(request.toJSONString());
                        writer.newLine();
                        writer.flush();
                    }
                    case "2" -> {
                        Map<String, Object> product = updateProduct();
                        JSONObject request = createJsonRequest(menu, product);
                        writer.write(request.toJSONString());
                        writer.newLine();
                        writer.flush();
                    }
                    case "3" -> {
                        Map<String, Object> product = deleteProduct();
                        JSONObject request = createJsonRequest(menu, product);
                        writer.write(request.toJSONString());
                        writer.newLine();
                        writer.flush();
                    }
                    case "4" -> {
                        JSONObject request = createJsonRequest(menu);
                        writer.write(request.toJSONString());
                        writer.newLine();
                        writer.flush();
                        return;
                    }
                    default -> {
                        System.out.println("1~4 중 하나를 입력해주세요.");
                        continue;
                    }
                }
                String response = reader.readLine();
                parseResponse(response);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 서버 응답 json을 파싱하여 요청 결과에 대한 처리
     * status == success: 상품 목록 재할당
     * status == fail: 오류 메세지 출력
     *
     * @param response json 형태의 문자열
     */
    private void parseResponse(String response) {
        System.out.println(response);
        JSONParser parser = new JSONParser();
        try{
            JSONObject body = (JSONObject) parser.parse(response);
            String status = (String) body.get("status");
            if("fail".equals(status)) {
                String message = (String) body.get("message");
                System.out.println(message);
            } else {
                JSONArray jsonArray = (JSONArray) body.get("data");
                ArrayList<Product> products = new ArrayList<>();
                for (Object json : jsonArray) {
                    JSONObject jsonProduct = (JSONObject) json;
                    System.out.println(jsonProduct.toJSONString());
                    Product product = new Product(
                            ((Long) jsonProduct.get("no")).intValue(),
                            (String) jsonProduct.get("name"),
                            ((Long) jsonProduct.get("price")).intValue(),
                            ((Long) jsonProduct.get("stock")).intValue()
                    );
                    products.add(product);
                }
                productList = products;
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 사용자 입력에 대한 요청 json 문자열 생성
     *
     * @param menu 메뉴 번호
     * @return
     */
    private JSONObject createJsonRequest(String menu) {
        Map<String, Object> request = new HashMap<>();
        request.put("menu", menu);
        return new JSONObject(request);
    }

    /**
     * 사용자 입력에 대한 요청 json 문자열 생성
     *
     * @param menu 메뉴 번호
     * @param data 상품 데이터
     * @return
     */
    private JSONObject createJsonRequest(String menu, Map<String, Object> data) {
        Map<String, Object> request = new HashMap<>();
        request.put("menu", menu);
        request.put("data", new JSONObject(data));
        return new JSONObject(request);
    }

    /**
     * 상품 생성에 대한 사용자 입력
     * 입력마다 유효성 검증 -> 검증 실패 시 메세지 출력
     *
     * @return 상품 생성 데이터
     */
    private Map<String, Object> createProduct() {
        Map<String, Object> product = new HashMap<>();
        while(true) {
            System.out.println("[상품 생성]");
            System.out.print("상품 이름: ");
            String name = sc.nextLine();
            if(ProductValidator.isEmptyValue(name)) {
                System.out.println("상품 이름은 빈 값을 입력할 수 없습니다.");
                continue;
            }
            System.out.print("상품 가격: ");
            String price = sc.nextLine();
            if(!ProductValidator.isPositiveInteger(price)) {
                System.out.println("상품 가격은 1원 이상을 입력해주세요.");
                continue;
            }
            System.out.print("상품 재고: ");
            String stock = sc.nextLine();
            if(!ProductValidator.isPositiveInteger(stock)) {
                System.out.println("상품 재고는 1원 이상을 입력해주세요.");
                continue;
            }
            product.put("name", name);
            product.put("price", Integer.parseInt(price));
            product.put("stock", Integer.parseInt(stock));
            break;
        }
        return product;
    }

    /**
     * 상품 수정에 대한 사용자 입력
     * 입력마다 유효성 검증 -> 검증 실패 시 메세지 출력
     *
     * @return 상품 수정 데이터
     */
    private Map<String, Object> updateProduct() {
        Map<String, Object> product = new HashMap<>();
        while(true) {
            System.out.println("[상품 수정]");
            System.out.print("상품 번호: ");
            String no = sc.nextLine();
            if(!ProductValidator.isPositiveInteger(no)) {
                System.out.println("상품 번호는 1 이상의 숫자를 입력해주세요.");
                continue;
            }
            System.out.print("이름 변경");
            String name = sc.nextLine();
            if(ProductValidator.isEmptyValue(name)) {
                System.out.println("상품 이름은 빈 값을 입력할 수 없습니다.");
                continue;
            }
            System.out.print("가격 변경: ");
            String price = sc.nextLine();
            if(!ProductValidator.isPositiveInteger(price)) {
                System.out.println("상품 가격은 1원 이상을 입력해주세요.");
                continue;
            }
            System.out.print("재고 변경: ");
            String stock = sc.nextLine();
            if(!ProductValidator.isPositiveInteger(stock)) {
                System.out.println("상품 재고는 1원 이상을 입력해주세요.");
                continue;
            }
            product.put("no", Integer.parseInt(no));
            product.put("name", name);
            product.put("price", Integer.parseInt(price));
            product.put("stock", Integer.parseInt(stock));
            break;
        }
        return product;
    }

    /**
     * 상품 삭제에 대한 사용자 입력
     * 입력마다 유효성 검증 -> 검증 실패 시 메세지 출력
     *
     * @return 상품 삭제 데이터
     */
    private Map<String, Object> deleteProduct() {
        Map<String, Object> product = new HashMap<>();
        while(true) {
            System.out.println("[상품 삭제]");
            System.out.print("상품 번호: ");
            String no = sc.nextLine();
            if(!ProductValidator.isPositiveInteger(no)) {
                System.out.println("상품 번호는 1 이상의 숫자를 입력해주세요.");
                continue;
            }
            product.put("no", Integer.parseInt(no));
            break;
        }
        return product;
    }

    /**
     * 상품 목록 출력
     */
    public void printProductList() {
        System.out.println("-----------------------------------------------");
        System.out.printf("%-5s%-20s%-10s%-10s\n", "no", "name", "price", "stock");
        System.out.println("-----------------------------------------------");
        for(Product product : productList) {
            System.out.printf("%-5d%-20s%-10d%-10d\n", product.getNo(), product.getName(), product.getPrice(), product.getStock());
        }
        System.out.println("-----------------------------------------------");
    }

    /**
     * 사용자 메뉴 출력
     */
    public void printMenus() {
        System.out.println("메뉴: 1.Create | 2.Update | 3.Delete | 4.Exit");
    }
}
