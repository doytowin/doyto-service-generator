package win.doyto.service.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import win.doyto.query.controller.RestApi;

import java.lang.reflect.Method;

/**
 * RestResponseAdvice
 *
 * @author f0rb on 2017-01-15.
 */
@Slf4j
@ControllerAdvice
class JsonResponseAdvice implements ResponseBodyAdvice<Object> {
    private static JsonResponse wrap(Object body) {
        if (body instanceof JsonResponse) {
            return (JsonResponse) body;
        } else if (body instanceof ErrorCode) {
            return JsonResponse.build((ErrorCode) body);
        } else {
            return JsonResponse.success(body);
        }
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {

        Method method = returnType.getMethod();
        Class declaringClass = method.getDeclaringClass();
        log.debug("拦截方法: {}#{} - {}", declaringClass.getName(), method.getName(), method.getReturnType());
        if (AbstractJackson2HttpMessageConverter.class.isAssignableFrom(converterType)
            && RestApi.class.isAssignableFrom(declaringClass)
        ) {
            log.debug("JsonResponse: {}.{}", declaringClass.getName(), method.getName());
            return true;
        }

        return false;
    }

    @Override
    public Object beforeBodyWrite(
            Object body, MethodParameter returnType, MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request, ServerHttpResponse response
    ) {
        try {
            return wrap(body);
        } catch (Exception e) {
            log.info("JsonResponse: {}", e.getMessage());
            return body;
        }
    }
}
