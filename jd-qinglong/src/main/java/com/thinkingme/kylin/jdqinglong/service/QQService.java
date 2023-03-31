package com.thinkingme.kylin.jdqinglong.service;

import com.alibaba.fastjson.JSONObject;
import com.thinkingme.kylin.bot.annotation.FriendMessageHandler;
import com.thinkingme.kylin.bot.annotation.TempMessageHandler;
import com.thinkingme.kylin.bot.core.Friend;
import com.thinkingme.kylin.bot.core.TempFriend;
import com.thinkingme.kylin.bot.message.MessageChain;
import com.thinkingme.kylin.bot.message.support.TextMessage;
import com.thinkingme.kylin.jdqinglong.bean.JDCookie;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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


    @FriendMessageHandler
    @SneakyThrows
    public void friendMessageHandler(Friend friend, MessageChain messageChain, String message,Integer id){
        log.info("收到好友消息【{}】:【{}】",friend.getNickname(),message);
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
            chatGptService.sendMessage(message,friend);
//            friend.sendMessage(new TextMessage("你干嘛~ 哎呦！"));
        }
    }

    public String test() throws Exception {
        throw new Exception("5456");
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
}
