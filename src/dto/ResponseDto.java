package dto;

public class ResponseDto {
  private String status;
  private Product data;

  public ResponseDto(String status, Product data) {
    this.status = status;
    this.data = data;
  }

  public String getStatus() {
    return status;
  }

  public Product getData() {
    return data;
  }

  @Override
  public String toString() {
    return "ResponseDto{" +
        "status='" + status + '\'' +
        ", data=" + data +
        '}';
  }
}
