package com.thinkingme.kylin.bot.api.support;

import com.thinkingme.kylin.bot.api.BaseApi;

/**
 * @author xiaoxu
 * @since 2022-05-24 10:19
 */
public class GetGroups extends BaseApi {

    @Override
    public String getAction() {
        return "get_group_list";
    }

    @Override
    public Object getParams() {
        return null;
    }
}
