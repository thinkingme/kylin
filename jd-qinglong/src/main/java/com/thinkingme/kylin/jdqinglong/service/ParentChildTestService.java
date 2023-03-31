package com.thinkingme.kylin.jdqinglong.service;

import com.thinkingme.kylin.jdqinglong.controller.TestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *
 * </p>
 *
 * @author: huige
 * @date: 2023/3/9 9:32
 */
@Service
public class ParentChildTestService {

    @Autowired
    TestController testController;

    public void parentChildTest(){
    }

}
