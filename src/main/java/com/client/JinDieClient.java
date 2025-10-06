package com.client;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
public class JinDieClient {

    String aspNetSessionId = null;

    String kdServiceSessionId = null;

    public void login() {
        String url = "http://60.173.16.127:2021/K3Cloud/Kingdee.BOS.WebApi.ServicesStub.AuthService.ValidateUser.common.kdsvc";
        JSONObject bodyParams = new JSONObject();
        bodyParams.put("parameters", List.of("689e99d014d661", "小兰兰", "wulan0826", 2052));
        HttpResponse response = HttpRequest.post(url)
                .header("Content-Type", "application/json")
                .body(bodyParams.toJSONString())
                .execute();
        Map<String, List<String>> headers = response.headers();
        String cookies = headers.get("Set-Cookie").toString();
        cookies = cookies.replace("[", "")
                .replace("]", "")
                .replaceAll("path=/;", "")
                .replaceAll("path=/", "")
                .replaceAll("HttpOnly,", "")
                .replaceAll("HttpOnly", "")
                .replaceAll(" ", "");
        List<String> list = List.of(cookies.split(";"));
        for (String cookie : list) {
            // 查找 ASP.NET_SessionId
            if (cookie.startsWith("ASP.NET_SessionId=")) {
                aspNetSessionId = cookie.split("=")[1];
            }
            // 查找 kdservice-sessionid
            else if (cookie.startsWith("kdservice-sessionid=")) {
                kdServiceSessionId = cookie.split("=")[1];
            }
        }
    }

    public JSONArray queryPage(JSONObject bodyParams) {
        this.login();
        String url = "http://60.173.16.127:2021/k3cloud/Kingdee.BOS.WebApi.ServicesStub.DynamicFormService.ExecuteBillQuery.common.kdsvc";
        HttpResponse response = HttpRequest.post(url)
                .cookie(String.format("ASP.NET_SessionId=%s; kdservice-sessionid=%s", aspNetSessionId, kdServiceSessionId))
                .header("Content-Type", "application/json")
                .body(bodyParams.toJSONString())
                .execute();
        String body = response.body();
        return JSONArray.parseArray(body);
    }

    public JSONObject queryInfo(JSONObject bodyParams) {
        this.login();
        String url = "http://60.173.16.127:2021/k3cloud/Kingdee.BOS.WebApi.ServicesStub.DynamicFormService.View.common.kdsvc";
        HttpResponse response = HttpRequest.post(url)
                .cookie(String.format("ASP.NET_SessionId=%s; kdservice-sessionid=%s", aspNetSessionId, kdServiceSessionId))
                .header("Content-Type", "application/json")
                .body(bodyParams.toJSONString())
                .execute();
        String body = response.body();
        return JSONObject.parseObject(response.body());
    }
}
