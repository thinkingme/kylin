package com.thinkingme.kylin.jdqinglong.utils;

import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * @author yangxg
 * @date 2021/9/27
 */
public interface WebDriverOpCallBack<T> {
    T doBusiness(RemoteWebDriver webDriver);
}
