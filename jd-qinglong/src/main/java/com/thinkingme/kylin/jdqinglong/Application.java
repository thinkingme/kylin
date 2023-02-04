package com.thinkingme.kylin.jdqinglong;

import com.thinkingme.kylin.bot.EnableBot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@SpringBootApplication
@EnableScheduling
@EnableBot
@ServletComponentScan
@ComponentScan("com.thinkingme.kylin")
public class Application {

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(2);
        scheduler.setThreadNamePrefix("scheduled-task-");
        scheduler.setDaemon(true);
        return scheduler;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
