package com.thinkingme.kylin.bot.injector.support;

import com.thinkingme.kylin.bot.core.Bot;
import com.thinkingme.kylin.bot.event.BaseEvent;
import com.thinkingme.kylin.bot.event.message.GroupRecallEvent;
import com.thinkingme.kylin.bot.event.message.MessageEvent;
import com.thinkingme.kylin.bot.injector.ObjectInjector;

/**
 * @author xiaoxu
 * @since 2022-05-24 10:19
 */
public class MessageIdIntInjector implements ObjectInjector<Integer> {
    @Override
    public Class<Integer> getClassType() {
        return int.class;
    }

    @Override
    public String[] getType() {
        return new String[]{"message", "recallMessage"};
    }

    @Override
    public Integer getObject(BaseEvent event, Bot bot) {
        if (event instanceof MessageEvent) {
            return ((MessageEvent) event).getMessageId();
        }
        if (event instanceof GroupRecallEvent) {
            return ((GroupRecallEvent) event).getMessageId();
        }
        return null;
    }
}
