package com;

import com.alibaba.fastjson.JSONObject;
import com.client.DingTalkClient;
import com.client.DingTalkYiDaClient;
import com.config.DingTalkConfig;
import com.config.DingTalkYiDaConfig;
import com.config.YiDaConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class ApplicationTest {

    @Test
    public void UpdateInstancesDataTest() {
        DingTalkConfig dingTalkConfig = YiDaConfig.getDingTalkConfig().toJavaObject(DingTalkConfig.class);
        JSONObject yiDaConfigObj = YiDaConfig.getDingTalkYiDaConfig();
        DingTalkYiDaConfig yiDaConfig = yiDaConfigObj.toJavaObject(DingTalkYiDaConfig.class);
        DingTalkYiDaClient yiDaClient = new DingTalkYiDaClient(dingTalkConfig, yiDaConfig);
        String appType = "APP_C1BXS10DGPJNFD2OCQ5N";
        String systemToken = "99E668B181ETUITW6VZXM4NB9MIA3POGGVS7M8B";

        Map<String, String> updateFormDataJsonMap = new HashMap<>();
        JSONObject data = new JSONObject();
        data.put("radioField_m7t1w8a2", "待付款");
        data.put("dateField_ma2b7fcu", "1745510400000");
        String listStr = "";
        List<String> list = Arrays.asList(listStr.split(","));
        list.forEach(instanceId -> {
            updateFormDataJsonMap.put(instanceId, data.toJSONString());
        });
        String body = yiDaClient.updateInstancesDatas(true, "FORM-03227602943D4D3B8E78C4AAD8C792733SGG", false,
                appType, systemToken, false, updateFormDataJsonMap, true, "17405624843132918");
        System.out.println(body);
    }

    @Test
    public void sendRobotMessage() {
        DingTalkConfig dingTalkConfig = YiDaConfig.getDingTalkConfig().toJavaObject(DingTalkConfig.class);
        DingTalkClient dingTalkClient = new DingTalkClient(dingTalkConfig);
        String robotCode = "ding2c7psoboguhdyvnk";
        List<String> dingUserIdList = List.of("263609342222576294");
        String msgKey = "sampleMarkdown";
        String title = String.format("您好，%s年%s月薪资发放异常数据请查收！", 2025, 6);
        String url = "https://www.baidu.com?ddtab=true";
        String text = String.format("## %s  \n  异常数据总量：%s条  \n  对公支付异常数据：%s条  \n  对私支付异常数据：%s条  \n  推送时间：%s  \n  [                                    详情](%s)",
                title, 0, 0, 0, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), url);
        JSONObject msgParam = new JSONObject();
        msgParam.put("title", title);
        msgParam.put("text", text);
        JSONObject result = dingTalkClient.robotOtomessageBatchSend(robotCode, dingUserIdList, msgKey, msgParam.toJSONString());
        System.out.println(result);
    }
}
