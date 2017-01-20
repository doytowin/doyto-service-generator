package org.grs.generator.component.spring;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import org.grs.generator.common.ResponseCode;

/**
 * 返回给移动客户端的JSON对象的结构
 *
 * @author Yuanzhen on 2015-09-07.
 */
@lombok.Getter
@lombok.Setter
public class RestResponse implements Serializable {
    private Boolean success = true;
    private String message;
    private String info;
    private String code = "0";
    private Object result;
    private Long total;
    private Map<String, List<String>> errors;

    public RestResponse() {
    }

    public RestResponse(Object result) {
        this.result = result;
    }

    public RestResponse(Object result, Long total) {
        this.result = result;
        this.total = total;
    }

    public RestResponse(ResponseCode code) {
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

    //public synchronized Map<String, List<String>> getErrors() {
    //    return hasErrors() ? new LinkedHashMap<>(internalGetMessages()) : null;
    //}

    public synchronized void addError(String fieldName, String message) {
        final Map<String, List<String>> messages = internalGetMessages();
        List<String> filedMessages = messages.computeIfAbsent(fieldName, k -> new ArrayList<>());
        filedMessages.add(message);
    }

    public synchronized boolean hasErrors() {
        return (errors != null) && !errors.isEmpty();
    }

    private synchronized Map<String, List<String>> internalGetMessages() {
        if (errors == null) {
            errors = new LinkedHashMap<>();
        }
        return errors;
    }

    public synchronized void clearMessages() {
        internalGetMessages().clear();
    }
}
