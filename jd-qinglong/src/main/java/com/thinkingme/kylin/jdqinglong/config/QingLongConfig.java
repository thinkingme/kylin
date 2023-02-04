package com.thinkingme.kylin.jdqinglong.config;

import com.thinkingme.kylin.jdqinglong.bean.QLConfig;
import com.thinkingme.kylin.jdqinglong.utils.CommonAttributes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * <p>
 * 青龙的配置
 * </p>
 *
 * @author: huige
 * @date: 2022/12/21 17:15
 */
@Configuration
@Slf4j
public class QingLongConfig {

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Autowired
    ResourceLoader resourceLoader;
    @Value("${env.path}")
    private String envPath;

    private Properties properties = new Properties();


    @Bean
    public List<QLConfig>  parseConfig() {
        List<QLConfig> qlConfigs = new ArrayList<>();
        if (envPath.startsWith(ResourceLoader.CLASSPATH_URL_PREFIX)) {
            Resource resource = resourceLoader.getResource(envPath);
            try (InputStreamReader inputStreamReader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
                properties.load(inputStreamReader);
            } catch (IOException e) {
                throw new RuntimeException("env.properties配置有误");
            }
        } else {
            File envFile = new File(envPath);
            if (!envFile.exists()) {
                throw new RuntimeException("env.properties配置有误");
            }
            try (BufferedReader br = new BufferedReader(new FileReader(envFile))) {
                properties.load(br);
            } catch (IOException e) {
                throw new RuntimeException("env.properties配置有误");
            }
        }
        CommonAttributes.debug = Boolean.parseBoolean(properties.getProperty("jd.debug", "false"));

        for (int i = 1; i <= 5; i++) {
            QLConfig config = new QLConfig();
            config.setId(i);
            for (String key : properties.stringPropertyNames()) {
                String value = properties.getProperty(key);
                if (key.equals("QL_USERNAME_" + i)) {
                    config.setQlUsername(value);
                } else if (key.equals("QL_URL_" + i)) {
                    if (value.endsWith("/")) {
                        value = value.substring(0, value.length() - 1);
                    }
                    config.setQlUrl(value);
                } else if (key.equals("QL_PASSWORD_" + i)) {
                    config.setQlPassword(value);
                } else if (key.equals("QL_CLIENTID_" + i)) {
                    config.setQlClientID(value);
                } else if (key.equals("QL_SECRET_" + i)) {
                    config.setQlClientSecret(value);
                } else if (key.equals("QL_LABEL_" + i)) {
                    config.setLabel(value);
                } else if (key.equals("QL_CAPACITY_" + i)) {
                    config.setCapacity(Integer.parseInt(value));
                }
            }
            if (config.isValid()) {
                qlConfigs.add(config);
            }
        }
        log.info("解析" + qlConfigs.size() + "套配置");
        return qlConfigs;
    }
}
