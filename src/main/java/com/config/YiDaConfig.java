package com.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.experimental.UtilityClass;

@UtilityClass
public class YiDaConfig {

    public JSONObject getDingTalkConfig() {
        return JSON.parseObject("{\"appKey\":\"ding2c7psoboguhdyvnk\",\"agentId\":3766768189,\"appSecret\":\"zpKIQn4z8SrZTPmxKilAI7Eg-XidQGb8Vlg9DB85nNVVwDf-wzbgSQwDfx0_Etw_\"}");
    }

    public JSONObject getDingTalkYiDaConfig() {
        return JSON.parseObject("{\"domain\":\"https://api.dingtalk.com\",\"userId\":\"263609342222576294\",\"appType\":\"APP_JZGW187VKTTUVV1OFCSD\",\"systemToken\":\"ULD66N812SOUQV2J6EAN5AXMR0A33STBSJM9MKD\"}");
    }

    public JSONObject getFormConfig() {
        return JSON.parseObject("{\"useFormSchema\":{\"formUuid\":\"FORM-CDBEC36EC79A4E1391947A119AD12B34TJBN\"}}");
    }
}
