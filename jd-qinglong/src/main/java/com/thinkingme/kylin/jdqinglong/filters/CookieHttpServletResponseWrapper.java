package com.thinkingme.kylin.jdqinglong.filters;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * <p>
 *  这方案行不通原本可以重写addHeader方法来修改写入的JSESSIONID
 *  但是发现过滤的是一个门面模式的Requset
 *  然后JSESSIONID调用的是门面里面那个的addHeader方法，所以覆盖不进去
 * </p>
 *
 * @author: huige
 * @date: 2023/1/12 14:40
 */
public class CookieHttpServletResponseWrapper extends HttpServletResponseWrapper {
    private final String SAME_SITE_ATTRIBUTE_VALUES = "SameSite=Lax";
    public CookieHttpServletResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public void addHeader(String name, String value) {
        if(SAME_SITE_ATTRIBUTE_VALUES.equals(name)){

        }
        super.addHeader(name, value);
    }
}
