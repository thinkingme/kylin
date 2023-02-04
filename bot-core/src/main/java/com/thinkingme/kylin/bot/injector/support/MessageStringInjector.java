package com.thinkingme.kylin.bot.injector.support;

import com.thinkingme.kylin.bot.core.Bot;
import com.thinkingme.kylin.bot.event.BaseEvent;
import com.thinkingme.kylin.bot.event.message.GroupMessageEvent;
import com.thinkingme.kylin.bot.event.message.GroupRecallEvent;
import com.thinkingme.kylin.bot.event.message.PrivateMessageEvent;
import com.thinkingme.kylin.bot.injector.ObjectInjector;
import com.thinkingme.kylin.bot.message.CacheMessage;
import com.thinkingme.kylin.bot.message.MessageChain;
import com.thinkingme.kylin.bot.message.MessageTypeHandle;

import java.util.List;

/**
 * @author xiaoxu
 * @since 2022-05-24 10:19
 */
public class MessageStringInjector implements ObjectInjector<String> {
    @Override
    public Class<String> getClassType() {
        return String.class;
    }

    @Override
    public String[] getType() {
        return new String[]{"message"};
    }

    @Override
    public String getObject(BaseEvent event, Bot bot) {
        MessageChain messageChain = null;
        if (event instanceof GroupMessageEvent) {
            messageChain = new MessageChain();
            GroupMessageEvent groupMessageEvent = (GroupMessageEvent) event;
            for (int i = 0; i < groupMessageEvent.getMessage().size(); i++) {
                messageChain.add(MessageTypeHandle.getMessage(groupMessageEvent.getMessage().getJSONObject(i)));
            }
        }
        if (event instanceof PrivateMessageEvent) {
            PrivateMessageEvent privateMessageEvent = (PrivateMessageEvent) event;
            messageChain = new MessageChain();
            for (int i = 0; i < privateMessageEvent.getMessage().size(); i++) {
                messageChain.add(MessageTypeHandle.getMessage(privateMessageEvent.getMessage().getJSONObject(i)));
            }
        }
        if (event instanceof GroupRecallEvent) {
            GroupRecallEvent groupRecallEvent = (GroupRecallEvent) event;
            List<CacheMessage> groupCacheMessageChain = bot.getGroupCacheMessageChain(groupRecallEvent.getGroupId(), groupRecallEvent.getMessageId(), 1);
            if (groupCacheMessageChain.isEmpty()) {
                return null;
            }
            messageChain = groupCacheMessageChain.get(0).getMessageChain();
        }
        return messageChain == null ? null : messageChain.toString();
    }
}
