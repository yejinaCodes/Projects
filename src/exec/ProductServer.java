package exec;

import dto.ProductDto;
import dto.RequestDto;
import dto.ResponseDto;
import dto.ResponseStatus;
import exception.ProductException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import vo.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * 상품 관리 프로그램 서버
 */
public class ProductServer {

    // 상품 저장소
    private static final List<Product> productStore = new Vector<>();
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
            try {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
            try {
                JSONObject request = (JSONObject) parser.parse(json);
                String menu = (String) request.get("menu");
                if(request.get("data") == null) {
                    return new RequestDto(menu);
                }
                JSONObject data = (JSONObject) request.get("data");

                // TODO: 유효성 확인, 타입 변환 처리
                ProductDto dto = new ProductDto(
                        data.get("no") == null ? null : ((Long) data.get("no")).intValue(),
                        (String) data.get("name"),
                        data.get("price") == null ? null : ((Long) data.get("price")).intValue(),
                        data.get("stock") == null ? null : ((Long) data.get("stock")).intValue()
                );
                return new RequestDto(menu, dto);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
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
                    case "1" -> createProduct(requestDto.getProduct());
                    case "2" -> updateProduct(requestDto.getProduct());
                    case "3" -> deleteProduct(requestDto.getProduct());
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
         * @param dto
         */
        private void createProduct(ProductDto dto) {
            Product newProduct = dto.toCreate(++productNo);
            productStore.add(newProduct);
        }

        /**
         * 상품 수정 메서드
         *
         * @param dto
         */
        private void updateProduct(ProductDto dto) {
            Product findProduct = findProductByNo(dto.getNo());
            findProduct.updateProduct(dto.getName(), dto.getPrice(), dto.getStock());
        }

        /**
         * 상품 삭제 메서드
         *
         * @param dto
         */
        private void deleteProduct(ProductDto dto) {
            Product findProduct = findProductByNo(dto.getNo());
            productStore.remove(findProduct);
        }

        /**
         * 상품 조회 메서드
         *
         * @param no
         * @return
         */
        private Product findProductByNo(int no) {
            for (Product product : productStore) {
                if(product.getNo() == no) {
                    return product;
                }
            }
            throw new ProductException("존재하지 않는 상품입니다.");
        }
    }
}
