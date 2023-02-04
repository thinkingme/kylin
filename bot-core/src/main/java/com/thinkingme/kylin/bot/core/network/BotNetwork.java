package com.thinkingme.kylin.bot.core.network;

import com.thinkingme.kylin.bot.config.BotConfig;
import com.thinkingme.kylin.bot.core.component.BotDispatcher;
import com.thinkingme.kylin.bot.core.Bot;

import java.util.Map;

/**
 * @author xiaoxu
 * @since 2022/5/19 16:33
 */
public interface BotNetwork {

    void init(BotConfig botConfig, Map<Long, Bot> bots, BotDispatcher botDispatcher);

}
