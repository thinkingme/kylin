package com.thinkingme.kylin.jdqinglong.filters;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.regex.Pattern;

/**
 * a servletRequest wrapper to resolve xss attack
 * @author      : lincy
 * @date        : 2021/3/31 10:14
 **/
public class XsslHttpServletRequestWrapper extends HttpServletRequestWrapper {

    HttpServletRequest xssRequest = null;
    private static final String xssField = "from";

    public XsslHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        xssRequest = request;
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        //处理参数中有from的情况，如果以后要过滤更多字段可以弄个数组来处理
        if(xssField.equals(name.trim())){
            if (value != null) {
                value = replaceXSS(value);
            }
        }
        return value;
    }

    /**
     * 去除待带script、src的语句，转义替换后的value值
     */
    public static String replaceXSS(String value) {
        if (value != null) {
            try{
                value = value.replace("+","%2B");   //'+' replace to '%2B'
                value = URLDecoder.decode(value, "utf-8");
            }catch(UnsupportedEncodingException e){
            }catch(IllegalArgumentException e){
            }

            // Avoid null characters
            value = value.replaceAll("\0", "");

            // Avoid anything between script tags
            Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid anything in a src='...' type of e­xpression
            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // Remove any lonesome </script> tag
            scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            // Remove any lonesome <script ...> tag
            scriptPattern = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid eval(...) e­xpressions
            scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid e­xpression(...) e­xpressions
            scriptPattern = Pattern.compile("e­xpression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid javascript:... e­xpressions
            scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");
            // Avoid alert:... e­xpressions
            scriptPattern = Pattern.compile("alert", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");
            // Avoid οnlοad= e­xpressions
            scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
            scriptPattern = Pattern.compile("vbscript[\r\n| | ]*:[\r\n| | ]*", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");
        }
        return filter(value);
    }

    /**
     * 过滤特殊字符
     */
    public static String filter(String value) {
        if (value == null) {
            return null;
        }
        StringBuffer result = new StringBuffer(value.length());
        for (int i=0; i<value.length(); ++i) {
            switch (value.charAt(i)) {
                case '<':
                    result.append(encode("<"));
                    break;
                case '>':
                    result.append(encode(">"));
                    break;
                case '"':
                    result.append(encode("\""));
                    break;
                case '\'':
                    result.append(encode("'"));
                    break;
                case '%':
                    result.append(encode("%"));
                    break;
                case ';':
                    result.append(encode(";"));
                    break;
                case '(':
                    result.append(encode("("));
                    break;
                case ')':
                    result.append(encode(")"));
                    break;
                case '&':
                    result.append(encode("&"));
                    break;
                case '+':
                    result.append(encode("+"));
                    break;
                default:
                    result.append(value.charAt(i));
                    break;
            }
        }
        return result.toString();
    }

    private static String encode(String str){
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }
}
