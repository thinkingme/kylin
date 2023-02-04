package com.thinkingme.kylin.jdqinglong.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.thinkingme.kylin.jdqinglong.bean.JDCookie;
import com.thinkingme.kylin.jdqinglong.bean.QLConfig;
import com.thinkingme.kylin.jdqinglong.bean.QLToken;
import com.thinkingme.kylin.jdqinglong.bean.QLUploadStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * <p>
 * 青龙面板的服务接口
 * </p>
 *
 * @author: huige
 * @date: 2022/12/21 16:19
 */
@Service
@Slf4j
public class QingLongService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private List<QLConfig> qlConfigs;

    /**
     * 构造请求头
     * @param qlConfig
     * @return
     */
    private HttpHeaders getHttpHeaders(QLConfig qlConfig) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + qlConfig.getQlToken().getToken());
        headers.add("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4577.63 Safari/537.36");
        headers.add("Content-Type", "application/json;charset=UTF-8");
        headers.add("Accept-Encoding", "gzip, deflate");
        headers.add("Accept-Language", "zh-CN,zh;q=0.9");
        return headers;
    }

    public boolean getToken(QLConfig qlConfig) {
        String qlUrl = qlConfig.getQlUrl();
        String qlClientID = qlConfig.getQlClientID();
        String qlClientSecret = qlConfig.getQlClientSecret();
        try {
            ResponseEntity<String> entity = restTemplate.getForEntity(qlUrl + "/open/auth/token?client_id=" + qlClientID + "&client_secret=" + qlClientSecret, String.class);
            if (entity.getStatusCodeValue() == 200) {
                String body = entity.getBody();
                log.info("获取token " + body);
                JSONObject jsonObject = JSON.parseObject(body);
                Integer code = jsonObject.getInteger("code");
                if (code == 200) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    String token = data.getString("token");
                    String tokenType = data.getString("token_type");
                    long expiration = data.getLong("expiration");
                    qlConfig.setQlToken(new QLToken(token, tokenType, expiration));
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(qlUrl + "获取token失败，请检查配置");
        }
        return false;
    }

    /**
     * 查询青龙面板的所有环境变量
     * @param qlConfig
     * @param searchValue 为空串时查询所有
     * @return
     */

    public JSONArray getQingLongEnv( QLConfig qlConfig, String searchValue) {

        if (qlConfig.getQlToken() == null) {
            return null;
        }
        String url = qlConfig.getQlUrl() + "/" + "open" + "/envs?searchValue=" + searchValue + "&t=" + System.currentTimeMillis();
        log.info("开始获取当前ck数量" + url);
        HttpHeaders headers = getHttpHeaders(qlConfig);
        ResponseEntity<String> exchange = null;
        exchange = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        if (exchange.getStatusCode().is2xxSuccessful()) {
            String body = exchange.getBody();
            return JSON.parseObject(body).getJSONArray("data");
        } else if (exchange.getStatusCodeValue() == 401) {
            log.info("token" + qlConfig.getQlToken().getToken() + "失效");
        }
        return null;
    }

    public ResponseEntity<String> postJDCookie2QingLong(String ck,String remark,QLConfig qlConfig){
        HttpHeaders headers = getHttpHeaders(qlConfig);
        String url = qlConfig.getQlUrl() + "/" + "open" + "/envs?t=" + System.currentTimeMillis();
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("value", ck);
        jsonObject.put("name", "JD_COOKIE");
        jsonObject.put("remarks",remark);
        jsonArray.add(jsonObject);
        HttpEntity<?> request = new HttpEntity<>(jsonArray.toJSONString(), headers);
        log.info("开始上传ck " + url);
        ResponseEntity<String> exchange = null;
        try {
            exchange = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            if (exchange.getStatusCode().is2xxSuccessful()) {
                log.info("create resp content : " + exchange.getBody() + ", resp code : " + exchange.getStatusCode());
                return exchange;
            }
        } catch (HttpClientErrorException.Unauthorized e) {
            int rawStatusCode = e.getRawStatusCode();
            log.info(rawStatusCode + " : token" + qlConfig.getQlToken().getToken() + "失效");
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 启动京东cookie
     * @param updateId 对应环境变量的id
     * @param qlConfig 青龙配置
     * @return
     */

    public ResponseEntity<String> enableJDCookie2QingLong(String updateId,QLConfig qlConfig){
        HttpHeaders headers = getHttpHeaders(qlConfig);
        log.info("开始启用ck" + updateId);
        JSONArray enableBody = new JSONArray();
        enableBody.add(updateId);
        HttpEntity<?> enableRequest = new HttpEntity<>(enableBody.toJSONString(), headers);
        String enableUrl = qlConfig.getQlUrl() + "/" + "open" + "/envs/enable?t=" + System.currentTimeMillis();
        ResponseEntity<String> enableExchange = restTemplate.exchange(enableUrl, HttpMethod.PUT, enableRequest, String.class);
        if (enableExchange.getStatusCode().is2xxSuccessful()) {
            log.info("enableCookie resp content : " + enableExchange.getBody() + ", resp code : " + enableExchange.getStatusCode());
        }
        return null;
    }
    /**
     * 禁用京东cookie
     * @param updateId 对应环境变量的id
     * @param qlConfig 青龙配置
     * @return
     */

    public ResponseEntity<String> disableJDCookie2QingLong(String updateId,QLConfig qlConfig){
        HttpHeaders headers = getHttpHeaders(qlConfig);
        log.info("开始禁用ck" + updateId);
        JSONArray enableBody = new JSONArray();
        enableBody.add(updateId);
        HttpEntity<?> enableRequest = new HttpEntity<>(enableBody.toJSONString(), headers);
        String enableUrl = qlConfig.getQlUrl() + "/" + "open" + "/envs/disable?t=" + System.currentTimeMillis();
        ResponseEntity<String> enableExchange = restTemplate.exchange(enableUrl, HttpMethod.PUT, enableRequest, String.class);
        if (enableExchange.getStatusCode().is2xxSuccessful()) {
            log.info("disableCookie resp content : " + enableExchange.getBody() + ", resp code : " + enableExchange.getStatusCode());
        }
        return null;
    }

    public QLUploadStatus uploadQingLongWithToken( String ck, String remark, QLConfig qlConfig) throws Exception {
        JDCookie jdCookie = JDCookie.parse(ck);
        int res = -1;
        String pushRes = "";
        boolean update = false;
        String updateId = "";
        String updateRemark = null;
        JSONArray data = getQingLongEnv( qlConfig, "JD_COOKIE");

        // pt_pin存在则更新，无则增加
        if (data != null && data.size() > 0) {
            for (int i = 0; i < data.size(); i++) {
                JSONObject jsonObject = data.getJSONObject(i);
                String id = jsonObject.getString("id");
                String value = jsonObject.getString("value");
                JDCookie oldCookie = null;
                oldCookie = JDCookie.parse(value);
                if (oldCookie.getPtPin().equals(jdCookie.getPtPin())) {
                    update = true;
                    updateId = id;
                    break;
                }
            }
        }
        HttpHeaders headers = getHttpHeaders(qlConfig);
        String url = qlConfig.getQlUrl() + "/" + "open" + "/envs?t=" + System.currentTimeMillis();
        if (!update) {
            if (qlConfig.getRemain() <= 0) {
                return new QLUploadStatus(qlConfig, res, qlConfig.getRemain() <= 0, pushRes);
            }
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("value", jdCookie.toString());
            jsonObject.put("name", "JD_COOKIE");
            jsonObject.put("remarks",remark);
            jsonArray.add(jsonObject);
            HttpEntity<?> request = new HttpEntity<>(jsonArray.toJSONString(), headers);
            log.info("开始上传ck " + url);
            ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            if (exchange.getStatusCode().is2xxSuccessful()) {
                log.info("create resp content : " + exchange.getBody() + ", resp code : " + exchange.getStatusCode());
                res = 1;
            }
        } else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("value", jdCookie.toString());
            jsonObject.put("name", "JD_COOKIE");
            jsonObject.put("remarks", remark);
            jsonObject.put("id", updateId);
            HttpEntity<?> request = new HttpEntity<>(jsonObject.toJSONString(), headers);
            log.info("开始更新ck" + url);
            ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
            if (exchange.getStatusCode().is2xxSuccessful()) {
                log.info("update resp content : " + exchange.getBody() + ", resp code : " + exchange.getStatusCode());
                String body = exchange.getBody();
                JSONObject result = JSON.parseObject(body).getJSONObject("data");
                log.info("ck状态" + result.getString("status"));
                // status为1说明被禁用了，为0是启用
                if (result.getIntValue("status") == 1) {
                    log.info("开始启用ck" + updateId);
                    JSONArray enableBody = new JSONArray();
                    enableBody.add(updateId);
                    HttpEntity<?> enableRequest = new HttpEntity<>(enableBody.toJSONString(), headers);
                    String enableUrl = qlConfig.getQlUrl() + "/" + "open" + "/envs/enable?t=" + System.currentTimeMillis();
                    ResponseEntity<String> enableExchange = restTemplate.exchange(enableUrl, HttpMethod.PUT, enableRequest, String.class);
                    if (enableExchange.getStatusCode().is2xxSuccessful()) {
                        log.info("enableCookie resp content : " + enableExchange.getBody() + ", resp code : " + enableExchange.getStatusCode());
                    }
                }
                res = 1;
            }
        }
        return new QLUploadStatus(qlConfig, res, qlConfig.getRemain() <= 0, pushRes);
    }

    /**
     * 上传cookie到青龙面板。
     * @param remark
     * @param ck
     * @return
     * @throws Exception
     */
    public JSONObject uploadQingLong(String remark, String ck) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", 0);
        List<QLUploadStatus> uploadStatuses = new ArrayList<>();
        if (qlConfigs != null) {
            for (QLConfig qlConfig : qlConfigs) {
                    QLUploadStatus status = uploadQingLongWithToken(ck, remark, qlConfig);
                    log.info("上传" + qlConfig.getQlUrl() + "结果" + status.getUploadStatus());
                    uploadStatuses.add(status);
            }
        }

        StringBuilder errorMsg = new StringBuilder();
        StringBuilder successMsg = new StringBuilder();
        for (QLUploadStatus uploadStatus : uploadStatuses) {
            String label = uploadStatus.getQlConfig().getLabel();
            if (uploadStatus.getUploadStatus() <= 0) {
                if (!StringUtils.isEmpty(label)) {
                    errorMsg.append(label);
                } else {
                    errorMsg.append("QL_URL_").append(uploadStatus.getQlConfig().getId());
                }
                errorMsg.append("上传失败<br/>");
            }
            if (uploadStatus.isFull()) {
                if (!StringUtils.isEmpty(label)) {
                    errorMsg.append(label);
                } else {
                    errorMsg.append("QL_URL_").append(uploadStatus.getQlConfig().getId());
                }
                errorMsg.append("超容量了<br/>");
            }
            if (uploadStatus.getUploadStatus() > 0) {
                if (!StringUtils.isEmpty(label)) {
                    successMsg.append(label);
                } else {
                    successMsg.append("QL_URL_").append(uploadStatus.getQlConfig().getId());
                }
                successMsg.append("上传成功<br/>");
            }
        }
        if (errorMsg.length() > 0) {
            jsonObject.put("status", -2);
            jsonObject.put("html", errorMsg.toString());
            return jsonObject;
        }
        jsonObject.put("status", 2);
        String s = successMsg.toString();
        if (s.endsWith("<br/>")) {
            s = s.substring(0, s.length() - 5);
        }
        jsonObject.put("html", s);
        return jsonObject;
    }

    //TODO 源哈希负载均衡 如果容器多了的话可以做一个
}
