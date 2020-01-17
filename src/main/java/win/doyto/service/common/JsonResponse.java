package win.doyto.service.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class JsonResponse<T> {
    private static final String SUCCESS_CODE = "0";

    private String code;

    private String message;

    private T data;

    private Map<String, List<String>> errors;

    public static <T> JsonResponse<T> success() {
        return success(null);
    }

    public static <T> JsonResponse<T> success(T data) {
        JsonResponse<T> response = new JsonResponse<>();
        response.setCode(SUCCESS_CODE);
        response.setData(data);
        response.setMessage("Success");
        return response;
    }

    public static <T> JsonResponse<T> build(ErrorCode errorCode) {
        return build(errorCode.getCode(), errorCode.getMessage());
    }

    public static <T> JsonResponse<T> build(String statusCode, String message) {
        JsonResponse<T> response = new JsonResponse<>();
        response.setCode(statusCode);
        response.setMessage(message);
        return response;
    }

    public synchronized void addError(String fieldName, String message) {
        internalGetErrors().computeIfAbsent(fieldName, k -> new ArrayList<>()).add(message);
    }

    private synchronized Map<String, List<String>> internalGetErrors() {
        if (errors == null) {
            errors = new LinkedHashMap<>();
        }
        return errors;
    }

    public Boolean getSuccess() {
        return SUCCESS_CODE.equals(code);
    }

}
