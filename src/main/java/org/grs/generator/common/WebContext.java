package org.grs.generator.common;

import java.io.File;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 将每次请求的request和response对象存入ThreadLocal的Map里以供业务层访问.
 *
 * 在{@link WebContextFilter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)}
 * 方法中，对于需要的请求调用{@link WebContext#init(HttpServletRequest, HttpServletResponse)}
 * 和{@link WebContext#cleanup()}方法

 * @author yuanzhen
 * @version 2.0, 2012-10-17 16:46:50
 */
@Slf4j
public final class WebContext {
    private static final ThreadLocal<WebContext> contextStore = new ThreadLocal<WebContext>();
    private static String WEB_REAL_PATH = null;
    private static String WEB_URL = null;
    private static final String SESSION_REDIRECT = "redirect";

    private final HttpServletRequest request;
    private final HttpServletResponse response;


    private WebContext(HttpServletRequest req, HttpServletResponse res) {
        request = req;
        response = res;
    }

    public static void init(HttpServletRequest request, HttpServletResponse response) {
		contextStore.set(new WebContext(request, response));
	}

    public static void cleanup() {
		contextStore.remove();
	}

	/**
	 * Gets the HTTP servlet request object.
	 *
	 * @return the HTTP servlet request object.
	 */
	public static HttpServletRequest getRequest() {
		return contextStore.get().request;
	}

	/**
	 * Gets the HTTP servlet response object.
	 *
	 * @return the HTTP servlet response object.
	 */
	public static HttpServletResponse getResponse() {
		return contextStore.get().response;
	}

    public static HttpSession getSession() {
        return getRequest().getSession(true);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getSessionAttr(String name) {
        return (T) getSession().getAttribute(name);
    }

    @SuppressWarnings("unchecked")
    public static <T> T removeSessionAttr(String name) {
        T value = getSessionAttr(name);
        getSession().removeAttribute(name);
        return value;
    }

    public static void setSessionAttr(String name, Object value) {
        getSession().setAttribute(name, value);
    }

    public static String getWebUrl() {
        HttpServletRequest request = getRequest();
        if (WEB_URL == null) {
            synchronized (WebContext.class) {
                if (WEB_URL == null) {
                    StringBuffer url = request.getRequestURL();
                    String urlStr = url.substring(0, url.length() - request.getRequestURI().length() + request.getContextPath().length());
                    log.info("WEB_URL: {}", urlStr);
                    if (urlStr.contains("localhost") || urlStr.contains("127.0.0.1")) {//localhost只作为临时地址
                        return urlStr;
                    }
                    WEB_URL = urlStr;
                }
            }
        }
        return WEB_URL;
    }

    public static String getWebPath() {
        if (WEB_REAL_PATH == null) {
            synchronized (WebContext.class) {
                if (WEB_REAL_PATH == null) {
                    String path = getRequest().getServletContext().getRealPath(File.separator);
                    if (!path.endsWith(File.separator)) {//Weblogic获取的路径可能没有'/'，补一个
                        path += File.separator;
                    }
                    WEB_REAL_PATH = path;
                }
            }
        }
        log.info("Web部署路径：path=" + WEB_REAL_PATH);
        return WEB_REAL_PATH;
    }

    /**
     * 将跳转连接redirect设置到session里
     * 若redirect为空, 并且session里不存在跳转连接, 则设置当前访问地址设置到session里
     *
     * @param redirect 跳转连接
     */
    public static void setRedirect(String redirect) {
        if (StringUtils.isNotBlank(redirect)) {
            WebContext.setSessionAttr(SESSION_REDIRECT, redirect);
        } else {
            String url = WebContext.getRequest().getRequestURL().toString();
            String queryString = WebContext.getRequest().getQueryString();
            if (queryString != null) {
                url = url + "?" + queryString;
            }
            WebContext.setSessionAttr(SESSION_REDIRECT, url);
        }
    }
}
