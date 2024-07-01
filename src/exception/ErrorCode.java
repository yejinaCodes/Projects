package exception;

public enum ErrorCode {

  INVALID_INPUT_NUMBER(400, "유효하지 않은 숫자입니다.", "N001"),
  INVALID_INPUT_CHARACTER(400, "유효하지 않은 문자입니다.", "C001"),
  EXIST_ALREADY_NAME(400, "이미 존재하는 상품 이름입니다.", "N002"),
  PRODUCT_NO_INFORMATION(404, "상품목록에 존재하지 않은 정보입니다.", "I001");

  private int status;
  private String code;
  private String message;

  ErrorCode(int status, String code, String message) {
    this.status = status;
    this.code = code;
    this.message = message;
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
