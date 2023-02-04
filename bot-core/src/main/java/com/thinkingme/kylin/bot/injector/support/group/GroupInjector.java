package com.thinkingme.kylin.bot.injector.support.group;

import com.thinkingme.kylin.bot.core.Bot;
import com.thinkingme.kylin.bot.core.Group;
import com.thinkingme.kylin.bot.event.BaseEvent;
import com.thinkingme.kylin.bot.event.message.GroupMessageEvent;
import com.thinkingme.kylin.bot.event.message.GroupRecallEvent;
import com.thinkingme.kylin.bot.event.message.MemberAddEvent;
import com.thinkingme.kylin.bot.injector.ObjectInjector;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xiaoxu
 * @since 2022-05-24 10:19
 */
@Slf4j
public class GroupInjector implements ObjectInjector<Group> {
    @Override
    public Class<Group> getClassType() {
        return Group.class;
    }

    @Override
    public String[] getType() {
        return new String[]{"message", "recallMessage", "memberAddMessage"};
    }

    @Override
    public Group getObject(BaseEvent event, Bot bot) {
        try {
            if (event instanceof GroupMessageEvent) {
                return bot.getGroup(((GroupMessageEvent) event).getGroupId());
            }
            if (event instanceof GroupRecallEvent) {
                return bot.getGroup(((GroupRecallEvent) event).getGroupId());
            }
            if (event instanceof MemberAddEvent) {
                return bot.getGroup(((MemberAddEvent) event).getGroupId());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
