package dto;

import java.util.List;

public class ResponseDto {
  private String status;
  private List<Product> data;

  public ResponseDto(String status, List<Product> data) {
    this.status = status;
    this.data = data;
  }

  public String getStatus() {
    return status;
  }

  public List<Product> getData() {
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
