package exec;

import dto.RequestDto;
import dto.ResponseDto;
import dto.ResponseStatus;
import exception.ProductException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import util.ProductValidator;
import vo.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * 상품 관리 프로그램 서버
 */
public class ProductServer {

    // 상품 저장소
    private static final List<Product> productStore = Collections.synchronizedList(new ArrayList<>());
    private static int productNo = 0;
    private static final int PORT_NUM = 8888;


    public static void main(String[] args) {
        ProductServer productServer = new ProductServer();
        productServer.start();
    }

    /**
     * 클라이언트 연결 요청을 받아 SocketClient에 위임
     */
    public void start() {
        try(ServerSocket serverSocket = new ServerSocket(PORT_NUM)) {
            while(true) {
                Socket socket = serverSocket.accept();
                SocketClient socketClient = new SocketClient(socket);
                Thread thread = new Thread(socketClient);
                thread.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 클라이언트 요청을 처리하는 클래스
     * socket을 통해 연결된 클라이언트와 통신
     */
    static class SocketClient implements Runnable {

        private final Socket socket;

        public SocketClient(Socket socket) {
            this.socket = socket;
        }

        /**
         * 1. 입력 json 문자열 파싱 -> ResponseDto 변환
         * 2. 요청 서비스 처리 -> ResponseDto 생성
         * 3. ResponseDto -> 출력 json 문자열 변환
         */
        @Override
        public void run() {
            try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                while(true) {
                    String line = reader.readLine();
                    System.out.println("request json: " + line);
                    RequestDto request = parseJsonToRequest(line);
                    // 클라이언트가 4.Exit 메뉴 선택 시 socket close
                    if("4".equals(request.getMenu())) {
                        System.out.println(socket.getInetAddress() + ": 연결을 종료합니다.");
                        socket.close();
                        return;
                    }
                    ResponseDto response = productService(request);
                    String json = convertResponseToJson(response);
                    writer.write(json + "\n");
                    writer.flush();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * json 문자열을 RequestDto 객체로 변환하는 메서드
         *
         * @param json json 형태의 문자열
         * @return RequestDto
         */
        private RequestDto parseJsonToRequest(String json) {
            JSONParser parser = new JSONParser();
            JSONObject request;
            try {
                request = (JSONObject) parser.parse(json);
                String menu = (String) request.get("menu");
                if(request.get("data") == null) {
                    return new RequestDto(menu);
                }
                JSONObject dataJson = (JSONObject) request.get("data");
                Map<String, Object> data = convertJsonToMap(dataJson);
                return new RequestDto(menu, data);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * JsonObject을 HashMap 으로 변환하는 메서드
         *
         * @param object JSONObject
         * @return HashMap
         */
        private Map<String, Object> convertJsonToMap(JSONObject object) {
            Map<String, Object> map = new HashMap<>();
            Set keys = object.keySet();
            for(Object key : keys) {
                if(key instanceof String k) {
                    map.put(k, object.get(k));
                }
            }
            return map;
        }

        /**
         * ResponseDto 객체를 json 문자열로 변환하는 메서드
         *
         * @param dto ResponseDto
         * @return json 형태의 문자열
         */
        private String convertResponseToJson(ResponseDto dto) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", dto.getStatus().getValue());
            if(ResponseStatus.SUCCESS == dto.getStatus()) {
                List<Product> list = (List<Product>)dto.getData();
                JSONArray jsonProductArray = new JSONArray();
                for(Product product: list) {
                    JSONObject jsonProduct = new JSONObject();
                    jsonProduct.put("no", product.getNo());
                    jsonProduct.put("name", product.getName());
                    jsonProduct.put("price", product.getPrice());
                    jsonProduct.put("stock", product.getStock());
                    jsonProductArray.add(jsonProduct);
                }
                response.put("data", jsonProductArray);
            } else if(ResponseStatus.FAIL == dto.getStatus()) {
                response.put("message", dto.getData());
            }
            return new JSONObject(response).toJSONString();
        }

        /**
         * 상품 서비스 메서드
         * RequestDto.menu 에 따라 서비스 분기
         * 클라이언트 응답 객체 ResponseDto 반환
         *
         * @param requestDto 클라이언트 요청 객체
         * @return 서비스 처리 결과에 따라 ResponseDto 생성하여 반환
         */
        private ResponseDto productService(RequestDto requestDto) {
            try {
                switch (requestDto.getMenu()) {
                    case "1" -> createProduct(requestDto.getData());
                    case "2" -> updateProduct(requestDto.getData());
                    case "3" -> deleteProduct(requestDto.getData());
                    default -> throw new ProductException("존재하지 않는 메뉴입니다.");
                }
                return new ResponseDto<>(ResponseStatus.SUCCESS, productStore);
            } catch(ProductException e) {
                return new ResponseDto<>(ResponseStatus.FAIL, e.getMessage());
            }
        }

        /**
         * 상품 생성 메서드
         *
         * @param prams
         */
        private void createProduct(Map<String, Object> prams) {
            String name = prams.get("name").toString();
            String price = prams.get("price").toString();
            String stock = prams.get("stock").toString();
            if(ProductValidator.validateName(name) && ProductValidator.validatePrice(price) && ProductValidator.validateStock(stock)) {
                Product newProduct = new Product(++productNo, name, Integer.parseInt(price), Integer.parseInt(stock));
                productStore.add(newProduct);
            } else {
                throw new ProductException("입력 정보가 올바르지 않아 상품 생성에 실패했습니다.");
            }
        }

        /**
         * 상품 수정 메서드
         *
         * @param prams
         */
        private void updateProduct(Map<String, Object> prams) {
            String no = prams.get("no").toString();
            String name = prams.get("name").toString();
            String price = prams.get("price").toString();
            String stock = prams.get("stock").toString();
            if(ProductValidator.validateNo(no) && ProductValidator.validateName(name) && ProductValidator.validatePrice(price) && ProductValidator.validateStock(stock)) {
                int index = findProductIndexByNo(Integer.parseInt(no));
                Product findProduct = productStore.get(index);
                findProduct.updateProduct(name, Integer.parseInt(price), Integer.parseInt(stock));
            } else {
                throw new ProductException("입력 정보가 올바르지 않아 상품 수정에 실패했습니다.");
            }
        }

        /**
         * 상품 삭제 메서드
         *
         * @param prams
         */
        private void deleteProduct(Map<String, Object> prams) {
            String no = prams.get("no").toString();
            if(ProductValidator.validateNo(no)) {
                int index = findProductIndexByNo(Integer.parseInt(no));
                productStore.remove(index);
            } else {
                throw new ProductException("입력 정보가 올바르지 않아 상품 삭제에 실패했습니다.");
            }
        }

        private int findProductIndexByNo(int no) {
            for(int i = 0; i < productStore.size(); i++) {
                if(productStore.get(i).getNo() == no) {
                    return i;
                }
            }
            throw new ProductException("존재하지 않는 상품입니다.");
        }
    }
}
