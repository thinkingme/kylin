package com.thinkingme.kylin.jdqinglong.aspect;

import com.thinkingme.kylin.bot.core.Contact;
import com.thinkingme.kylin.bot.message.support.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;


/**
 * <p>
 * qq服务的切面，必须加Component
 * 相关文档：https://docs.spring.io/spring-framework/docs/5.3.24/reference/html/core.html#aop
 * </p>
 *
 * @author: huige
 * @date: 2022/12/22 15:59
 */
@Aspect
@Component
@Slf4j
public class QQServiceAspect {

//    @Around("execution(public !static * com.thinkingme.kylin.jdqinglong.service.QQService.*(..)) && args(friend,..)")
//    public Object aroundE(ProceedingJoinPoint pj, Contact friend) throws Exception {
//        Object proceed = null;
//        try {
//            proceed = pj.proceed();
//        } catch (Throwable t){
//            t.printStackTrace();
//            friend.sendMessage(new TextMessage("服务异常！"));
//        }
//        return proceed;
//    }
    @Around("execution(public !static * com.thinkingme.kylin.jdqinglong.service.QQService.test(..))")
    public Object aroundCon(ProceedingJoinPoint pj) throws Exception {
        Object proceed = null;
        try {
            proceed = pj.proceed();
        } catch (Throwable t){
            t.printStackTrace();
//            friend.sendMessage(new TextMessage("服务异常！"));
        }
        return proceed;
    }

}
