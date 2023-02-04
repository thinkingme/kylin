package com.thinkingme.kylin.bot.core.network;

import com.thinkingme.kylin.bot.api.ApiResult;
import com.thinkingme.kylin.bot.api.BaseApi;
import com.thinkingme.kylin.bot.core.Bot;

/**
 * @author xiaoxu
 * @since 2022/5/19 10:59
 */
public interface BotClient {

    ApiResult invokeApi(BaseApi baseApi, Bot bot);

    void heartbeat();

}
