package com.thinkingme.kylin.bot.support;

import com.thinkingme.kylin.bot.injector.support.*;
import com.thinkingme.kylin.bot.core.component.BotDispatcher;
import com.thinkingme.kylin.bot.core.component.BotFactory;
import com.thinkingme.kylin.bot.core.component.BotInit;
import com.thinkingme.kylin.bot.core.component.SnowFlakeIdGenerator;
import com.thinkingme.kylin.bot.handler.message.GroupMessageEventHandler;
import com.thinkingme.kylin.bot.handler.message.GroupRecallEventHandler;
import com.thinkingme.kylin.bot.handler.message.MemberAddEventHandler;
import com.thinkingme.kylin.bot.handler.message.PrivateMessageEventHandler;
import com.thinkingme.kylin.bot.handler.meta.HeartbeatEventHandler;
import com.thinkingme.kylin.bot.injector.support.friend.FriendInjector;
import com.thinkingme.kylin.bot.injector.support.friend.TempFriendInjector;
import com.thinkingme.kylin.bot.injector.support.group.GroupInjector;
import com.thinkingme.kylin.bot.injector.support.group.MemberInjector;
import com.thinkingme.kylin.bot.injector.support.group.RecallMessageInjector;
import com.thinkingme.kylin.bot.scheduled.FlushCacheScheduled;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author xiaoxu
 * @since 2022-05-24 10:19
 */
public class BotApplicationRegistrar implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return new String[]{
                BotFactory.class.getName(),
                BotDispatcher.class.getName(),
                SnowFlakeIdGenerator.class.getName(),
                HeartbeatEventHandler.class.getName(),
                PrivateMessageEventHandler.class.getName(),
                GroupMessageEventHandler.class.getName(),
                GroupRecallEventHandler.class.getName(),
                MemberAddEventHandler.class.getName(),
                RecallMessageInjector.class.getName(),
                BotInit.class.getName(),
                MessageStringInjector.class.getName(),
                GroupInjector.class.getName(),
                MessageChainInjector.class.getName(),
                TempFriendInjector.class.getName(),
                MemberInjector.class.getName(),
                MessageIdInjector.class.getName(),
                MessageIdIntInjector.class.getName(),
                BotInjector.class.getName(),
                FlushCacheScheduled.class.getName(),
                FriendInjector.class.getName(),
        };
    }

}
