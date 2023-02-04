package com.thinkingme.kylin.jdqinglong.bean;

import com.thinkingme.kylin.jdqinglong.exception.JDParseException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

/**
 * @author yangxg
 * @date 2021/9/18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JDCookie {


    private String ptPin;
    private String ptKey;

    public static final String KEY = "pt_key";
    public static final String PIN = "pt_pin";

    public static JDCookie parse(String ck) throws Exception {
        JDCookie jdCookie = new JDCookie();
        String[] split = ck.split(";");
        for (String s : split) {
            if (s.contains(KEY)) {
                String[] split1 = s.split("=");
                if(split1.length!=2){
                    throw new JDParseException("cookie格式错误！");
                }
                jdCookie.setPtKey(split1[1].trim());
            }
            if (s.contains(PIN)) {
                String[] split1 = s.split("=");
                if(split1.length!=2){
                    throw new JDParseException("cookie格式错误！");
                }
                jdCookie.setPtPin(split1[1].trim());
            }
        }
        return jdCookie;
    }

    @Override
    public String toString() {
        return "pt_key=" + ptKey + ";pt_pin=" + ptPin + ";";
    }

    public boolean isEmpty() {
        return StringUtils.isEmpty(ptPin) && StringUtils.isEmpty(ptKey);
    }
}
