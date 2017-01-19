package org.grs.generator.component.spring;

import java.util.List;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter4;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import org.grs.generator.common.Pageable;
import org.grs.generator.common.ResponseCode;

/**
 * RestResponseAdvice
 *
 * @author f0rb on 2017-01-15.
 */
@ControllerAdvice
public class RestResponseAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        if (converterType.equals(FastJsonHttpMessageConverter4.class)
                || converterType.equals(FastJsonHttpMessageConverter.class)
                || converterType.equals(MappingJackson2HttpMessageConverter.class)
                ) {
            if (!returnType.getMethod().getReturnType().equals(RestResponse.class)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends
            HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body == null) {
            return new RestResponse(ResponseCode.RECORD_NOT_FOUND);
        } else if (body instanceof BindingResult) {
            RestResponse ret = new RestResponse(ResponseCode.VALIDATION_FAILED);
            BindingResult result = (BindingResult) body;
            List<FieldError> fieldErrorList = result.getFieldErrors();
            for (FieldError fieldError : fieldErrorList) {
                ret.addError(fieldError.getField(), fieldError.getDefaultMessage());
            }
            return ret;
        } else if (body instanceof RestResponse) {
            return body;
        } else if (body instanceof Pageable.Page) {
            Pageable.Page page = (Pageable.Page) body;
            RestResponse ret = new RestResponse();
            ret.setResult(page.getData());
            ret.setTotal(page.getTotal());
            return body;
        } else {
            return new RestResponse(body);
        }
    }
}
