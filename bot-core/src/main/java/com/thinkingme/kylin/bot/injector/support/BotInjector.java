package com.thinkingme.kylin.bot.injector.support;

import com.thinkingme.kylin.bot.core.Bot;
import com.thinkingme.kylin.bot.event.BaseEvent;
import com.thinkingme.kylin.bot.injector.ObjectInjector;

/**
 * @author xiaoxu
 * @since 2022-05-24 10:19
 */
public class BotInjector implements ObjectInjector<Bot> {
    @Override
    public Class<Bot> getClassType() {
        return Bot.class;
    }

    @Override
    public String[] getType() {
        return new String[]{"all"};
    }

    @Override
    public Bot getObject(BaseEvent event, Bot bot) {
        return bot;
    }
}
