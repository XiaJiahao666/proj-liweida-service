package com.utils;

import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONValidator;
import com.client.DingTalkYiDaClient;
import com.client.RedisClient;
import com.config.DingTalkConfig;
import com.config.DingTalkYiDaConfig;
import com.config.YiDaConfig;
import com.redis.RedisKeys;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class YiDaUtils {

    private String managerUserId;

    private DingTalkYiDaClient yiDaClient;

    private final List<String> retryCodeList = List.of("ServiceUnavailable");

    private YiDaUtils() {
    }

    private static final YiDaUtils single = new YiDaUtils();

    //静态工厂方法
    public static YiDaUtils getInstance() {
        DingTalkConfig dingTalkConfig = YiDaConfig.getDingTalkConfig().toJavaObject(DingTalkConfig.class);
        JSONObject yiDaConfigObj = YiDaConfig.getDingTalkYiDaConfig();
        DingTalkYiDaConfig yiDaConfig = yiDaConfigObj.toJavaObject(DingTalkYiDaConfig.class);
        single.yiDaClient = new DingTalkYiDaClient(dingTalkConfig, yiDaConfig);
        single.managerUserId = yiDaConfigObj.getString("userId");
        return single;
    }

    public void deleteAll(String formUuid) {
        int startPage = 1;
        int pageSize = 99;
        while (true) {
            JSONObject paramsObject = new JSONObject();
            paramsObject.put("formUuid", formUuid);
            paramsObject.put("userId", managerUserId);
            paramsObject.put("pageSize", pageSize);
            paramsObject.put("searchFieldJson", "{}");
            paramsObject.put("currentPage", startPage);
            String respStr = yiDaClient.searchForm(paramsObject);
            JSONObject respObj = JSONObject.parseObject(respStr);
            JSONArray respData = respObj.getJSONArray("data");
            if (respObj.getInteger("totalCount") == 0) {
                break;
            }
            respData.toJavaList(JSONObject.class).parallelStream().forEachOrdered(form -> {
                deleteForm(form.getString("formInstanceId"));
            });
        }
    }

    /**
     * 根据条件查询单条数据
     *
     * @param formUuid        表单id
     * @param searchFieldJson 查询条件
     * @return formData
     */
    public JSONObject getFormData(String formUuid, String searchFieldJson) {
        int startPage = 1;
        int pageSize = 99;
        JSONObject paramsObject = new JSONObject();
        paramsObject.put("formUuid", formUuid);
        paramsObject.put("userId", managerUserId);
        paramsObject.put("pageSize", pageSize);
        paramsObject.put("searchFieldJson", searchFieldJson);
        paramsObject.put("currentPage", startPage);
        log.info("searchForm: {}", paramsObject.toJSONString());
        try {
            String respStr = yiDaClient.searchForm(paramsObject);
            log.info("respStr: {}", respStr);
            JSONObject respObj = JSONObject.parseObject(respStr);
            JSONArray respData = respObj.getJSONArray("data");
            if (respObj.getInteger("totalCount") == 0) {
                return null;
            }
            return respData.getJSONObject(0);
        } catch (Exception e) {
            return getFormData(formUuid, searchFieldJson);
        }
    }

    /**
     * 根据条件查询所有数据
     *
     * @param formUuid        表单id
     * @param searchFieldJson 查询条件
     * @return formData
     */
    public List<JSONObject> getFormDataList(String formUuid, String searchFieldJson, String createFromTimeGMT, String createToTimeGMT) {
        int startPage = 1;
        int pageSize = 99;
        JSONObject paramsObject = new JSONObject();
        paramsObject.put("formUuid", formUuid);
        paramsObject.put("userId", managerUserId);
        paramsObject.put("pageSize", pageSize);
        paramsObject.put("searchFieldJson", searchFieldJson);
        paramsObject.put("currentPage", startPage);
        paramsObject.put("createFromTimeGMT", createFromTimeGMT);
        paramsObject.put("createToTimeGMT", createToTimeGMT);
        log.info("searchForm: {}", paramsObject.toJSONString());
        List<JSONObject> dataList = new ArrayList<>();
        try {
            while (true) {
                paramsObject.put("currentPage", startPage);
                String respStr = yiDaClient.searchForm(paramsObject);
                log.info("respStr: {}", respStr);
                JSONObject respObj = JSONObject.parseObject(respStr);
                JSONArray respData = respObj.getJSONArray("data");
                dataList.addAll(respData.toJavaList(JSONObject.class));
                if (startPage * pageSize > respObj.getInteger("totalCount")) {
                    break;
                }
                startPage++;
            }
            return dataList;
        } catch (Exception e) {
            return getFormDataList(formUuid, searchFieldJson, createFromTimeGMT, createToTimeGMT);
        }
    }

    /**
     * 根据条件查询单条数据
     *
     * @param formUuid        表单id
     * @param searchFieldJson 查询条件
     * @return formData
     */
    public JSONObject getFormDataByUser(String formUuid, String searchFieldJson, String dingUserId) {
        int startPage = 1;
        int pageSize = 99;
        JSONObject paramsObject = new JSONObject();
        paramsObject.put("formUuid", formUuid);
        paramsObject.put("userId", dingUserId);
        paramsObject.put("pageSize", pageSize);
        paramsObject.put("searchFieldJson", searchFieldJson);
        paramsObject.put("currentPage", startPage);
        log.info("searchForm: {}", paramsObject.toJSONString());
        try {
            String respStr = yiDaClient.searchForm(paramsObject);
            log.info("respStr: {}", respStr);
            JSONObject respObj = JSONObject.parseObject(respStr);
            JSONArray respData = respObj.getJSONArray("data");
            if (respObj.getInteger("totalCount") == 0) {
                return null;
            }
            return respData.getJSONObject(0);
        } catch (Exception e) {
            return getFormDataByUser(formUuid, searchFieldJson, dingUserId);
        }
    }

    /**
     * 根据条件查询单条数据
     *
     * @param formInstanceId 表单实例id
     * @return formData
     */
    public JSONObject getFormInfo(String formInstanceId) {
        try {
            String respStr = yiDaClient.getForm(formInstanceId, managerUserId);
            log.info("respStr: {}", respStr);
            return JSONObject.parseObject(respStr);
        } catch (Exception e) {
            return getFormInfo(formInstanceId);
        }
    }

    /**
     * 根据条件查询单条数据
     *
     * @param formInstanceId 表单实例id
     * @param userId         宜搭用户id
     * @return formData
     */
    public JSONObject getFormDataById(String formInstanceId, String userId) {
        try {
            String respStr = yiDaClient.getForm(formInstanceId, userId);
            log.info("respStr: {}", respStr);
            return JSONObject.parseObject(respStr);
        } catch (Exception e) {
            return getFormDataById(formInstanceId, userId);
        }
    }

    /**
     * 根据条件查询分页数据
     *
     * @param formUuid        表单id
     * @param searchFieldJson 查询条件
     * @return formData
     */
    public JSONObject getFormDataForPage(String formUuid, int currentPage, int pageSize, String searchFieldJson) {
        JSONObject paramsObject = new JSONObject();
        paramsObject.put("formUuid", formUuid);
        paramsObject.put("userId", managerUserId);
        paramsObject.put("pageSize", pageSize);
        paramsObject.put("searchFieldJson", searchFieldJson);
        paramsObject.put("currentPage", currentPage);
        log.info("searchForm: {}", paramsObject.toJSONString());
        try {
            String respStr = yiDaClient.searchForm(paramsObject);
            log.info("respStr: {}", respStr);
            return JSONObject.parseObject(respStr);
        } catch (Exception e) {
            return getFormDataForPage(formUuid, currentPage, pageSize, searchFieldJson);
        }
    }

    /**
     * 根据条件查询分页数据
     *
     * @param formUuid        表单id
     * @param searchFieldJson 查询条件
     * @return formData
     */
    public JSONObject getFormDataForPageByUser(String formUuid, int currentPage, int pageSize, String searchFieldJson, String dingUserId) {
        JSONObject paramsObject = new JSONObject();
        paramsObject.put("formUuid", formUuid);
        paramsObject.put("userId", dingUserId);
        paramsObject.put("pageSize", pageSize);
        paramsObject.put("searchFieldJson", searchFieldJson);
        paramsObject.put("currentPage", currentPage);
        log.info("searchForm: {}", paramsObject.toJSONString());
        try {
            String respStr = yiDaClient.searchForm(paramsObject);
            log.info("respStr: {}", respStr);
            return JSONObject.parseObject(respStr);
        } catch (Exception e) {
            return getFormDataForPageByUser(formUuid, currentPage, pageSize, searchFieldJson, dingUserId);
        }
    }

    /**
     * 根据条件列表数据
     *
     * @param formUuid        表单id
     * @param searchFieldJson 查询条件
     * @return formData
     */
    public List<JSONObject> getFormDataList(String formUuid, String searchFieldJson) {
        int startPage = 1;
        int pageSize = 99;
        JSONObject paramsObject = new JSONObject();
        paramsObject.put("formUuid", formUuid);
        paramsObject.put("userId", managerUserId);
        paramsObject.put("pageSize", pageSize);
        paramsObject.put("searchFieldJson", searchFieldJson);
        paramsObject.put("currentPage", startPage);
        log.info("searchForm: {}", paramsObject.toJSONString());
        List<JSONObject> dataList = new ArrayList<>();
        try {
            while (true) {
                paramsObject.put("currentPage", startPage);
                String respStr = yiDaClient.searchForm(paramsObject);
                log.info("respStr: {}", respStr);
                JSONObject respObj = JSONObject.parseObject(respStr);
                JSONArray respData = respObj.getJSONArray("data");
                dataList.addAll(respData.toJavaList(JSONObject.class));
                if (startPage * pageSize > respObj.getInteger("totalCount")) {
                    break;
                }
                startPage++;
            }
            return dataList;
        } catch (Exception e) {
            return getFormDataList(formUuid, searchFieldJson);
        }
    }

    /**
     * 根据条件列表数据
     *
     * @param formUuid        表单id
     * @param searchFieldJson 查询条件
     * @return formData
     */
    public List<JSONObject> getFormDataListByUser(String formUuid, String searchFieldJson, String dingUserId) {
        int startPage = 1;
        int pageSize = 99;
        JSONObject paramsObject = new JSONObject();
        paramsObject.put("formUuid", formUuid);
        paramsObject.put("userId", dingUserId);
        paramsObject.put("pageSize", pageSize);
        paramsObject.put("searchFieldJson", searchFieldJson);
        paramsObject.put("currentPage", startPage);
        log.info("searchForm: {}", paramsObject.toJSONString());
        List<JSONObject> dataList = new ArrayList<>();
        try {
            while (true) {
                paramsObject.put("currentPage", startPage);
                String respStr = yiDaClient.searchForm(paramsObject);
                log.info("respStr: {}", respStr);
                JSONObject respObj = JSONObject.parseObject(respStr);
                JSONArray respData = respObj.getJSONArray("data");
                dataList.addAll(respData.toJavaList(JSONObject.class));
                if (startPage * pageSize > respObj.getInteger("totalCount")) {
                    break;
                }
                startPage++;
            }
            return dataList;
        } catch (Exception e) {
            return getFormDataListByUser(formUuid, searchFieldJson, dingUserId);
        }
    }

    /**
     * 根据条件列表数据
     *
     * @param formUuid        表单id
     * @param searchFieldJson 查询条件
     * @return formData
     */
    public List<JSONObject> getFormDataList(String formUuid, String searchFieldJson, String dynamicOrder) {
        int startPage = 1;
        int pageSize = 99;
        JSONObject paramsObject = new JSONObject();
        paramsObject.put("formUuid", formUuid);
        paramsObject.put("userId", managerUserId);
        paramsObject.put("pageSize", pageSize);
        paramsObject.put("searchFieldJson", searchFieldJson);
        paramsObject.put("currentPage", startPage);
        paramsObject.put("dynamicOrder", dynamicOrder);
        log.info("searchForm: {}", paramsObject.toJSONString());
        List<JSONObject> dataList = new ArrayList<>();
        try {
            while (true) {
                paramsObject.put("currentPage", startPage);
                String respStr = yiDaClient.searchForm(paramsObject);
                log.info("respStr: {}", respStr);
                JSONObject respObj = JSONObject.parseObject(respStr);
                JSONArray respData = respObj.getJSONArray("data");
                dataList.addAll(respData.toJavaList(JSONObject.class));
                if (startPage * pageSize > respObj.getInteger("totalCount")) {
                    break;
                }
                startPage++;
            }
            return dataList;
        } catch (Exception e) {
            return getFormDataList(formUuid, searchFieldJson);
        }
    }

    /**
     * 新增表单数据
     *
     * @param formUUid     表单id
     * @param formDataJson 数据
     */
    public String createForm(String formUUid, String formDataJson) {
        String result = yiDaClient.createForm(formUUid, formDataJson, managerUserId);
        log.info("createForm===>result: {} formUUid: {} formDataJson：{}", result, formUUid, formDataJson);
        if (StringUtils.isBlank(JSON.parseObject(result).getString("result"))) {
            throw new HttpException(JSON.parseObject(result).getString("message"));
        }
        return JSON.parseObject(result).getString("result");
    }

    /**
     * 新增表单数据
     *
     * @param formUUid     表单id
     * @param formDataJson 数据
     * @param ddUserId     钉钉用户id
     */
    public String createFormByUser(String formUUid, String formDataJson, String ddUserId) {
        String result = yiDaClient.createForm(formUUid, formDataJson, ddUserId);
        log.info("createForm===>result: {} formUUid: {}", result, formUUid);
        if (StringUtils.isBlank(JSON.parseObject(result).getString("result"))) {
            throw new HttpException(JSON.parseObject(result).getString("message"));
        }
        return JSON.parseObject(result).getString("result");
    }

    /**
     * 更新表单数据
     *
     * @param formInstanceId 实例id
     * @param formDataJson   数据
     */
    public void updateForm(String formInstanceId, String formDataJson) {
        try {
            String result = yiDaClient.updateForm(formInstanceId, formDataJson, managerUserId);
            log.info("updateForm===>result: {} formInstanceId: {}", result, formInstanceId);
        } catch (Exception e) {
            updateForm(formInstanceId, formDataJson);
        }
    }

    /**
     * 删除表单数据
     *
     * @param formInstanceId 表单实例id
     */
    public void deleteForm(String formInstanceId) {
        if (StringUtils.isBlank(formInstanceId)) {
            return;
        }
        try {
            String result = yiDaClient.deleteForm(formInstanceId, managerUserId);
            log.info("deleteForm===>result: {} formInstanceId: {}", result, formInstanceId);
        } catch (Exception e) {
            deleteForm(formInstanceId);
        }
    }

    /**
     * 删除表单数据
     *
     * @param formInstanceId 表单实例id
     */
    public void deleteFormByUser(String formInstanceId, String dingUserId) {
        if (StringUtils.isBlank(formInstanceId)) {
            return;
        }
        try {
            String result = yiDaClient.deleteForm(formInstanceId, dingUserId);
            log.info("deleteForm===>result: {} formInstanceId: {}", result, formInstanceId);
        } catch (Exception e) {
            deleteFormByUser(formInstanceId, dingUserId);
        }
    }

    /**
     * 新增流程数据
     *
     * @param formUUid     表单id
     * @param processCode  流程code
     * @param formDataJson 数据
     */
    public String createProcess(String formUUid, String processCode, String formDataJson) {
        String result = yiDaClient.createProcess(formUUid, processCode, formDataJson, managerUserId);
        log.info("createProcess===>result: {} formUUid: {} processCode：{} formDataJson：{}", result, formUUid, processCode, formDataJson);
        if (StringUtils.isBlank(JSON.parseObject(result).getString("result"))) {
            throw new HttpException(JSON.parseObject(result).getString("message"));
        }
        return JSON.parseObject(result).getString("result");
    }

    /**
     * 通过高级查询条件获取表单实例数据（包括子表单组件数据）
     */
    public Integer getFormEqOrLikeDataListCount(String formUuid, List<JSONObject> searchCondition) {
        int startPage = 1;
        int pageSize = 1;
        JSONObject paramsObject = new JSONObject();
        paramsObject.put("formUuid", formUuid);
        paramsObject.put("userId", managerUserId);
        paramsObject.put("pageSize", pageSize);
        paramsObject.put("pageNumber", startPage);
        paramsObject.put("searchCondition", searchCondition.toString());
        paramsObject.put("appType", YiDaConfig.getDingTalkYiDaConfig().getString("appType"));
        paramsObject.put("systemToken", YiDaConfig.getDingTalkYiDaConfig().getString("systemToken"));
        log.info("searchForm: {}", paramsObject.toJSONString());
        String respStr = this.request("/v1.0/yida/forms/instances/advances/queryAll", Method.POST, paramsObject.toJSONString());
        log.info("respStr: {}", respStr);
        JSONObject respObj = JSONObject.parseObject(respStr);
        return respObj.getInteger("totalCount");
    }

    /**
     * 通过高级查询条件获取表单实例数据（包括子表单组件数据）
     */
    public JSONObject getFormEqOrLikeDataPage(String formUuid, int startPage, int pageSize, List<JSONObject> searchCondition) {
        JSONObject paramsObject = new JSONObject();
        paramsObject.put("formUuid", formUuid);
        paramsObject.put("userId", managerUserId);
        paramsObject.put("pageSize", pageSize);
        paramsObject.put("pageNumber", startPage);
        paramsObject.put("searchCondition", searchCondition.toString());
        paramsObject.put("appType", YiDaConfig.getDingTalkYiDaConfig().getString("appType"));
        paramsObject.put("systemToken", YiDaConfig.getDingTalkYiDaConfig().getString("systemToken"));
        log.info("searchForm: {}", paramsObject.toJSONString());
        String respStr = this.request("/v1.0/yida/forms/instances/advances/queryAll", Method.POST, paramsObject.toJSONString());
        log.info("respStr: {}", respStr);
        return JSONObject.parseObject(respStr);
    }

    /**
     * 通过高级查询条件获取表单实例数据（包括子表单组件数据）
     */
    public List<JSONObject> getFormEqOrLikeDataList(String formUuid, List<JSONObject> searchCondition) {
        int startPage = 1;
        int pageSize = 99;
        JSONObject paramsObject = new JSONObject();
        paramsObject.put("formUuid", formUuid);
        paramsObject.put("userId", managerUserId);
        paramsObject.put("pageNumber", startPage);
        paramsObject.put("pageSize", pageSize);
        paramsObject.put("searchCondition", searchCondition.toString());
        paramsObject.put("appType", YiDaConfig.getDingTalkYiDaConfig().getString("appType"));
        paramsObject.put("systemToken", YiDaConfig.getDingTalkYiDaConfig().getString("systemToken"));
        log.info("searchForm: {}", paramsObject.toJSONString());
        List<JSONObject> dataList = new ArrayList<>();
        try {
            while (true) {
                paramsObject.put("pageNumber", startPage);
                String respStr = this.request("/v2.0/yida/forms/instances/advances/queryAll", Method.POST, paramsObject.toJSONString());
                log.info("respStr: {}", respStr);
                JSONObject respObj = JSONObject.parseObject(respStr);
                JSONArray respData = respObj.getJSONArray("data");
                dataList.addAll(respData.toJavaList(JSONObject.class));
                if (startPage * pageSize > respObj.getInteger("totalCount")) {
                    break;
                }
                startPage++;
            }
            return dataList;
        } catch (Exception e) {
            return getFormEqOrLikeDataList(formUuid, searchCondition);
        }
    }/**
     * 获取子表组件数据
     */
    public List<JSONObject> getTableData(String formInstanceId, String formUuid, String tableFieldId) {
        String url = YiDaConfig.getDingTalkYiDaConfig().getString("domain") + "/v1.0/yida/forms/innerTables/" + formInstanceId;
        int startPage = 1;
        int pageSize = 50;
        JSONObject paramsObject = new JSONObject();
        paramsObject.put("formUuid", formUuid);
        paramsObject.put("tableFieldId", tableFieldId);
        paramsObject.put("pageNumber", startPage);
        paramsObject.put("pageSize", pageSize);
        paramsObject.put("systemToken", YiDaConfig.getDingTalkYiDaConfig().getString("systemToken"));
        paramsObject.put("userId", managerUserId);
        paramsObject.put("appType", YiDaConfig.getDingTalkYiDaConfig().getString("appType"));
        log.info("searchForm: {}", paramsObject.toJSONString());
        List<JSONObject> dataList = new ArrayList<>();
        try {
            while (true) {
                paramsObject.put("pageNumber", startPage);
                HttpRequest request = HttpRequest.get(url)
                        .header("x-acs-dingtalk-access-token", this.getToken())
                        .form(paramsObject);
                HttpResponse response = request.execute();
                String respStr = response.body();
                log.info("respStr: {}", respStr);
                JSONObject respObj = JSONObject.parseObject(respStr);
                JSONArray respData = respObj.getJSONArray("data");
                dataList.addAll(respData.toJavaList(JSONObject.class));
                if (startPage * pageSize > respObj.getInteger("totalCount")) {
                    break;
                }
                startPage++;
            }
            return dataList;
        } catch (Exception e) {
            return getTableData(formInstanceId, formUuid, tableFieldId);
        }
    }

    private String request(String url, Method method, String body) {
        url = YiDaConfig.getDingTalkYiDaConfig().getString("domain") + url;
        HttpRequest request = HttpRequest.of(url);
        String responseBody = request.method(method).timeout(30000)
                .header("x-acs-dingtalk-access-token", this.getToken())
                .body(body)
                .contentType("application/json")
                .charset(StandardCharsets.UTF_8)
                .execute().body();
        if (!JSONValidator.from(responseBody).validate()) {
            return responseBody;
        }
        JSONObject respObj = JSON.parseObject(responseBody);
        if (respObj.containsKey("code") && retryCodeList.contains(respObj.getString("code"))) {
            log.error("钉钉宜搭接口请求错误，执行重试，重试次数：1，responseBody: {}", responseBody);
            return this.request(url, method, body, 1);
        }
        return responseBody;
    }

    private String request(String url, Method method, String body, Integer retryCount) {
        HttpRequest request = HttpRequest.of(url);
        String responseBody = request.method(method).timeout(30000)
                .header("x-acs-dingtalk-access-token", this.getToken())
                .body(body)
                .contentType("application/json")
                .charset(StandardCharsets.UTF_8)
                .execute().body();
        if (!JSONValidator.from(responseBody).validate()) {
            return responseBody;
        }
        JSONObject respObj = JSON.parseObject(responseBody);
        if (respObj.containsKey("code") && retryCodeList.contains(respObj.getString("code"))) {
            if (retryCount >= 5) {
                log.error("钉钉宜搭接口请求错误，重试次数达到上限，responseBody: {}", responseBody);
                return responseBody;
            }
            log.error("钉钉宜搭接口请求错误，执行重试，重试次数：{}，responseBody: {}", retryCount + 1, responseBody);
            return this.request(url, method, body, retryCount + 1);
        }
        return responseBody;
    }

    public String getToken() {
        RedisKeys redisKeys = new RedisKeys();
        RedisClient redisClient = new RedisClient();
        String appKey = YiDaConfig.getDingTalkConfig().getString("appKey");
        String appSecret = YiDaConfig.getDingTalkConfig().getString("appSecret");

        Object accessToken = redisClient.get(redisKeys.dingTalkAccessToken(appKey));
        if (ObjectUtils.isNotEmpty(accessToken)) {
            return accessToken.toString();
        } else {
            String url = String.format("https://oapi.dingtalk.com/gettoken?appkey=%s&appsecret=%s", appKey, appSecret);
            String body = HttpRequest.get(url).execute().body();
            JSONObject respObj = JSON.parseObject(body);
            if (respObj.getInteger("errcode") != 0) {
                throw new HttpException("获取钉钉access_token错误: " + respObj.getString("errmsg"));
            } else {
                redisClient.set(redisKeys.dingTalkAccessToken(appKey), respObj.getString("access_token"), respObj.getLongValue("expires_in") - 10L);
                return respObj.getString("access_token");
            }
        }
    }

    /**
     * 附件处理，获取临时免登附件地址
     */
    public String getAttachmentUrl(JSONObject yiDaAttachment) {
        String responseBody = yiDaClient.temporaryUrls(managerUserId, URLEncoder.encode("https://www.aliwork.com" + yiDaAttachment.getString("downloadUrl"), StandardCharsets.UTF_8));
        log.info("yiDaResponseBody: {}", responseBody);
        JSONObject responseObj = JSON.parseObject(responseBody);
        return responseObj.getString("result");
    }
}