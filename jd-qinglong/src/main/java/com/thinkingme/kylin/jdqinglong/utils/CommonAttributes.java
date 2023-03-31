/*
 *
 *
 *
 */
package com.thinkingme.kylin.jdqinglong.utils;

import org.springframework.web.socket.WebSocketSession;

/**
 * 公共参数
 */
public final class CommonAttributes {

    public static final String TMPDIR = System.getProperty("java.io.tmpdir");
    //    public static final String TMPDIR = "/tmp";
    public static final String SESSION_ID = "HTTP_SESSION_ID";
    public static final String JD_LOGIN_TYPE = "JD_LOGIN_TYPE";

    /**
     * chatGpt分段参数
     */



    /**
     * 日期格式配比
     */
    public static final String[] DATE_PATTERNS = new String[]{"yyyy", "yyyy-MM", "yyyyMM", "yyyy/MM", "yyyy-MM-dd", "yyyyMMdd", "yyyy/MM/dd", "yyyy-MM-dd HH:mm:ss", "yyyyMMddHHmmss", "yyyy/MM/dd HH:mm:ss"};
    public static boolean debug = false;
    public static WebSocketSession webSocketSession;
    public static boolean mockCaptcha = "1".equals(System.getenv("mockCaptcha"));

    /**
     * 不可实例化
     */
    private CommonAttributes() {
    }

}