package com.thinkingme.kylin.bot.handler.message;

import com.alibaba.fastjson.JSONObject;
import com.thinkingme.kylin.bot.message.Message;
import com.thinkingme.kylin.bot.message.MessageChain;
import com.thinkingme.kylin.bot.annotation.MemberAddHandler;
import com.thinkingme.kylin.bot.core.Bot;
import com.thinkingme.kylin.bot.core.component.BotFactory;
import com.thinkingme.kylin.bot.event.message.MemberAddEvent;
import com.thinkingme.kylin.bot.handler.EventHandler;
import com.thinkingme.kylin.bot.util.ArrayUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author xiaoxu
 * @since 2022-05-24 10:19
 */
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MemberAddEventHandler implements EventHandler {

    @Override
    public void handle(JSONObject jsonObject, Bot bot) throws InvocationTargetException, IllegalAccessException {
        if (!MemberAddEvent.isSupport(jsonObject)) {
            return;
        }
        MemberAddEvent memberAddEvent = jsonObject.toJavaObject(MemberAddEvent.class);
        log.debug(memberAddEvent.toString());
        List<Object> resultList = BotFactory.handleMethod(bot, memberAddEvent, handlerMethod -> {
            if (!handlerMethod.getMethod().isAnnotationPresent(MemberAddHandler.class)) {
                return false;
            }
            MemberAddHandler memberAddHandler = handlerMethod.getMethod().getAnnotation(MemberAddHandler.class);
            if (memberAddHandler.bot() != 0 && memberAddHandler.bot() != memberAddEvent.getSelfId()) {
                return false;
            }
            if (memberAddHandler.groupIds().length > 0 && !ArrayUtils.contain(memberAddHandler.groupIds(), memberAddEvent.getGroupId())) {
                return false;
            }
            if (ArrayUtils.contain(memberAddHandler.excludeGroupIds(), memberAddEvent.getGroupId())) {
                return false;
            }
            return true;
        }, "memberAddMessage");
        for (Object result : resultList) {
            try {
                if (result instanceof Message) {
                    bot.getGroup(memberAddEvent.getGroupId()).sendMessage((Message) result);
                }
                if (result instanceof MessageChain) {
                    bot.getGroup(memberAddEvent.getGroupId()).sendMessage((MessageChain) result);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

}
