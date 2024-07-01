package exception;

public class ProductException extends RuntimeException {
  private ErrorCode errorCode;

  public ProductException(ErrorCode errorCode) {
    this.errorCode = errorCode;
    System.out.println("\n============= error 발생 ===============");
    System.out.println(
        errorCode.getCode() + " / " + errorCode.getMessage() + " / " + errorCode.getStatus());
    System.out.println("=======================================\n");
  }

}
