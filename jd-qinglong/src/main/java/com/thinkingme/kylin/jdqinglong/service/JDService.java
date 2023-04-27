package com.thinkingme.kylin.jdqinglong.service;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *
 * </p>
 *
 * @author: huige
 * @date: 2023/4/21 13:24
 */
@Service
@Slf4j
public class JDService {

    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS).build();


    public String getJdFruitInfo(String cookie) throws IOException {
        String massage="京东农场未开启！";

        String s = jdFruitRequest(cookie);

        JSONObject jsonObject = JSONObject.parseObject(s);

        String code = jsonObject.getString("code");
        if("400".equals(code)){
            return "查询异常！";
        }
        String fruit = getJdFruit(cookie);
        JSONObject fruitObject = JSONObject.parseObject(fruit).getJSONObject("farmUserPro");
        if(fruitObject==null){
            return "查询异常！";
        }
        String name = fruitObject.getString("name");
        int treeEnergy = fruitObject.getInteger("treeEnergy");
        int treeTotalEnergy = fruitObject.getInteger("treeTotalEnergy");
        int treeState = fruitObject.getInteger("treeState");
        String totalEnergy = fruitObject.getString("totalEnergy");
        String jDwaterEveryDayT = fruitObject.getString("JDwaterEveryDayT");

        float percent = ((float)treeEnergy/(float) treeTotalEnergy)*100;

        if(!StringUtils.isEmpty(name)){
            if(treeEnergy!=0){
                if (treeState == 2 || treeState == 3) {
                    massage = "【东东农场】 "+name+" 可以兑换了!";
                } else {
                    massage = "【东东农场】"+name+"  "+percent+"%";
                }
            }else {
                if (treeState == 0) {
                    massage = "【东东农场】水果领取后未重新种植!";

                } else if (treeState == 1) {
                    massage = "【东东农场】"+name+"种植中...";
                } else {
                    massage = "【东东农场】状态异常!";
                }
            }
        }

        return massage;
    }

    public String jdFruitRequest(String cookie) throws IOException {

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://api.m.jd.com/client.action/?functionId=initForFarm&body=%7B%22version%22%3A%2014%2C%22channel%22%3A%201%2C%22babelChannel%22%3A%20%22120%22%7D&appid=wh5")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Accept-Language", "zh-cn")
                .addHeader("Connection", "keep-alive")
                .addHeader("Cookie", cookie)
                .addHeader("sec-fetch-dest", "empty")
                .addHeader("sec-fetch-mode", "cors")
                .addHeader("sec-fetch-site", "same-site")
                .addHeader("Referer", "https://wqs.jd.com/my/jingdou/my.shtml?sceneval=2")
                .addHeader("User-Agen", getUserAgent())
                .addHeader("origin", "https://home.m.jd.com")
                .addHeader("cache-control", "no-cache")
                .build();
        Response response = okHttpClient.newCall(request).execute();
        return response.body().string();
    }

    public String getJdFruit(String cookie) throws IOException {

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=utf-8"), "body=%7B%22version%22%3A4%7D&appid=wh5&clientVersion=9.1.0");
//        RequestBody formBody = new FormBody.Builder()
//                .add("body", "%7B%22version%22%3A4%7D")
//                .add("appid", "wh5")
//                .add("clientVersion","9.1.0")
//                .build();

        Request request = new Request.Builder()
                .url("https://api.m.jd.com/client.action?functionId=initForFarm")
                .post(requestBody)
                .header("accept", "*/*")
                .addHeader("accept-language","zh-CN,zh;q=0.9")
                .addHeader("cache-control","no-cache")
                .addHeader("cookie",cookie)
                .addHeader("origin","https://home.m.jd.com")
                .addHeader("pragma","no-cache")
                .addHeader("referer","https://home.m.jd.com/myJd/newhome.action")
                .addHeader("sec-fetch-dest","empty")
                .addHeader("sec-fetch-mode","cors")
                .addHeader("sec-fetch-site","same-site")
                .addHeader("User-Agent",getUserAgent())
                .addHeader("Content-Type","application/x-www-form-urlencoded;charset=utf-8")
                .build();
        Call call=okHttpClient.newCall(request);
        Response response = call.execute();
        return response.body().string();
    }


    public String getUserAgent(){
        String []userAgentArr = new String[]{
                "jdapp;android;11.2.8;;;Mozilla/5.0 (Linux; Android 10; ONEPLUS A5010 Build/QKQ1.191014.012; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/76.0.3809.89 MQQBrowser/6.2 TBS/045230 Mobile Safari/537.36",
                "jdapp;iPhone;11.2.6;;;Mozilla/5.0 (iPhone; CPU iPhone OS 14_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
                "jdapp;android;11.2.5;;;Mozilla/5.0 (Linux; Android 9; Mi Note 3 Build/PKQ1.181007.001; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/78.0.3904.108 MQQBrowser/6.2 TBS/045131 Mobile Safari/537.36",
                "jdapp;android;11.2.4;;;Mozilla/5.0 (Linux; Android 10; GM1910 Build/QKQ1.190716.003; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/76.0.3809.89 MQQBrowser/6.2 TBS/045230 Mobile Safari/537.36",
                "jdapp;android;11.2.2;;;Mozilla/5.0 (Linux; Android 9; 16T Build/PKQ1.190616.001; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/78.0.3904.108 MQQBrowser/6.2 TBS/044942 Mobile Safari/537.36",
                "jdapp;iPhone;11.2.0;;;Mozilla/5.0 (iPhone; CPU iPhone OS 13_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
                "jdapp;iPhone;11.1.4;;;Mozilla/5.0 (iPhone; CPU iPhone OS 13_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
                "jdapp;iPhone;11.1.2;;;Mozilla/5.0 (iPhone; CPU iPhone OS 13_5 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
                "jdapp;iPhone;11.1.0;;;Mozilla/5.0 (iPhone; CPU iPhone OS 14_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
                "jdapp;iPhone;11.0.4;;;Mozilla/5.0 (iPhone; CPU iPhone OS 13_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
                "jdapp;iPhone;11.0.2;;;Mozilla/5.0 (iPhone; CPU iPhone OS 13_7 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
                "jdapp;iPhone;11.0.0;;;Mozilla/5.0 (iPhone; CPU iPhone OS 14_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
                "jdapp;iPhone;10.5.4;;;Mozilla/5.0 (iPhone; CPU iPhone OS 13_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
                "jdapp;iPhone;10.5.2;;;Mozilla/5.0 (iPhone; CPU iPhone OS 13_4 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
                "jdapp;iPhone;10.5.0;;;Mozilla/5.0 (iPhone; CPU iPhone OS 14_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
                "jdapp;android;11.2.8;;;Mozilla/5.0 (Linux; Android 9; MI 6 Build/PKQ1.190118.001; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/78.0.3904.108 MQQBrowser/6.2 TBS/044942 Mobile Safari/537.36",
                "jdapp;android;11.2.6;;;Mozilla/5.0 (Linux; Android 11; Redmi K30 5G Build/RKQ1.200826.002; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/76.0.3809.89 MQQBrowser/6.2 TBS/045511 Mobile Safari/537.36",
                "jdapp;iPhone;11.2.5;;;Mozilla/5.0 (iPhone; CPU iPhone OS 11_4 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15F79",
                "jdapp;android;11.2.4;;;Mozilla/5.0 (Linux; Android 10; M2006J10C Build/QP1A.190711.020; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/76.0.3809.89 MQQBrowser/6.2 TBS/045230 Mobile Safari/537.36",
                "jdapp;android;11.2.2;;;Mozilla/5.0 (Linux; Android 10; M2006J10C Build/QP1A.190711.020; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/76.0.3809.89 MQQBrowser/6.2 TBS/045230 Mobile Safari/537.36",
                "jdapp;android;11.2.0;;;Mozilla/5.0 (Linux; Android 10; ONEPLUS A6000 Build/QKQ1.190716.003; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/76.0.3809.89 MQQBrowser/6.2 TBS/045224 Mobile Safari/537.36",
                "jdapp;android;11.1.4;;;Mozilla/5.0 (Linux; Android 9; MHA-AL00 Build/HUAWEIMHA-AL00; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/78.0.3904.108 MQQBrowser/6.2 TBS/044942 Mobile Safari/537.36",
                "jdapp;android;11.1.2;;;Mozilla/5.0 (Linux; Android 8.1.0; 16 X Build/OPM1.171019.026; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/78.0.3904.108 MQQBrowser/6.2 TBS/044942 Mobile Safari/537.36",
                "jdapp;android;11.1.0;;;Mozilla/5.0 (Linux; Android 8.0.0; HTC U-3w Build/OPR6.170623.013; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/78.0.3904.108 MQQBrowser/6.2 TBS/044942 Mobile Safari/537.36",
                "jdapp;iPhone;11.0.4;;;Mozilla/5.0 (iPhone; CPU iPhone OS 14_0_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
                "jdapp;android;11.0.2;;;Mozilla/5.0 (Linux; Android 10; LYA-AL00 Build/HUAWEILYA-AL00L; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/76.0.3809.89 MQQBrowser/6.2 TBS/045230 Mobile Safari/537.36",
                "jdapp;iPhone;11.0.0;;;Mozilla/5.0 (iPhone; CPU iPhone OS 14_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
                "jdapp;iPhone;10.5.4;;;Mozilla/5.0 (iPhone; CPU iPhone OS 14_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
                "jdapp;iPhone;10.5.2;;;Mozilla/5.0 (iPhone; CPU iPhone OS 14_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
                "jdapp;android;10.5.0;;;Mozilla/5.0 (Linux; Android 8.1.0; MI 8 Build/OPM1.171019.026; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/78.0.3904.108 MQQBrowser/6.2 TBS/045131 Mobile Safari/537.36",
                "jdapp;android;11.2.8;;;Mozilla/5.0 (Linux; Android 10; Redmi K20 Pro Premium Edition Build/QKQ1.190825.002; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/76.0.3809.89 MQQBrowser/6.2 TBS/045227 Mobile Safari/537.36",
                "jdapp;iPhone;11.2.5;;;Mozilla/5.0 (iPhone; CPU iPhone OS 14_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
                "jdapp;iPhone;11.2.4;;;Mozilla/5.0 (iPhone; CPU iPhone OS 14_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
                "jdapp;android;11.2.2;;;Mozilla/5.0 (Linux; Android 11; Redmi K20 Pro Premium Edition Build/RKQ1.200826.002; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/76.0.3809.89 MQQBrowser/6.2 TBS/045513 Mobile Safari/537.36",
                "jdapp;android;11.2.0;;;Mozilla/5.0 (Linux; Android 10; MI 8 Build/QKQ1.190828.002; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/76.0.3809.89 MQQBrowser/6.2 TBS/045227 Mobile Safari/537.36",
                "jdapp;iPhone;11.1.4;;;Mozilla/5.0 (iPhone; CPU iPhone OS 14_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
                "jdapp;android;11.0.1;;;Mozilla/5.0 (Linux; Android 10; ONEPLUS A5010 Build/QKQ1.191014.012; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/76.0.3809.89 MQQBrowser/6.2 TBS/045230 Mobile Safari/537.36",
                "jdapp;iPhone;11.1.2;;;Mozilla/5.0 (iPhone; CPU iPhone OS 14_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
                "jdapp;android;11.1.0;;;Mozilla/5.0 (Linux; Android 10; Mi Note 5 Build/PKQ1.181007.001; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/78.0.3904.108 MQQBrowser/6.2 TBS/045131 Mobile Safari/537.36",
                "jdapp;android;11.0.4;;;Mozilla/5.0 (Linux; Android 11; LIO-AN00 Build/HUAWEILIO-AN00; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/76.0.3809.89 MQQBrowser/6.2 TBS/045230 Mobile Safari/537.36",
                "jdapp;android;11.0.2;;;Mozilla/5.0 (Linux; Android 10; SKW-A0 Build/SKYW2001202CN00MQ0; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/78.0.3904.108 MQQBrowser/6.2 TBS/044942 Mobile Safari/537.36",
                "jdapp;iPhone;11.0.0;;;Mozilla/5.0 (iPhone; CPU iPhone OS 13_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
                "jdapp;iPhone;10.5.4;;;Mozilla/5.0 (iPhone; CPU iPhone OS 13_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
                "jdapp;iPhone;10.5.2;;;Mozilla/5.0 (iPhone; CPU iPhone OS 13_5 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
                "jdapp;iPhone;10.5.0;;;Mozilla/5.0 (iPhone; CPU iPhone OS 14_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
                "jdapp;iPhone;11.2.8;;;Mozilla/5.0 (iPhone; CPU iPhone OS 13_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
                "jdapp;iPhone;11.2.5;;;Mozilla/5.0 (iPhone; CPU iPhone OS 13_7 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
                "jdapp;iPhone;11.2.4;;;Mozilla/5.0 (iPhone; CPU iPhone OS 14_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
                "jdapp;iPhone;11.2.2;;;Mozilla/5.0 (iPhone; CPU iPhone OS 13_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
                "jdapp;iPhone;11.2.0;;;Mozilla/5.0 (iPhone; CPU iPhone OS 13_4 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
                "jdapp;iPhone;11.1.4;;;Mozilla/5.0 (iPhone; CPU iPhone OS 14_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
                "jdapp;android;11.1.2;;;Mozilla/5.0 (Linux; Android 9; MI 6 Build/PKQ1.190118.001; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/78.0.3904.108 MQQBrowser/6.2 TBS/044942 Mobile Safari/537.36",
                "jdapp;android;11.1.0;;;Mozilla/5.0 (Linux; Android 12; Redmi K30 5G Build/RKQ1.200826.002; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/76.0.3809.89 MQQBrowser/6.2 TBS/045511 Mobile Safari/537.36",
                "jdapp;iPhone;11.0.4;;;Mozilla/5.0 (iPhone; CPU iPhone OS 11_4 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15F79",
                "jdapp;android;11.0.2;;;Mozilla/5.0 (Linux; Android 10; M2006J10C Build/QP1A.190711.020; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/76.0.3809.89 MQQBrowser/6.2 TBS/045230 Mobile Safari/537.36",
                "jdapp;android;11.0.0;;;Mozilla/5.0 (Linux; Android 12; HWI-AL00 Build/HUAWEIHWI-AL00; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/76.0.3809.89 MQQBrowser/6.2 TBS/045230 Mobile Safari/537.36",
                "jdapp;android;10.5.4;;;Mozilla/5.0 (Linux; Android 10; ANE-AL00 Build/HUAWEIANE-AL00; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/76.0.3809.89 MQQBrowser/6.2 TBS/045224 Mobile Safari/537.36",
                "jdapp;android;10.5.2;;;Mozilla/5.0 (Linux; Android 9; ELE-AL00 Build/HUAWEIELE-AL00; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/78.0.3904.108 MQQBrowser/6.2 TBS/044942 Mobile Safari/537.36",
                "jdapp;android;10.5.0;;;Mozilla/5.0 (Linux; Android 10; LIO-AL00 Build/HUAWEILIO-AL00; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/78.0.3904.108 MQQBrowser/6.2 TBS/044942 Mobile Safari/537.36",
                "jdapp;android;11.2.8;;;Mozilla/5.0 (Linux; Android 10; SM-G9750 Build/QP1A.190711.020; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/78.0.3904.108 MQQBrowser/6.2 TBS/044942 Mobile Safari/537.36",
                "jdapp;iPhone;11.2.5;;;Mozilla/5.0 (iPhone; CPU iPhone OS 14_0_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
                "jdapp;android;11.2.4;;;Mozilla/5.0 (Linux; Android 12; EVR-AL00 Build/HUAWEIEVR-AL00; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/76.0.3809.89 MQQBrowser/6.2 TBS/045230 Mobile Safari/537.36",
                "jdapp;iPhone;11.2.2;;;Mozilla/5.0 (iPhone; CPU iPhone OS 14_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
                "jdapp;iPhone;11.2.0;;;Mozilla/5.0 (iPhone; CPU iPhone OS 14_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
                "jdapp;iPhone;11.1.4;;;Mozilla/5.0 (iPhone; CPU iPhone OS 14_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
                "jdapp;android;11.1.2;;;Mozilla/5.0 (Linux; Android 8.1.0; MI 8 Build/OPM1.171019.026; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/78.0.3904.108 MQQBrowser/6.2 TBS/045131 Mobile Safari/537.36",
                "jdapp;android;11.1.0;;;Mozilla/5.0 (Linux; Android 9; HLK-AL00 Build/HONORHLK-AL00; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/76.0.3809.89 MQQBrowser/6.2 TBS/045227 Mobile Safari/537.36",
                "jdapp;iPhone;11.0.4;;;Mozilla/5.0 (iPhone; CPU iPhone OS 14_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
                "jdapp;iPhone;11.0.2;;;Mozilla/5.0 (iPhone; CPU iPhone OS 14_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1",
                "jdapp;android;11.0.0;;;Mozilla/5.0 (Linux; Android 10; LYA-AL10 Build/HUAWEILYA-AL10; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/76.0.3809.89 MQQBrowser/6.2 TBS/045513 Mobile Safari/537.36",
                "jdapp;android;10.5.4;;;Mozilla/5.0 (Linux; Android 10; MI 9 Build/QKQ1.190825.002; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/76.0.3809.89 MQQBrowser/6.2 TBS/045227 Mobile Safari/537.36",
                "jdapp;iPhone;10.5.2;;;Mozilla/5.0 (iPhone; CPU iPhone OS 14_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1"
        };
        return userAgentArr[new Random().nextInt(userAgentArr.length)+1];
    }

}
