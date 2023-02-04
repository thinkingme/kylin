package com.thinkingme.kylin.bot.injector.support.group;

import com.thinkingme.kylin.bot.core.Bot;
import com.thinkingme.kylin.bot.event.BaseEvent;
import com.thinkingme.kylin.bot.event.message.GroupRecallEvent;
import com.thinkingme.kylin.bot.injector.ObjectInjector;
import com.thinkingme.kylin.bot.injector.object.RecallMessage;

/**
 * @author xiaoxu
 * @since 2022-05-24 10:19
 */
public class RecallMessageInjector implements ObjectInjector<RecallMessage> {
    @Override
    public Class<RecallMessage> getClassType() {
        return RecallMessage.class;
    }

    @Override
    public String[] getType() {
        return new String[]{"recallMessage"};
    }

    @Override
    public RecallMessage getObject(BaseEvent event, Bot bot) {
        if (event instanceof GroupRecallEvent) {
            GroupRecallEvent groupRecallEvent = (GroupRecallEvent) event;
            RecallMessage recallMessage = new RecallMessage();
            recallMessage.setSenderId(groupRecallEvent.getUserId());
            recallMessage.setOperatorId(groupRecallEvent.getOperatorId());
            return recallMessage;
        }
        return null;
    }
}
