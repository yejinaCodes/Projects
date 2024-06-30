package dto;

public enum ResponseStatus {
    SUCCESS("success"),
    FAIL("fail");

    private String value;

    ResponseStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
