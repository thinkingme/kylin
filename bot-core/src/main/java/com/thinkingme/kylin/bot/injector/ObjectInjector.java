package com.thinkingme.kylin.bot.injector;

import com.thinkingme.kylin.bot.core.Bot;
import com.thinkingme.kylin.bot.event.BaseEvent;

/**
 * @author xiaoxu
 * @since 2022-05-24 10:19
 */
public interface ObjectInjector<T> {

    String[] getType();

    Class<T> getClassType();

    T getObject(BaseEvent event, Bot bot);

}