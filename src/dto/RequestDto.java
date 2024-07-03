package dto;

import java.util.Map;

public class RequestDto {

    private final String menu;
    private Map<String, Object> data;

    public RequestDto(String menu) {
        this.menu = menu;
    }

    public RequestDto(String menu, Map<String, Object> data) {
        this.menu = menu;
        this.data = data;
    }

    public String getMenu() {
        return menu;
    }

    public Map<String, Object> getData() {
        return data;
    }
}
