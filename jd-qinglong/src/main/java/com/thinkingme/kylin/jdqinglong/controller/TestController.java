package com.thinkingme.kylin.jdqinglong.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *
 * </p>
 *
 * @author: huige
 * @date: 2023/1/12 10:32
 */
@RestController
public class TestController {

    @RequestMapping("/test")
    public String testCon(HttpServletRequest request){
        request.getSession();
        return "111";
    }
}
