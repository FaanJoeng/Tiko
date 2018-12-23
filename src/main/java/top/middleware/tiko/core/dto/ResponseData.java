package top.middleware.tiko.core.dto;

import java.util.HashMap;
import java.util.Map;

public class ResponseData extends HashMap {

    public ResponseData() {
        put("status", true);
    }

    public Map code(String code) {

        put("code", code);
        return this;
    }

    public Map message(String message) {
        put("message", message);
        return this;
    }
}
