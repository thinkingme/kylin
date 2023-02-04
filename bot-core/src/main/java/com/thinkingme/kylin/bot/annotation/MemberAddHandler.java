package com.thinkingme.kylin.bot.annotation;

import java.lang.annotation.*;

/**
 * @author xiaoxu
 * @since 2021/8/9 11:15
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MemberAddHandler {

    /**
     * 限制bot 参数为bot qq  0为不限制
     */
    long bot() default 0;

    /**
     * 限制某个群
     */
    long[] groupIds() default {};

    /**
     * 排除某个群
     */
    long[] excludeGroupIds() default {};

}
