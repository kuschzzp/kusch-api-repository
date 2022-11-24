package com.kusch.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.protocol.HttpContext;

/**
 * 由于302会spring会自动重定向，所以需要将原始地址存一下以便后面获取
 * 重定向策略，将重定向的地址 用 RedirectUri 为key加入到响应头中
 *
 * @author Mr.kusch
 * @date 2022/11/23 15:47
 */
public class GetUriRedirectStrategy extends DefaultRedirectStrategy {

    public final static String REDIRECT_URI = "RedirectUri";

    @Override
    public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
        boolean redirected = super.isRedirected(request, response, context);
        Header[] allHeaders = response.getAllHeaders();
        for (Header header : allHeaders) {
            if (StringUtils.equals(header.getName(), "Location")) {
                context.setAttribute(REDIRECT_URI, header.getValue());
            }
        }
        Object uri = context.getAttribute(REDIRECT_URI);
        response.setHeader(REDIRECT_URI, String.valueOf(uri));
        return redirected;
    }
}
