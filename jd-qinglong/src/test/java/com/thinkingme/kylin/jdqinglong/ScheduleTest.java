package com.thinkingme.kylin.jdqinglong;

import com.thinkingme.kylin.jdqinglong.service.ScheduleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <p>
 *
 * </p>
 *
 * @author: huige
 * @date: 2023/2/1 14:04
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ScheduleTest {

    @Autowired
    ScheduleService scheduleService;
    @Test
    public void testNotifyJDCookieDisable() throws InterruptedException {
        Thread.sleep(5000);
        scheduleService.notifyMaster();
    }
}
