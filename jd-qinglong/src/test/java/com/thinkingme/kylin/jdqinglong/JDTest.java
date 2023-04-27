package com.thinkingme.kylin.jdqinglong;

import okhttp3.*;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *
 * </p>
 *
 * @author: huige
 * @date: 2023/4/21 13:24
 */
public class JDTest {

    @Test
    public void fruit(){
        OkHttpClient okHttpClient = new OkHttpClient
                .Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS).build();

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
                .addHeader("cookie","pt_key=AAJkMCIaADDyQ7usZleCVQS-pu_8Bctm00AHAax6gUeghk3IEVZtipPNT8avqmxVrBOOBVDvscc;pt_pin=jd_vgRktSCsbbIj;")
                .addHeader("origin","https://home.m.jd.com")
                .addHeader("pragma","no-cache")
                .addHeader("referer","https://home.m.jd.com/myJd/newhome.action")
                .addHeader("sec-fetch-dest","empty")
                .addHeader("sec-fetch-mode","cors")
                .addHeader("sec-fetch-site","same-site")
                .addHeader("User-Agent","jdapp;iPhone;9.4.4;14.3;network/4g;Mozilla/5.0 (iPhone; CPU iPhone OS 14_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1")
                .addHeader("Content-Type","application/x-www-form-urlencoded;charset=utf-8")
                .build();
        Call call=okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            ResponseBody responseBody = response.body();
            String string = responseBody.string();
            System.out.println(string);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}


//$.JdFarmProdName = $.farmInfo.farmUserPro.name;
//        $.JdtreeEnergy = $.farmInfo.farmUserPro.treeEnergy;
//        $.JdtreeTotalEnergy = $.farmInfo.farmUserPro.treeTotalEnergy;
//        $.treeState = $.farmInfo.treeState;
//        let waterEveryDayT = $.JDwaterEveryDayT;
//        let waterTotalT = ($.farmInfo.farmUserPro.treeTotalEnergy - $.farmInfo.farmUserPro.treeEnergy - $.farmInfo.farmUserPro.totalEnergy) / 10; //一共还需浇多少次水
//        let waterD = Math.ceil(waterTotalT / waterEveryDayT);
//
//        $.JdwaterTotalT = waterTotalT;
//        $.JdwaterD = waterD;