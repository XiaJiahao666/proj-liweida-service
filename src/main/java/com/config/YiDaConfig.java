package com.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.experimental.UtilityClass;

@UtilityClass
public class YiDaConfig {

    public JSONObject getDingTalkConfig() {
        return JSON.parseObject("{\"appKey\":\"dingir1ggsfgxg5tnm0i\",\"agentId\":4051040819,\"appSecret\":\"cZqvz2KqFNf7KDEendbr34HuBUSJwmTlv7qcND63l6X00AS7Vl1yGfInE59BPZxm\"}");
    }

    public JSONObject getDingTalkYiDaConfig() {
        return JSON.parseObject("{\"domain\":\"https://api.dingtalk.com\",\"userId\":\"1347603326689084\",\"appType\":\"APP_KAVYSD41EP15CZ3XLJFG\",\"systemToken\":\"69E66NA17YBYDLEY5RUZJ4OQ20X23BWK1SQEMC4\"}");
    }

    public JSONObject getFormConfig() {
        return JSON.parseObject("");
    }
}
