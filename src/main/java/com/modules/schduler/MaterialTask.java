package com.modules.schduler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.client.JinDieClient;
import com.utils.YiDaUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component("materialTask")
public class MaterialTask {

//    @Scheduled(cron = "1 0/10 * * * ?")
    public void run() {
        log.error("MaterialTask start");
        List<JSONObject> createFormList = new ArrayList<>();
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.plusMinutes(-20);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String FilterString = String.format("FModifyDate between '%s' and '%s'", start.format(dateTimeFormatter), end.format(dateTimeFormatter));
        boolean flag = true;
        int StartRow = 0;
        while (flag) {
            JSONObject pageParams = new JSONObject();
            pageParams.put("FormId", "BD_MATERIAL");
            pageParams.put("TopRowCount", 0);
            pageParams.put("Limit", 2000);
            pageParams.put("StartRow", StartRow);
            pageParams.put("FilterString", FilterString);
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

                JSONObject createForm = new JSONObject();
                createForm.put("textField_mgt24vp3", list.get(0));
                createForm.put("textField_mgt24vp4", list.get(1));
                createForm.put("textField_mgt24vp5", list.get(2));
                createForm.put("textField_mgt24vp6", unitNumber);
                createForm.put("textField_mgt24vpb", list.get(3));
                createFormList.add(createForm);
            });
            StartRow = StartRow + 2000;
        }

        createFormList.forEach(createForm -> {
            JSONObject searchField = new JSONObject();
            searchField.put("textField_mgt24vp3", createForm.getString("textField_mgt24vp3"));
            searchField.put("textField_mgt24vp4", createForm.getString("textField_mgt24vp4"));
            searchField.put("textField_mgt24vp5", createForm.getString("textField_mgt24vp5"));
            searchField.put("textField_mgt24vp6", createForm.getString("textField_mgt24vp6"));
            List<JSONObject> materialList = YiDaUtils.getInstance().getFormDataList("FORM-28DCDDA33C2B4E6E8E08C746A50DA2FB5Z27", searchField.toJSONString());
            if (!materialList.isEmpty()) {
                String formInstanceId = materialList.get(0).getString("formInstanceId");
                YiDaUtils.getInstance().updateForm(formInstanceId, createForm.toJSONString());
            } else {
                YiDaUtils.getInstance().createForm("FORM-28DCDDA33C2B4E6E8E08C746A50DA2FB5Z27", createForm.toJSONString());
            }
        });
        log.error("MaterialTask end");
    }
}
