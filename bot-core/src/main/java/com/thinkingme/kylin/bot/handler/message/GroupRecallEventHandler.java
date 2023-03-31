package com.thinkingme.kylin.bot.handler.message;

import com.alibaba.fastjson.JSONObject;
import com.thinkingme.kylin.bot.message.Message;
import com.thinkingme.kylin.bot.message.MessageChain;
import com.thinkingme.kylin.bot.annotation.GroupRecallHandler;
import com.thinkingme.kylin.bot.core.Bot;
import com.thinkingme.kylin.bot.core.component.BotFactory;
import com.thinkingme.kylin.bot.event.message.GroupRecallEvent;
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
public class GroupRecallEventHandler implements EventHandler {

    @Override
    public void handle(JSONObject jsonObject, Bot bot) throws InvocationTargetException, IllegalAccessException {
        if (!GroupRecallEvent.isSupport(jsonObject)) {
            return;
        }
        GroupRecallEvent groupRecallEvent = jsonObject.toJavaObject(GroupRecallEvent.class);
        log.debug(groupRecallEvent.toString());
        List<Object> resultList = BotFactory.handleMethod(bot, groupRecallEvent, handlerMethod -> {
            if (!handlerMethod.getMethod().isAnnotationPresent(GroupRecallHandler.class)) {
                return false;
            }
            GroupRecallHandler groupRecallHandler = handlerMethod.getMethod().getAnnotation(GroupRecallHandler.class);
            if (groupRecallHandler.bot() != 0 && groupRecallHandler.bot() != groupRecallEvent.getSelfId()) {
                return false;
            }
            if (groupRecallHandler.groupIds().length > 0 && !ArrayUtils.contain(groupRecallHandler.groupIds(), groupRecallEvent.getGroupId())) {
                return false;
            }
            if (ArrayUtils.contain(groupRecallHandler.excludeGroupIds(), groupRecallEvent.getGroupId())) {
                return false;
            }
            if (groupRecallHandler.senderIds().length > 0 && !ArrayUtils.contain(groupRecallHandler.senderIds(), groupRecallEvent.getOperatorId())) {
                return false;
            }
            if (ArrayUtils.contain(groupRecallHandler.excludeSenderIds(), groupRecallEvent.getOperatorId())) {
                return false;
            }
            return true;
        }, "recallMessage");
        for (Object result : resultList) {
            try {
                if (result instanceof Message) {
                    bot.getGroup(groupRecallEvent.getGroupId()).sendMessage((Message) result);
                }
                if (result instanceof MessageChain) {
                    bot.getGroup(groupRecallEvent.getGroupId()).sendMessage((MessageChain) result);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

}
