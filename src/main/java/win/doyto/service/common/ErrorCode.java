package win.doyto.service.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * ErrorCode
 *
 * @author f0rb
 * @date 2018-11-20
 */
public interface ErrorCode extends Serializable {

    String getMessage();

    String getCode();

    default boolean isSuccess() {
        return "0".equals(getCode());
    }

    default ErrorCode args(Object... args) {
        return build(getCode(), String.format(getMessage(), args));
    }

    @Getter
    @AllArgsConstructor
    @Slf4j
    class DefaultErrorCode implements ErrorCode {

        private final String code;

        private final String message;

    }

    static ErrorCode build(String code, String message) {
        return new DefaultErrorCode(code, message);
    }

    static ErrorCode build(ErrorCode errorCode) {
        return build(errorCode.getCode(), errorCode.getMessage());
    }

    static void assertNotNull(Object target, ErrorCode errorCode, Object... message) {
        assertFalse(target == null, errorCode, message);
    }

    static void assertFalse(boolean condition, ErrorCode errorCode, Object... message) {
        if (condition) {
            fail(errorCode, message);
        }
    }

    static void assertTrue(boolean condition, ErrorCode errorCode, Object... message) {
        if (!condition) {
            fail(errorCode, message);
        }
    }

    static void fail(ErrorCode errorCode, Object... message) {
        DefaultErrorCode.log.warn("[{}]{}: {}", errorCode.getCode(), errorCode.getMessage(), StringUtils.joinWith(",", message));
        throw new ErrorCodeException(errorCode);
    }
}
