package exception;

public enum ErrorCode {

  INVALID_INPUT_NUMBER(400, "유효하지 않은 숫자입니다.", "N001"),
  INVALID_INPUT_CHARACTER(400, "유효하지 않은 문자입니다.", "C001"),
  INVALID_INPUT_BLANK(400, "빈문자입니다.", "B001"),
  INVALID_INPUT_PRICE(400, "유효하지 않은 가격입니다.", "P001"),
  INVALID_INPUT_STOCK(400, "유효하지 않은 재고입니다.", "S001"),
  EXIST_ALREADY_NAME(400, "이미 존재하는 상품 이름입니다.", "N002"),
  PRODUCT_NO_INFORMATION(404, "상품목록에 존재하지 않은 정보입니다.", "I001");

  private int status;
  private String message;
  private String code;

  ErrorCode(int status, String message, String code) {
    this.status = status;
    this.message = message;
    this.code = code;
  }

  public int getStatus() {
    return status;
  }

  public String getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }
}
