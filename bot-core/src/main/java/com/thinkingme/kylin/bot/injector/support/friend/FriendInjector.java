package com.thinkingme.kylin.bot.injector.support.friend;

import com.thinkingme.kylin.bot.core.Bot;
import com.thinkingme.kylin.bot.core.Friend;
import com.thinkingme.kylin.bot.event.BaseEvent;
import com.thinkingme.kylin.bot.event.message.PrivateMessageEvent;
import com.thinkingme.kylin.bot.injector.ObjectInjector;

/**
 * @author xiaoxu
 * @since 2022-05-24 10:19
 */
public class FriendInjector implements ObjectInjector<Friend> {
    @Override
    public Class<Friend> getClassType() {
        return Friend.class;
    }

    @Override
    public String[] getType() {
        return new String[]{"message"};
    }

    @Override
    public Friend getObject(BaseEvent event, Bot bot) {
        if (event instanceof PrivateMessageEvent) {
            PrivateMessageEvent privateMessageEvent = (PrivateMessageEvent) event;
            return bot.getFriend(privateMessageEvent.getUserId());
        }
        return null;
    }
}
