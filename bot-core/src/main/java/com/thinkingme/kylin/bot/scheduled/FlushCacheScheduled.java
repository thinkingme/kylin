package com.thinkingme.kylin.bot.scheduled;

import com.thinkingme.kylin.bot.core.Bot;
import com.thinkingme.kylin.bot.core.Group;
import com.thinkingme.kylin.bot.core.component.BotFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Collection;

/**
 * @author xiaoxu
 * @since 2021/9/3 9:54 上午
 */
@Slf4j
public class FlushCacheScheduled {

    @Scheduled(fixedRate = 10 * 60 * 1000)
    public synchronized void flush() {
        try {
            this.flushFriends();
            this.flushGroups();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    public synchronized void flushFriends() {
        Collection<Bot> bots = BotFactory.getBots().values();
        if (bots.isEmpty()) {
            return;
        }
        for (Bot bot : bots) {
            bot.flushFriends();
        }
    }

    public synchronized void flushGroups() {
        Collection<Bot> bots = BotFactory.getBots().values();
        if (bots.isEmpty()) {
            return;
        }
        for (Bot bot : bots) {
            for (Group group : bot.flushGroups()) {
                bot.flushGroupMembers(group);
            }
        }
    }


}
