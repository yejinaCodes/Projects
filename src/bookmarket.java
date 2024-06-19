import java.io.*;
import java.nio.Buffer;
import java.util.StringTokenizer;

public class bookmarket {
    public static void main(String[] args) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;
        System.out.printf("당신의 이름을 입력하세요 : ");
        String name = br.readLine();
        System.out.printf("연락처를 입력하세요 : ");
        String phone = br.readLine();

        System.out.println("***********************************************");
        System.out.println("        Welcome to Shopping Mall               ");
        System.out.println("        Welcome to Book Market!                ");
        System.out.println("***********************************************");
        System.out.println(" 1. 고객 정보 확인하기      4. 바구니에 항목 추가하기");
        System.out.println(" 2. 장바구니 상품 목록 보기  5. 장바구니의 항목 수량 줄이기");
        System.out.println(" 3. 장바구니 비우기        6. 장바구니의 항목 삭제하기");
        System.out.println(" 7. 영수증 표시하기        8. 종료");
        System.out.println("***********************************************");

        System.out.println("메뉴 번호를 선책해주세요 ");
        st = new StringTokenizer(br.readLine());
        int menu = Integer.parseInt(st.nextToken());

        switch (menu){
            case 1:
                System.out.printf("1번을 선택했습니다");
                break;
            case 2:
                System.out.println("2번을 선택했습니다");
                break;
            case 3:
                System.out.println("3번을 선택했습니다");
                break;
            case 4:
                System.out.println("4번을 선택했습니다");
                break;
            case 5:
                System.out.println("5번을 선택했습니다");
                break;
            case 6:
                System.out.println("6번을 선택했습니다");
                break;
            case 7:
                System.out.println("7번을 선택했습니다");
                break;
            case 8:
                break;

        }


    }
}
