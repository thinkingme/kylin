package com.thinkingme.kylin.jdqinglong.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by yangxg on 2021/9/10
 *
 * @author yangxg
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignSessionIdStatus {
    private MyChromeClient myChromeClient;
    private boolean isNew;
}
