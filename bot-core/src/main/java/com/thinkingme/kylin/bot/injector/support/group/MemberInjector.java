package com.thinkingme.kylin.bot.injector.support.group;

import com.thinkingme.kylin.bot.core.Bot;
import com.thinkingme.kylin.bot.core.Member;
import com.thinkingme.kylin.bot.event.BaseEvent;
import com.thinkingme.kylin.bot.event.message.GroupMessageEvent;
import com.thinkingme.kylin.bot.event.message.MemberAddEvent;
import com.thinkingme.kylin.bot.injector.ObjectInjector;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xiaoxu
 * @since 2022-05-24 10:19
 */
@Slf4j
public class MemberInjector implements ObjectInjector<Member> {
    @Override
    public Class<Member> getClassType() {
        return Member.class;
    }

    @Override
    public String[] getType() {
        return new String[]{"message", "memberAddMessage"};
    }

    @Override
    public Member getObject(BaseEvent event, Bot bot) {
        try {
            if (event instanceof GroupMessageEvent) {
                return bot.getMember(((GroupMessageEvent) event).getGroupId(), ((GroupMessageEvent) event).getUserId());
            }
            if (event instanceof MemberAddEvent) {
                return bot.getMember(((MemberAddEvent) event).getGroupId(), ((MemberAddEvent) event).getUserId());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
