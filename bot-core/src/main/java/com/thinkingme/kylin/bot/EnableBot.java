package com.thinkingme.kylin.bot;

import com.thinkingme.kylin.bot.support.BotApplicationRegistrar;
import com.thinkingme.kylin.bot.support.BotAutoConfigRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author xiaoxu
 * @since 2020-08-07 10:43
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({BotApplicationRegistrar.class, BotAutoConfigRegistrar.class})
public @interface EnableBot {
}
