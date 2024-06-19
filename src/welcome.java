//again
import java.lang.*;
import java.util.Scanner;

public class welcome {
    public static void main(String[] args) {
        //introduction은 바뀔 수 있기 때문.
        String greeting = "Welcome to Shopping Mall";
        String tagline = "Welcome to BookMarket!";

        String name = "";
        String phoneNumber = "";
        Scanner sc = new Scanner(System.in);
        System.out.print("당신의 이름을 입력해주세요: ");
        name = sc.nextLine();
        System.out.print("당신의 연락처를 입력해주세요: ");
        phoneNumber = sc.nextLine();

        boolean quit = false;
        while(!quit) {
            System.out.println("***********************************");
            System.out.println("\t" + greeting);
            System.out.println("\t" + tagline);
            System.out.println("***********************************");
            System.out.println("1. 고객 정보 확인하기 \t4. 바구니에 항목 추가하기");
            System.out.println("2. 장바구니 상품 목록 보기 \t5. 장바구니의 항목 수량 줄이기");
            System.out.println("3. 장바구니 비우기 \t6. 장바구니 항목 삭제하기");
            System.out.println("7. 영수증 표시하기 \t8. 종료");
            System.out.println("***********************************");

            System.out.println("메뉴 번호를 선택 해주세요: ");
            int num = sc.nextInt();
            System.out.println(num + "번을 선택했습니다.");
            if (num < 1 || num > 8) {
                System.out.println("메뉴는 1부터 8까지의 숫자로 입력해주세요");
            } else {
                switch (num) {
                    case 1:
                        System.out.println("현재 고객의 정보: " + "이름) " + name + " 연락처) " + phoneNumber);
                        break;
                    case 2:
                        System.out.println("장바구니 상품 목록 보기");
                        break;
                    case 3:
                        System.out.println("장바구니 비우기.");
                        break;
                    case 4:
                        System.out.println("장바구니 항목 추가하기");
                        break;
                    case 5:
                        System.out.println("장바구니 항목 수량 추가하기");
                        break;
                    case 6:
                        System.out.println("장바구니 항목 삭제하기");
                        break;
                    case 7:
                        System.out.println("영수증 표시하기");
                        break;
                    case 8:
                        System.out.println("프로그램 종료");
                        quit = true;
                        break;
                }
            }//else
        } //while
    }
}
