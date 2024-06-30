package dto;

public class ResponseDto<T> {

    private final ResponseStatus status;
    private final T data;

    public ResponseDto(ResponseStatus status, T data) {
        this.status = status;
        this.data = data;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }
}


