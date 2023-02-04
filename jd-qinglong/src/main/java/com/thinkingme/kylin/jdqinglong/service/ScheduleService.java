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
import javafx.scene.input.DataFormat;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.datetime.joda.LocalDateTimeParser;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
     * 通知京东cookie失效
     */
    @Scheduled(cron = "0 0 10 * * ?")
    @SneakyThrows
    public void notifyJDCookieDisable() {
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
                        if(betweenDays>=3){
                            continue;
                        }
                        JDCookie jdCookie = JDCookie.parse(value);
                        if(status.equals(1)&&remarks!=null){
                            Bot bot = BotFactory.getBots().get(3214890695L);
                            bot.sendPrivateMessage(remarks,new MessageChain(){{
                                add(new TextMessage("账号："+jdCookie.getPtPin()+"过期，请重新获取！"));
                            }});
                        }
                    }
                }
            }
        }
    }


}
