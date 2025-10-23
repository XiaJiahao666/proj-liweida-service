package com;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.client.DingTalkClient;
import com.client.DingTalkYiDaClient;
import com.client.JinDieClient;
import com.config.DingTalkConfig;
import com.config.DingTalkYiDaConfig;
import com.config.YiDaConfig;
import com.modules.schduler.MaterialTask;
import com.utils.SpringContextUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@SpringBootTest
public class ApplicationTest {

    @Test
    public void MaterialTask() {
        MaterialTask materialTask = SpringContextUtils.getBean(MaterialTask.class);
        materialTask.run();
    }

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

    @Test
    public void productData() throws IOException {
        List<List<String>> headList = List.of(List.of("Number", "Name", "Specification", "Unit", "金蝶更新时间"));
        List<List<String>> valueList = new ArrayList<>();
        boolean flag = true;
        int StartRow = 0;
        while (flag) {
            JSONObject pageParams = new JSONObject();
            pageParams.put("FormId", "BD_MATERIAL");
            pageParams.put("TopRowCount", 0);
            pageParams.put("Limit", 2000);
            pageParams.put("StartRow", StartRow);
            pageParams.put("FilterString", "");
            pageParams.put("OrderString", "");
            pageParams.put("FieldKeys", "FNumber,FName,FSpecification,FModifyDate");

            JinDieClient jinDieClient = new JinDieClient();
            JSONObject bodyParam = new JSONObject();
            bodyParam.put("parameters", List.of(pageParams));
            JSONArray materialArr = jinDieClient.queryPage(bodyParam);
            if (materialArr.isEmpty()) {
                flag = false;
            }
            List<String> materialList = materialArr.toJavaList(String.class);
            materialList.forEach(item -> {
                List<String> list = JSONArray.parseArray(item).toJavaList(String.class);
                JSONObject infoParam = new JSONObject();
                infoParam.put("Number", list.get(0));
                bodyParam.put("parameters", List.of("BD_MATERIAL", infoParam.toJSONString()));
                JSONObject info = jinDieClient.queryInfo(bodyParam);
                String unitNumber = info.getJSONObject("Result").getJSONObject("Result").getJSONArray("MaterialBase")
                        .toJavaList(JSONObject.class).get(0).getJSONObject("BaseUnitId").getString("Number");
                valueList.add(List.of(list.get(0), list.get(1), list.get(2), unitNumber, list.get(3)));
            });
            StartRow = StartRow + 2000;
        }

        String fileName = "D:/YiDaFile/product.xlsx";
        EasyExcel.write(fileName).head(headList).sheet().doWrite(valueList);
    }
}
