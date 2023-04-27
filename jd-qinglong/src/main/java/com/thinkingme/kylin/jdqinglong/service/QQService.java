package com.thinkingme.kylin.jdqinglong.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.thinkingme.kylin.bot.annotation.FriendMessageHandler;
import com.thinkingme.kylin.bot.annotation.GroupMessageHandler;
import com.thinkingme.kylin.bot.annotation.TempMessageHandler;
import com.thinkingme.kylin.bot.core.Friend;
import com.thinkingme.kylin.bot.core.Group;
import com.thinkingme.kylin.bot.core.Member;
import com.thinkingme.kylin.bot.core.TempFriend;
import com.thinkingme.kylin.bot.message.MessageChain;
import com.thinkingme.kylin.bot.message.support.TextMessage;
import com.thinkingme.kylin.jdqinglong.bean.JDCookie;
import com.thinkingme.kylin.jdqinglong.bean.QLConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 *
 * </p>
 *
 * @author: huige
 * @date: 2022/12/16 11:12
 */
@Service
@Slf4j
public class QQService {

    @Autowired
    QingLongService qingLongService;
    @Autowired
    ScheduleService scheduleService;
    @Autowired
    ChatGptService chatGptService;
    @Autowired
    List<QLConfig> qlConfigs;
    @Autowired
    JDService jdService;

    private static Map<Long, String> qq2userName = new HashMap<Long,String>();


    @FriendMessageHandler
    @SneakyThrows
    public void friendMessageHandler(Friend friend, MessageChain messageChain, String message,Integer id){
        log.info("收到好友消息【{}】:【{}】",friend.getNickname(),message);
        JSONObject jsonObject = null;
        if(message.equals("查询农场")){
            if(qlConfigs!=null){
                for (QLConfig qlConfig : qlConfigs) {
                    JSONArray qingLongEnv = qingLongService.getQingLongEnv(qlConfig, String.valueOf(friend.getUserId()));
                    if (qingLongEnv != null && qingLongEnv.size() > 0) {
                        for (int i = 0; i < qingLongEnv.size(); i++) {
                            JSONObject jsonObject1 = qingLongEnv.getJSONObject(i);
                            Integer status = jsonObject1.getInteger("status");
                            String value = jsonObject1.getString("value");
                            if(status.equals(0)){
                                String jdFruit = jdService.getJdFruitInfo(value);
                                friend.sendMessage(new TextMessage(jdFruit));
                            } else if (status.equals(1)) {
                                JDCookie jdCookie = null;
                                try {
                                    jdCookie = JDCookie.parse(value);
                                } catch (Exception e) {
                                    log.error("jdcookie解析失败",e);
                                    continue;
                                }
                                JDCookie finalJdCookie = jdCookie;
                                friend.sendMessage(new TextMessage("账号：" + finalJdCookie.getPtPin() + "过期，请重新获取！"));
                            }

                        }
                    }else if(qingLongEnv != null){
                        friend.sendMessage(new TextMessage("请先上传cookie！"));
                    }
                }
            }
        }else if (message.equals("检查")){
          scheduleService.notifyJDCookieDisable(false);
          friend.sendMessage(new TextMessage("检查完毕！"));
        } else if(message.contains(JDCookie.KEY)&&message.contains(JDCookie.PIN)){
            try {
                jsonObject = qingLongService.uploadQingLong(String.valueOf(friend.getUserId()), message);
            }catch (Exception e){
                friend.sendMessage(new TextMessage("服务异常！"));
                log.error(e.getMessage(), e);
                return;
            }
            if(jsonObject.getInteger("status")>0){
                friend.sendMessage(new TextMessage("上传成功！"));
            }else{
                friend.sendMessage(new TextMessage("上传失败！"));
            }
        }else{
//            chatGptService.sendMessage(message,friend);
            friend.sendMessage(new TextMessage("你干嘛~ 哎呦！"));
        }
    }

    @TempMessageHandler
    @SneakyThrows
    public void tempFriendMessageHandler(TempFriend friend, MessageChain messageChain, String message, Integer id){
        log.info("收到临时会话消息【{}】:【{}】",friend.getUserId(),message);
        JSONObject jsonObject = null;
        if(message.contains(JDCookie.KEY)&&message.contains(JDCookie.PIN)){
            try {
                jsonObject = qingLongService.uploadQingLong(String.valueOf(friend.getUserId()), message);
            }catch (Exception e){
                friend.sendMessage(new TextMessage("服务异常！"));
                log.error(e.getMessage(), e);
                return;
            }
            if(jsonObject.getInteger("status")>0){
                friend.sendMessage(new TextMessage("上传成功！"));
            }else{
                friend.sendMessage(new TextMessage("上传失败！"));
            }
        }else{
            friend.sendMessage(new TextMessage("你干嘛~ 哎呦！"));
        }
    }
    @GroupMessageHandler
    @SneakyThrows
    public void groupMessageHandler(Group group, Member member, MessageChain messageChain, String message, Integer id){
        log.info("收到群消息【{}】:【{}】",member.getUserId(),message);
        JSONObject jsonObject = null;

    }



}
