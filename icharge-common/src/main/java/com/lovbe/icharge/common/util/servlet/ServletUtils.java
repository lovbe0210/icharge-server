package com.lovbe.icharge.common.util.servlet;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 客户端工具类
 *
 * @author 芋道源码
 */
public class ServletUtils {

    /**
     * 返回 JSON 字符串
     *
     * @param response 响应
     * @param object   对象，会序列化成 JSON 字符串
     */
    @SuppressWarnings("deprecation") // 必须使用 APPLICATION_JSON_UTF8_VALUE，否则会乱码
    public static void writeJSON(HttpServletResponse response, Object object) {
        String content = JSONUtil.toJsonStr(object);
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        Writer writer = null;
        try {
            writer = response.getWriter();
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            throw new UtilException(e);
        } finally {
            IoUtil.close(writer);
        }
    }

    /**
     * 返回附件
     *
     * @param response 响应
     * @param filename 文件名
     * @param content  附件内容
     */
    public static void writeAttachment(HttpServletResponse response, String filename, byte[] content) throws IOException {
        // 设置 header 和 contentType
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        // 输出附件
        IoUtil.write(response.getOutputStream(), false, content);
    }

    /**
     * @param request 请求
     * @return ua
     */
    public static String getUserAgent(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        return ua != null ? ua : "";
    }

    /**
     * 获得请求
     *
     * @return HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (!(requestAttributes instanceof ServletRequestAttributes)) {
            return null;
        }
        return ((ServletRequestAttributes) requestAttributes).getRequest();
    }

    public static String getUserAgent() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }
        return getUserAgent(request);
    }

    public static String getClientIP() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }
        String[] headers = {"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};
        return getClientIPByHeader(request, headers);
    }

    public static boolean isJsonRequest(ServletRequest request) {
        return StrUtil.startWithIgnoreCase(request.getContentType(), MediaType.APPLICATION_JSON_VALUE);
    }

    public static String getBody(HttpServletRequest request) {
        // 只有在 json 请求在读取，因为只有 CacheRequestBodyFilter 才会进行缓存，支持重复读取
        if (isJsonRequest(request)) {
            try (final BufferedReader reader = request.getReader()) {
                return IoUtil.read(reader);
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        }
        return null;
    }

    public static byte[] getBodyBytes(HttpServletRequest request) {
        // 只有在 json 请求在读取，因为只有 CacheRequestBodyFilter 才会进行缓存，支持重复读取
        if (isJsonRequest(request)) {
            try {
                return IoUtil.readBytes(request.getInputStream());
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        }
        return null;
    }

    public static String getClientIP(HttpServletRequest request) {
        String[] headers = {"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};
        return getClientIPByHeader(request, headers);
    }

    public static Map<String, String> getParamMap(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        for (Map.Entry<String, String[]> entry : getParams(request).entrySet()) {
            params.put(entry.getKey(), ArrayUtil.join(entry.getValue(), StrUtil.COMMA));
        }
        return params;
    }

    /**
     * 获取客户端IP
     *
     * <p>
     * headerNames参数用于自定义检测的Header<br>
     * 需要注意的是，使用此方法获取的客户IP地址必须在Http服务器（例如Nginx）中配置头信息，否则容易造成IP伪造。
     * </p>
     *
     * @param request     请求对象{@link HttpServletRequest}
     * @param headerNames 自定义头，通常在Http服务器（例如Nginx）中配置
     * @return IP地址
     * @since 4.4.1
     */
    public static String getClientIPByHeader(HttpServletRequest request, String... headerNames) {
        String ip;
        for (String header : headerNames) {
            ip = request.getHeader(header);
            if (false == NetUtil.isUnknown(ip)) {
                return NetUtil.getMultistageReverseProxyIp(ip);
            }
        }

        ip = request.getRemoteAddr();
        return NetUtil.getMultistageReverseProxyIp(ip);
    }

    /**
     * 获得所有请求参数
     *
     * @param request 请求对象{@link ServletRequest}
     * @return Map
     */
    public static Map<String, String[]> getParams(ServletRequest request) {
        final Map<String, String[]> map = request.getParameterMap();
        return Collections.unmodifiableMap(map);
    }

}
