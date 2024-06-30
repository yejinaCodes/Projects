package util;

/**
 * 상품 유효성 검증 로직 클래스
 */
public class ProductValidator {

    /**
     * str이 null 이나 빈 값, 공백 값인지 확인
     * @param str 문자열
     * @return str이 empty 값이면 true, 아니면 false
     */
    public static boolean isEmptyValue(String str) {
        return str == null || str.isBlank();
    }

    /**
     * str이 양의 정수인지 확인
     * @param str 문자열
     * @return str이 양의 정수이면 true, 아니면 false
     */
    public static boolean isPositiveInteger(String str) {
        try {
            int intValue = Integer.parseInt(str);
            if(intValue < 1) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
