package com.thinkingme.kylin.jdqinglong.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.thinkingme.kylin.bot.core.Bot;
import com.thinkingme.kylin.bot.core.component.BotFactory;
import com.thinkingme.kylin.bot.message.MessageChain;
import com.thinkingme.kylin.bot.message.support.TextMessage;
import com.thinkingme.kylin.jdqinglong.bean.JDCookie;
import com.thinkingme.kylin.jdqinglong.bean.QLConfig;
import com.thinkingme.kylin.jdqinglong.bean.QLToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

/**
 * Created by yangxg on 2021/10/12
 *
 * @author yangxg
 */
@Slf4j
@Service
public class ScheduleService {

    @Autowired
    List<QLConfig> qlConfigs;
    @Autowired
    QingLongService qingLongService;
    @Autowired
    EmailService emailService;

    @Autowired
    JDService jdService;

    @Value("${spring.mail.username}")
    private String to;


    public static void sleepRandom(){
        int nextInt = new Random().nextInt(600);
        try {
            log.info("睡眠{}秒",nextInt);
            Thread.sleep(nextInt*1000);
        } catch (InterruptedException e) {
            log.error("",e);
        }

    }

    @Scheduled(cron = "0 0 9 * * ?")
    @PostConstruct
    public void refreshOpenIdToken() {
        if (qlConfigs != null) {
            for (QLConfig qlConfig : qlConfigs) {
                QLToken qlTokenOld = qlConfig.getQlToken();
                qingLongService.getToken(qlConfig);
                log.info(qlConfig.getQlToken() + " token 从" + qlTokenOld + " 变为 " + qlConfig.getQlToken());
            }
        }
    }

    /**
     * 通知管理员
     */
    @Scheduled(cron = "0 0 9/20 * * ?")
    public void notifyMaster() {
        log.info("开始检查qq机器人是否在线");
        Bot bot = BotFactory.getBots().get(3214890695L);
        try {
            bot.flushFriends();
        } catch (Exception e) {
            //通过邮件通知管理员
            emailService.sendMail(to,"刷新好友列表失败！","请检查服务是否正常！");
            log.error("发送qq消息失败",e);
        }
    }

    /**
     * 通知京东cookie失效
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void notifyJDCookieDisable() {
        log.info("开始检查失效的京东cookie");
        sleepRandom();
        notifyJDCookieDisable(true);
    }

    /**
     * 通知活动完成情况
     * 1.京东农场
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void notifyJDActivity() {
        log.info("开始查询京东活动");
        sleepRandom();
        if (qlConfigs != null) {
            for (QLConfig qlConfig : qlConfigs) {
                JSONArray jd_cookie = qingLongService.getQingLongEnv(qlConfig, "JD_COOKIE");
                if (jd_cookie != null && jd_cookie.size() > 0) {
                    for (int i = 0; i < jd_cookie.size(); i++) {
                        JSONObject jsonObject = jd_cookie.getJSONObject(i);
                        Integer status = jsonObject.getInteger("status");
                        Long remarks = jsonObject.getLong("remarks");
                        String value = jsonObject.getString("value");
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        LocalDateTime updatedAt = LocalDateTime.parse(jsonObject.getString("updatedAt"),dtf);
                        long betweenDays = Duration.between(updatedAt,LocalDateTime.now()).toDays();

                        JDCookie jdCookie = null;
                        try {
                            jdCookie = JDCookie.parse(value);
                        } catch (Exception e) {
                            log.error("jdcookie解析失败",e);
                            continue;
                        }

                        if(status.equals(0)&&remarks!=null){
                            Bot bot = BotFactory.getBots().get(3214890695L);
                            String jdFruitInfo = null;
                            try {
                                jdFruitInfo = jdService.getJdFruitInfo(value);

                            } catch (IOException e) {
                                log.error("获取京东农场信息失败",e);
                            }
                            if("兑换".contains(jdFruitInfo)){
                                int message = 0;
                                try {
                                    String finalJdFruitInfo = jdFruitInfo;
                                    message = bot.sendPrivateMessage(remarks, new MessageChain() {{
                                        add(new TextMessage(finalJdFruitInfo));
                                    }});
                                } catch (Exception e) {
                                    log.error("发送私聊消息失败，账号: "+remarks,e);

                                }
                                if(message==0){
                                    try {
                                        String finalJdFruitInfo1 = jdFruitInfo;
                                        message = bot.sendTempMessage(remarks,764036808, new MessageChain() {{
                                            add(new TextMessage(finalJdFruitInfo1));
                                        }});
                                    } catch (Exception e) {
                                        log.error("发送临时消息失败，账号: "+remarks,e);

                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
    }


    public void notifyJDCookieDisable(boolean checkDay) {
        log.info("开始检查失效的京东cookie");
        if (qlConfigs != null) {
            for (QLConfig qlConfig : qlConfigs) {
                JSONArray jd_cookie = qingLongService.getQingLongEnv(qlConfig, "JD_COOKIE");
                if (jd_cookie != null && jd_cookie.size() > 0) {
                    for (int i = 0; i < jd_cookie.size(); i++) {
                        JSONObject jsonObject = jd_cookie.getJSONObject(i);
                        Integer status = jsonObject.getInteger("status");
                        Long remarks = jsonObject.getLong("remarks");
                        String value = jsonObject.getString("value");
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        LocalDateTime updatedAt = LocalDateTime.parse(jsonObject.getString("updatedAt"),dtf);
                        long betweenDays = Duration.between(updatedAt,LocalDateTime.now()).toDays();
                        if(checkDay && betweenDays>=3){
                            continue;
                        }

                        JDCookie jdCookie = null;
                        try {
                            jdCookie = JDCookie.parse(value);
                        } catch (Exception e) {
                            log.error("jdcookie解析失败",e);
                            continue;
                        }

                        if(status.equals(1)&&remarks!=null){
                            Bot bot = BotFactory.getBots().get(3214890695L);
                            JDCookie finalJdCookie = jdCookie;
                            int message = 0;
                            try {
                                message = bot.sendPrivateMessage(remarks, new MessageChain() {{
                                    add(new TextMessage("账号：" + finalJdCookie.getPtPin() + "过期，请重新获取！"));
                                }});
                            } catch (Exception e) {
                                log.error("发送私聊消息失败，账号: "+remarks,e);

                            }
                            if(message==0){
                                try {
                                    message = bot.sendTempMessage(remarks,764036808, new MessageChain() {{
                                        add(new TextMessage("账号：" + finalJdCookie.getPtPin() + "过期，请重新获取！"));
                                    }});
                                } catch (Exception e) {
                                    log.error("发送临时消息失败，账号: "+remarks,e);

                                }
                            }

                        }
                    }
                }
            }
        }
    }


}
