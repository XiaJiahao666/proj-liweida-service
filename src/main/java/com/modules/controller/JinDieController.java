package com.modules.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.client.JinDieClient;
import com.common.R;
import com.dto.JinDieDo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("jindie")
public class JinDieController {

    @PostMapping("page")
    public R<JSONArray> queryPage(@RequestBody JinDieDo jinDieDo) {
        JinDieClient jinDieClient = new JinDieClient();
        JSONObject bodyParam = new JSONObject();
        bodyParam.put("parameters", jinDieDo.getParameters());
        return R.ok(jinDieClient.queryPage(bodyParam));
    }

    @PostMapping("info")
    public R<JSONObject> queryInfo(@RequestBody JinDieDo jinDieDo) {
        JinDieClient jinDieClient = new JinDieClient();
        JSONObject bodyParam = new JSONObject();
        bodyParam.put("parameters", jinDieDo.getParameters());
        return R.ok(jinDieClient.queryInfo(bodyParam));
    }
}
