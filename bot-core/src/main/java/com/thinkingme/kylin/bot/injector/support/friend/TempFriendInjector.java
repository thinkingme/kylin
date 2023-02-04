package com.thinkingme.kylin.bot.injector.support.friend;

import com.thinkingme.kylin.bot.core.Bot;
import com.thinkingme.kylin.bot.core.TempFriend;
import com.thinkingme.kylin.bot.event.BaseEvent;
import com.thinkingme.kylin.bot.event.message.PrivateMessageEvent;
import com.thinkingme.kylin.bot.injector.ObjectInjector;

/**
 * @author xiaoxu
 * @since 2022-05-24 10:19
 */
public class TempFriendInjector implements ObjectInjector<TempFriend> {
    @Override
    public Class<TempFriend> getClassType() {
        return TempFriend.class;
    }

    @Override
    public String[] getType() {
        return new String[]{"message"};
    }

    @Override
    public TempFriend getObject(BaseEvent event, Bot bot) {
        if (event instanceof PrivateMessageEvent) {
            PrivateMessageEvent privateMessageEvent = (PrivateMessageEvent) event;
            if ("group".equals(privateMessageEvent.getSubType())) {
                return new TempFriend(privateMessageEvent.getUserId(), bot);
            }
        }
        return null;
    }
}
