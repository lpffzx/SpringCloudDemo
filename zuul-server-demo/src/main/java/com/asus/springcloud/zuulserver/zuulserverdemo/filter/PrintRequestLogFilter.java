package com.asus.springcloud.zuulserver.zuulserverdemo.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Map;

/**
 * 打印请求日志过滤器
 *
 * @author kevinli
 * @date 2018/8/13
 */
@Component
public class PrintRequestLogFilter extends ZuulFilter {

    private static Logger log = LoggerFactory.getLogger(PrintRequestLogFilter.class);

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return -999;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        log.info("{} >>> {}", request.getMethod(), request.getRequestURL().toString());
        log.info("REMOTE USER: {},  REMOTE ADDR: {},  REMOTE HOST: {},  REMOTE PORT: ", request.getRemoteUser(), request.getRemoteAddr(), request.getRemoteHost(), request.getRemotePort());

        Enumeration headerNames = request.getHeaderNames();
        StringBuilder sb = new StringBuilder();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            sb.append("{");
            sb.append(key);
            sb.append(": ");
            sb.append(value);
            sb.append("},");
        }
        if (sb.length() > 0) {
            sb.delete(sb.length() - 1, sb.length());
        }
        log.info("REQUEST HEADERS: {}", sb.toString());

        Map<String, String[]> map = request.getParameterMap();
        if (map != null) {
            sb = new StringBuilder();
            for (Map.Entry<String, String[]> entry : map.entrySet()) {
                sb.append("[");
                sb.append(entry.getKey());
                sb.append("=");
                sb.append(printArray(entry.getValue()));
                sb.append("]");
            }
            log.info("REQUEST PARAMETERS: {}", sb.toString());
        }
        return null;
    }

    private String printArray(String[] arr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
}