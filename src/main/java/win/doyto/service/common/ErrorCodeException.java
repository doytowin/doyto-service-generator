package win.doyto.service.common;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * ErrorCodeException
 *
 * @author f0rb
 * @date 2018-11-20
 */
public class ErrorCodeException extends RuntimeException {

    @Getter
    private final ErrorCode errorCode;

    public ErrorCodeException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCodeException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        String errorCodePackage = Constant.getErrorCodePackage();
        if (StringUtils.isNotBlank(errorCodePackage)) {
            return Arrays.stream(super.getStackTrace())
                    .filter(stackTraceElement -> stackTraceElement.getClassName().startsWith(errorCodePackage))
                    .toArray(StackTraceElement[]::new);
        }
        return super.getStackTrace();
    }
}
