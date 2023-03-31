package com.thinkingme.kylin.jdqinglong.controller;

import com.thinkingme.kylin.jdqinglong.service.ParentChildTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 父子容器测试
 * </p>
 *
 * @author: huige
 * @date: 2023/3/9 9:31
 */
@RestController
public class ParentChildTestController {

    @Autowired
    ParentChildTestService parentChildTestService;

//    @RequestMapping("/pc")
//    public String pc(HttpServletRequest request){
//        request.getSession();
//        parentChildTestService.parentChildTest();
//        return "111";
//    }

}
