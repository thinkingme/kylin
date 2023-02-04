package com.thinkingme.kylin.jdqinglong.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by yangxg on 2021/9/27
 *
 * @author yangxg
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyChromeClient {
    //用户追踪号，如果是机器人登录，则是qq号，如果是网页登录则是httpsessionid
    private String userTrackId;
    //阿东登录方式
    private LoginType loginType;
    //京东登录方式
    private JDLoginType jdLoginType;
    //用户缓存
    private String trackPhone;
    private long trackQQ;
    private String trackRemark;
    private String trackCK;
    private String chromeSessionId;
    private long expireTime;
    private String wsId;
    //是否已经推送到xdd上了
    private boolean pushedXDD;

    public boolean isExpire() {
        return expireTime < System.currentTimeMillis();
    }

    public long getExpireSeconds() {
        return (expireTime - System.currentTimeMillis()) / 1000;
    }
}
