package org.grs.generator.common;

import lombok.Getter;

/**
 * ResponseCode
 *
 * @author f0rb on 2017-01-16.
 */
@Getter
public enum ResponseCode {

    LOGIN_EXPIRED("1", "登录超时, 请刷新重试!"),
    ACCESS_DENIED("2", "拒绝访问!"),
    RECORD_NOT_FOUND("3", "指定记录不存在!"),
    ;

    private String code;
    private String message;

    ResponseCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String resolve(String code) {
        for (ResponseCode responseStatus : ResponseCode.values()) {
            if (responseStatus.code.equals(code)) {
                return responseStatus.message;
            }
        }
        return "未知错误[" + code + "]";
    }
}
