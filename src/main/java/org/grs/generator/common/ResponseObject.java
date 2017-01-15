package org.grs.generator.common;

import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.JSON;

/**
 * 返回给移动客户端的JSON对象的结构
 *
 * @author Yuanzhen on 2015-09-07.
 */
@lombok.Getter
@lombok.Setter
public class ResponseObject implements Serializable {
    private Boolean success = true;
    private String message;
    private String info;
    private String code = "0";
    private Object result;
    private Long total;
    private List errors;

    public ResponseObject() {
    }

    public ResponseObject(Object result) {
        this.result = result;
    }

    public ResponseObject(String code) {
        this.success = false;
        this.code = code;
        this.message = ResponseCode.resolve(code);//getMessageByCode
    }

    public ResponseObject(ResponseCode code) {
        this.success = false;
        this.code = code.getCode();
        this.message = code.getMessage();
    }

    public Boolean getSuccess() {
        return success && (message == null || message.length() == 0);
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
