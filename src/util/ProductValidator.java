package util;

/**
 * 상품 유효성 검증 로직 클래스
 */
public class ProductValidator {

    private static final int MAX_PRICE = 10000;
    private static final int MAX_STOCK = 1000;

    /**
     * str이 null 이나 빈 값, 공백 값인지 확인
     * @param str 문자열
     * @return str이 empty 값이면 true, 아니면 false
     */
    private static boolean isEmptyValue(String str) {
        return str == null || str.isBlank();
    }

    /**
     * str이 양의 정수인지 확인
     * @param str 문자열
     * @return str이 양의 정수이면 true, 아니면 false
     */
    private static boolean isIntegerNumber(String str) {
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static boolean validateName(String name) {
        return isEmptyValue(name);
    }

    public static boolean validatePrice(String price) {
        if(!isIntegerNumber(price)) {
            return false;
        }
        int iPrice = Integer.parseInt(price);
        return 0 < iPrice && iPrice < MAX_PRICE;
    }

    public static boolean validateStock(String stock) {
        if(!isIntegerNumber(stock)) {
            return false;
        }
        int iStock = Integer.parseInt(stock);
        return 0 < iStock && iStock < MAX_STOCK;
    }

    public static boolean validateNo(String no) {
        if(!isIntegerNumber(no)) {
            return false;
        }
        return Integer.parseInt(no) > 0;
    }
}
