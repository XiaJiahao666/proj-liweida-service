package com.client;

import cn.hutool.core.date.DateTime;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONValidator;
import com.config.DingTalkConfig;
import com.constant.DingTalkConstant;
import com.dto.DingTalkUserVo;
import com.redis.RedisKeys;
import com.utils.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class DingTalkClient {

    private final DingTalkConfig config;

    public DingTalkClient(DingTalkConfig config) {
        this.config = config;
    }

    /**
     * 获取access_token
     *
     * @return access_token
     */
    public String getToken() {
        RedisClient redisClient = SpringContextUtils.getBean(RedisClient.class);
        RedisKeys redisKeys = SpringContextUtils.getBean(RedisKeys.class);
        String redisKey = redisKeys.dingTalkAccessToken(this.config.getAppKey());
        Object accessToken = redisClient.get(redisKey);
        if (ObjectUtils.isNotEmpty(accessToken)) {
            return accessToken.toString();
        }
        String url = String.format(DingTalkConstant.GET_TOKEN + "?appkey=%s&appsecret=%s", config.getAppKey(), config.getAppSecret());
        String body = HttpRequest.get(url).execute().body();
        JSONObject respObj = JSON.parseObject(body);
        if (respObj.getInteger("errcode") != 0) {
            throw new HttpException("获取钉钉access_token错误: " + respObj.getString("errmsg"));
        }
        return respObj.getString("access_token");
    }

    /**
     * 获取应用列表
     *
     * @return appList
     */
    public JSONObject getAppList() {
        String accessToken = this.getToken();
        String url = String.format(DingTalkConstant.GET_APP_LIST + "?access_token=%s", accessToken);
        String body = HttpRequest.post(url).execute().body();
        return JSON.parseObject(body);
    }

    /**
     * 获取员工可见的应用列表
     *
     * @return appList
     */
    public JSONObject getAppListByUserId(String dingUserId) {
        String accessToken = this.getToken();
        String url = String.format(DingTalkConstant.GET_APP_LIST_BY_USERID + "?access_token=%s&userid=%s", accessToken, dingUserId);
        String body = HttpRequest.post(url).execute().body();
        return JSON.parseObject(body);
    }

    /**
     * 获取企业所有应用列表
     *
     * @return appList
     */
    public JSONObject getALLAppList() {
        String url = DingTalkConstant.GET_ALL_APP_LIST;
        String body = this.request(url, Method.GET, null);
        return JSON.parseObject(body);
    }

    /**
     * 获取部门用户列表
     *
     * @param userList 用户列表数据
     * @param cursor   分页查询的游标，最开始传0，后续传返回参数中的next_cursor值。
     * @param deptId   部门ID，根部门ID为1。
     */
    public void getUserList(List<DingTalkUserVo> userList, Long cursor, Long deptId) {
        String accessToken = this.getToken();
        String url = String.format(DingTalkConstant.GET_USER_LIST + "?access_token=%s", accessToken);
        JSONObject requestObj = new JSONObject();
        requestObj.put("dept_id", deptId);
        requestObj.put("cursor", cursor);
        requestObj.put("size", 100);
        String body = HttpRequest.post(url)
                .body(requestObj.toJSONString(), "application/json;charset=utf-8")
                .execute().body();
        JSONObject respObj = JSONObject.parseObject(body);
        if (respObj.getInteger("errcode") != 0) {
            throw new HttpException("获取部门用户列表错误: " + respObj.toJSONString());
        }
        JSONObject resultObj = respObj.getJSONObject("result");
        userList.addAll(resultObj.getJSONArray("list").toJavaList(DingTalkUserVo.class));
        if (resultObj.getBoolean("has_more")) {
            this.getUserList(userList, resultObj.getLong("next_cursor"), deptId);
        }
    }

    /**
     * 获取用户信息
     *
     * @param code code
     * @return userinfo
     */
    public JSONObject getUserInfo(String code) {
        String accessToken = this.getToken();
        String url = String.format(DingTalkConstant.GET_USER_INFO + "?access_token=%s&code=%s", accessToken, code);
        String body = HttpRequest.get(url).execute().body();
        JSONObject respObj = JSON.parseObject(body);
        if (respObj.getInteger("errcode") != 0) {
            throw new HttpException("获取钉钉用户错误: " + respObj.getString("errmsg"));
        }
        return respObj.getJSONObject("result");
    }

    public String getUserIdByMobile(String mobile) {
        String accessToken = this.getToken();
        String url = String.format(DingTalkConstant.GET_USER_ID_BY_MOBILE + "?access_token=%s", accessToken);
        JSONObject params = new JSONObject();
        params.put("mobile", mobile);
        String body = HttpRequest.post(url).body(params.toJSONString()).execute().body();
        JSONObject respObj = JSON.parseObject(body);
        if (respObj.getInteger("errcode") != 0) {
            throw new HttpException("获取钉钉用户id错误: " + respObj.getString("errmsg"));
        }
        return respObj.getJSONObject("result").getString("userid");
    }

    /**
     * 根据钉钉用户id获取用户详情
     *
     * @param dingUserId 钉钉用户id
     * @return userInfo
     */
    public JSONObject getUser(String dingUserId) {
        String accessToken = this.getToken();
        String url = String.format(DingTalkConstant.GET_USER + "?access_token=%s", accessToken);
        JSONObject params = new JSONObject();
        params.put("userid", dingUserId);
        String body = HttpRequest.post(url).body(params.toJSONString(), "application/json;charset=utf-8").execute().body();
        JSONObject respObj = JSON.parseObject(body);
        if (respObj.getInteger("errcode") != 0) {
            throw new HttpException("获取钉钉用户详情错误: " + respObj.getString("errmsg"));
        }
        return respObj.getJSONObject("result");
    }

    /**
     * 更新用户信息
     *
     * @param dingUserId 钉钉用户id
     * @param params     参数
     */
    public void updateUser(String dingUserId, JSONObject params) {
        String accessToken = this.getToken();
        String url = String.format(DingTalkConstant.UPDATE_USER + "?access_token=%s", accessToken);
        params.put("userid", dingUserId);
        String body = HttpRequest.post(url).body(params.toJSONString(), "application/json;charset=utf-8").execute().body();
        JSONObject respObj = JSON.parseObject(body);
        if (respObj.getInteger("errcode") != 0) {
            throw new HttpException("更新用户信息错误: " + respObj.getString("errmsg"));
        }
    }

    /**
     * 获取钉钉实例详情
     *
     * @param processInstanceId 实例id
     * @return respObj
     */
    public JSONObject getProcessInstance(String processInstanceId) {
        String accessToken = this.getToken();
        String url = String.format(DingTalkConstant.GET_PROCESS_INSTANCE + "?access_token=%s", accessToken);
        JSONObject params = new JSONObject();
        params.put("process_instance_id", processInstanceId);
        String body = HttpRequest.post(url).body(params.toJSONString(), "application/json;charset=utf-8").execute().body();
        return JSON.parseObject(body);
    }

    /**
     * 获取审批实例附件下载地址
     *
     * @param processInstanceId 实例id
     * @param fileId            文件id
     * @return result   file_id: 文件id, space_id: 文件spaceId, download_uri: 文件下载地址
     */
    public JSONObject getProcessFile(String processInstanceId, String fileId) {
        String accessToken = this.getToken();
        String url = String.format(DingTalkConstant.GET_PROCESS_FILE + "?access_token=%s", accessToken);
        JSONObject params = new JSONObject();
        params.put("process_instance_id", processInstanceId);
        params.put("file_id", fileId);
        JSONObject request = new JSONObject();
        request.put("request", params);
        String body = HttpRequest.post(url).body(request.toJSONString(), "application/json;charset=utf-8").execute().body();
        JSONObject respObj = JSON.parseObject(body);
        if (respObj.getInteger("errcode") != 0) {
            throw new HttpException("获取附件下载地址错误: " + respObj.getString("errmsg"));
        }
        return respObj.getJSONObject("result");
    }

    /**
     * 创建角色组
     *
     * @param name 名称
     * @return groupId
     */
    public Long createRoleGroup(String name) {
        String accessToken = this.getToken();
        String url = String.format(DingTalkConstant.CREATE_ROLE_GROUP + "?access_token=%s", accessToken);
        JSONObject params = new JSONObject();
        params.put("name", name);
        String body = HttpRequest.post(url).body(params.toJSONString(), "application/json;charset=utf-8").execute().body();
        JSONObject respObj = JSON.parseObject(body);
        if (respObj.getInteger("errcode") != 0) {
            log.error("创建钉钉角色组错误: {}", respObj.getString("errmsg"));
            throw new HttpException("创建钉钉角色组错误: " + respObj.getString("errmsg"));
        }
        return respObj.getLong("groupId");
    }

    /**
     * 创建角色
     *
     * @param name 名称
     * @return groupId
     */
    public Long createRole(String name, Long groupId) {
        String accessToken = this.getToken();
        String url = String.format(DingTalkConstant.CREATE_ROLE + "?access_token=%s", accessToken);
        JSONObject params = new JSONObject();
        params.put("roleName", name);
        params.put("groupId", groupId);
        String body = HttpRequest.post(url).body(params.toJSONString(), "application/json;charset=utf-8").execute().body();
        JSONObject respObj = JSON.parseObject(body);
        if (respObj.getInteger("errcode") != 0) {
            log.error("创建钉钉角色错误: {}", respObj.getString("errmsg"));
            throw new HttpException("创建钉钉角色错误: " + respObj.getString("errmsg"));
        }
        return respObj.getLong("roleId");
    }

    /**
     * 获取角色列表
     *
     * @return result
     */
    public JSONObject getRoleList() {
        String accessToken = this.getToken();
        String url = String.format(DingTalkConstant.GET_ROLE_LIST + "?access_token=%s", accessToken);
        String body = HttpRequest.post(url).execute().body();
        JSONObject respObj = JSON.parseObject(body);
        if (respObj.getInteger("errcode") != 0) {
            throw new HttpException("获取角色列表错误: " + respObj.getString("errmsg"));
        }
        return respObj.getJSONObject("result");
    }

    /**
     * 获取用户可查看的公告
     *
     * @param dingUserId 钉钉用户id
     * @return result
     */
    public JSONObject getNoticeList(String dingUserId) {
        String accessToken = this.getToken();
        String url = String.format(DingTalkConstant.GET_NOTICE_LIST_BY_USERID + "?access_token=%s", accessToken);
        JSONObject params = new JSONObject();
        params.put("userid", dingUserId);
        String body = HttpRequest.post(url).body(params.toJSONString(), "application/json;charset=utf-8").execute().body();
        JSONObject respObj = JSON.parseObject(body);
        if (respObj.getInteger("errcode") != 0) {
            throw new HttpException("获取用户可查看的公告错误: " + respObj.getString("errmsg"));
        }
        return respObj;
    }

    /**
     * 获取公告详情
     *
     * @param dingUserId 钉钉用户id
     * @return result
     */
    public JSONObject getNoticeInfo(String dingUserId, String noticeId) {
        String accessToken = this.getToken();
        String url = String.format(DingTalkConstant.GET_NOTICE_INFO + "?access_token=%s", accessToken);
        JSONObject params = new JSONObject();
        params.put("blackboard_id", noticeId);
        params.put("operation_userid", dingUserId);
        String body = HttpRequest.post(url).body(params.toJSONString(), "application/json;charset=utf-8").execute().body();
        JSONObject respObj = JSON.parseObject(body);
        if (respObj.getInteger("errcode") != 0) {
            throw new HttpException("获取公告详情错误: " + respObj.getString("errmsg"));
        }
        return respObj.getJSONObject("result");
    }

    /**
     * 获取公告分类列表
     *
     * @param dingUserId 操作人userid，必须是公告管理员
     * @return result
     */
    public JSONArray getNoticeTypeList(String dingUserId) {
        String accessToken = this.getToken();
        String url = String.format(DingTalkConstant.GET_NOTICE_TYPE_LIST + "?access_token=%s", accessToken);
        JSONObject params = new JSONObject();
        params.put("operation_userid", dingUserId);
        String body = HttpRequest.post(url).body(params.toJSONString(), "application/json;charset=utf-8").execute().body();
        JSONObject respObj = JSON.parseObject(body);
        if (respObj.getInteger("errcode") != 0) {
            throw new HttpException("获取公告分类列表错误: " + respObj.getString("errmsg"));
        }
        return respObj.getJSONArray("result");
    }

    /**
     * 获取考勤报表列定义
     *
     * @return result
     */
    public JSONObject getAttColumns() {
        String accessToken = this.getToken();
        String url = String.format(DingTalkConstant.GET_ATTENDANCE_COLUMNS + "?access_token=%s", accessToken);
        String body = HttpRequest.post(url).execute().body();
        JSONObject respObj = JSON.parseObject(body);
        if (respObj.getInteger("errcode") != 0) {
            throw new HttpException("获取勤报表列定义错误: " + respObj.getString("errmsg"));
        }
        return respObj.getJSONObject("result");
    }

    /**
     * 获取考勤报表列值
     *
     * @param dingUserId 用户的userid
     * @param columnIds  报表列ID，多值用英文逗号分隔
     * @param fromDate   开始时间
     * @param toDate     结束时间，结束时间减去开始时间必须在31天以内
     * @return result
     */
    public JSONObject getAttColumnsVal(String dingUserId, String columnIds, DateTime fromDate, DateTime toDate) {
        String accessToken = this.getToken();
        String url = String.format(DingTalkConstant.GET_ATTENDANCE_COLUMNS_VALUE + "?access_token=%s", accessToken);
        JSONObject params = new JSONObject();
        params.put("userid", dingUserId);
        params.put("column_id_list", columnIds);
        params.put("from_date", fromDate.toString());
        params.put("to_date", toDate.toString());
        String body = HttpRequest.post(url).body(params.toJSONString(), "application/json;charset=utf-8").execute().body();
        JSONObject respObj = JSON.parseObject(body);
        if (respObj.getInteger("errcode") != 0) {
            throw new HttpException("获取考勤报表列值错误: " + respObj.getString("errmsg"));
        }
        return respObj.getJSONObject("result");
    }

    /**
     * 获取报表假期数据
     *
     * @param dingUserId 用户的userid
     * @param leaveNames 假期名称，多个用英文逗号分隔
     * @param fromDate   开始时间
     * @param toDate     结束时间，结束时间减去开始时间必须在31天以内
     * @return result
     */
    public JSONObject getLeaveTimeByNames(String dingUserId, String leaveNames, DateTime fromDate, DateTime toDate) {
        String accessToken = this.getToken();
        String url = String.format(DingTalkConstant.GET_LEAVE_TIME_VALUE + "?access_token=%s", accessToken);
        JSONObject params = new JSONObject();
        params.put("userid", dingUserId);
        params.put("leave_names", leaveNames);
        params.put("from_date", fromDate.toString());
        params.put("to_date", toDate.toString());
        String body = HttpRequest.post(url).body(params.toJSONString(), "application/json;charset=utf-8").execute().body();
        JSONObject respObj = JSON.parseObject(body);
        if (respObj.getInteger("errcode") != 0) {
            throw new HttpException("获取报表假期数据错误: " + respObj.getString("errmsg"));
        }
        return respObj.getJSONObject("result");
    }

    /**
     * 查询企业下用户待办列表
     *
     * @param unionId   用户的unionId
     * @param nextToken 分页游标 说明 如果一个查询条件一次无法全部返回结果，会返回分页token，下次查询带上该token后会返回后续数据，直到分页token为null表示数据已经全部查询完毕
     * @param isDone    待办完成状态
     * @return taskList
     */
    public JSONArray getTaskList(String unionId, String nextToken, Boolean isDone, JSONArray taskList) {
        String url = DingTalkConstant.GET_TASK_LIST.replace("{unionId}", unionId);
        JSONObject params = new JSONObject();
        params.put("nextToken", nextToken);
        params.put("isDone", isDone);
        String body = this.request(url, Method.POST, params.toJSONString());
        JSONObject respObj = JSON.parseObject(body);
        JSONArray todoCards = respObj.getJSONArray("todoCards");
        taskList.addAll(todoCards);
        if (respObj.getString("nextToken") != null && !respObj.getString("nextToken").isEmpty()) {
            this.getTaskList(unionId, respObj.getString("nextToken"), isDone, taskList);
        }
        return taskList;
    }

    /**
     * 添加企业待入职员工
     *
     * @param name         姓名
     * @param mobile       手机号
     * @param preEntryTime 预期入职时间。2020-09-09 00:00:00
     * @param extendInfo   扩展信息 具体字段，查看钉钉开放平台 <a href="https://open.dingtalk.com/document/orgapp-server/add-employees-to-be-hired-through-intelligent-personnel">https://open.dingtalk.com/document/orgapp-server/add-employees-to-be-hired-through-intelligent-personnel</a>
     */
    public void createEmployee(String name, String mobile, String preEntryTime, JSONObject extendInfo) {
        String accessToken = this.getToken();
        String url = String.format(DingTalkConstant.CREATE_EMPLOYEE + "?access_token=%s", accessToken);
        JSONObject param = new JSONObject();
        param.put("name", name);
        param.put("mobile", mobile);
        if (StringUtils.isNotBlank(preEntryTime)) {
            param.put("pre_entry_time", preEntryTime);
        }
        param.put("extend_info", extendInfo.toJSONString());
        JSONObject params = new JSONObject();
        params.put("param", param);
        String body = HttpRequest.post(url).body(params.toJSONString(), "application/json;charset=utf-8").execute().body();
        JSONObject respObj = JSON.parseObject(body);
        if (respObj.getInteger("errcode") != 0) {
            throw new HttpException("添加待入职员工错误: " + respObj.getString("errmsg"));
        }
    }

    /**
     * 获取员工花名册字段信息
     *
     * @return appList
     */
    public JSONArray getSmartWorkEmployee(String userIdList, String fieldFilterList) {
        String accessToken = this.getToken();
        String url = String.format(DingTalkConstant.GET_SMARTWORK_HRM_EMPLOYEE + "?access_token=%s", accessToken);
        JSONObject param = new JSONObject();
        param.put("userid_list", userIdList);
        if (!fieldFilterList.isEmpty()) {
            param.put("field_filter_list", fieldFilterList);
        }
        param.put("agentid", config.getAgentId());
        String body = HttpRequest.post(url).body(param.toJSONString(), "application/json;charset=utf-8").execute().body();
        JSONObject respObj = JSON.parseObject(body);
        if (respObj.getInteger("errcode") != 0) {
            throw new HttpException("获取员工花名册字段信息错误: " + respObj.getString("errmsg"));
        }
        return respObj.getJSONArray("result");
    }

    /**
     * 更新员工花名册
     *
     * @param groups 修改的字段
     * @param userId 用户
     */
    public void updateSmartWorkEmployee(JSONArray groups, String userId) {
        String accessToken = this.getToken();
        String url = String.format(DingTalkConstant.UPDATE_SMARTWORK_HRM_EMPLOYEE + "?access_token=%s", accessToken);
        JSONObject param = new JSONObject();
        param.put("groups", groups);
        param.put("userid", userId);
        JSONObject params = new JSONObject();
        params.put("param", param);
        params.put("agentid", this.config.getAgentId());
        String body = HttpRequest.post(url).body(params.toJSONString(), "application/json;charset=utf-8").execute().body();
        JSONObject respObj = JSON.parseObject(body);
        if (respObj.getInteger("errcode") != 0) {
            throw new HttpException("更新员工花名册错误: " + respObj.getString("errmsg"));
        }
    }

    /**
     * 获取花名册字段组详情
     *
     * @return appList
     */
    public JSONArray getSmartWorkFiledGroup() {
        String accessToken = this.getToken();
        String url = String.format(DingTalkConstant.GET_SMARTWORK_FIELD_GROUP + "?access_token=%s", accessToken);
        JSONObject param = new JSONObject();
        param.put("agentid", config.getAgentId());
        String body = HttpRequest.post(url).body(param.toJSONString(), "application/json;charset=utf-8").execute().body();
        JSONObject respObj = JSON.parseObject(body);
        if (respObj.getInteger("errcode") != 0) {
            throw new HttpException("获取员工花名册字段组详情错误: " + respObj.getString("errmsg"));
        }
        return respObj.getJSONArray("result");
    }

    /**
     * 获取职位列表
     *
     * @param nextToken  分页游标 首次调用，该参数传0
     * @param maxResults 每页最大条目数，最大值200
     * @return positions
     */
    public List<JSONObject> getPositions(Integer nextToken, Integer maxResults) {
        String url = String.format(DingTalkConstant.GET_POSITIONS + "?nextToken=%s&maxResults=%s", nextToken, maxResults);
        String responseBody = this.request(url, Method.POST, null);
        JSONObject bodyObj = JSON.parseObject(responseBody);
        List<JSONObject> positions = bodyObj.getJSONArray("list").toJavaList(JSONObject.class);
        if (bodyObj.getBoolean("hasMore")) {
            positions.addAll(this.getPositions(bodyObj.getInteger("nextToken"), maxResults));
        }
        return positions;
    }

    /**
     * 获取职级列表
     *
     * @param nextToken  分页游标 首次调用，该参数传0
     * @param maxResults 每页最大条目数，最大值200
     * @return jobRanks
     */
    public List<JSONObject> getJobRanks(Integer nextToken, Integer maxResults) {
        String url = String.format(DingTalkConstant.GET_JOB_RANKS + "?nextToken=%s&maxResults=%s", nextToken, maxResults);
        String responseBody = this.request(url, Method.GET, null);
        JSONObject bodyObj = JSON.parseObject(responseBody);
        List<JSONObject> jobRanks = bodyObj.getJSONArray("list").toJavaList(JSONObject.class);
        if (bodyObj.getBoolean("hasMore")) {
            jobRanks.addAll(this.getJobRanks(bodyObj.getInteger("nextToken"), maxResults));
        }
        return jobRanks;
    }

    /**
     * 获取职务列表
     *
     * @param nextToken  分页游标 首次调用，该参数传0
     * @param maxResults 每页最大条目数，最大值200
     * @return jobs
     */
    public List<JSONObject> getJobs(Integer nextToken, Integer maxResults) {
        String url = String.format(DingTalkConstant.GET_JOBS + "?nextToken=%s&maxResults=%s", nextToken, maxResults);
        String responseBody = this.request(url, Method.GET, null);
        JSONObject bodyObj = JSON.parseObject(responseBody);
        List<JSONObject> jobs = bodyObj.getJSONArray("list").toJavaList(JSONObject.class);
        if (bodyObj.getBoolean("hasMore")) {
            jobs.addAll(this.getJobs(bodyObj.getInteger("nextToken"), maxResults));
        }
        return jobs;
    }

    /**
     * 发送工作通知消息
     *
     * @param msg        消息内容
     * @param userIdList 用户id
     * @param deptIdList 部门id
     * @param toAllUser  是否发送给企业全部用户
     * @return
     */
    public JSONObject sendNoticeMessage(JSONObject msg, List<String> userIdList, List<String> deptIdList, Boolean toAllUser) {
        String url = String.format(DingTalkConstant.SEND_NOTICE_MESSAGE + "?access_token=%s", this.getToken());
        JSONObject params = new JSONObject();
        params.put("agent_id", this.config.getAgentId());
        params.put("userid_list", StringUtils.join(userIdList, ","));
        if (!deptIdList.isEmpty()) {
            params.put("dept_id_list", StringUtils.join(deptIdList, ","));
        }
        params.put("to_all_user", toAllUser);
        params.put("msg", msg);
        try (HttpResponse response = HttpRequest.post(url)
                .body(params.toJSONString(), "application/json;charset=utf-8").execute()) {
            if (!response.isOk()) {
                throw new HttpException("发送工作通知失败，status: " + response.getStatus());
            }
            if (!JSONValidator.from(response.body()).validate()) {
                throw new HttpException("发送工作通知失败，body: " + response.body());
            }
            JSONObject respObj = JSON.parseObject(response.body());
            if (respObj.getInteger("errcode") != 0) {
                throw new HttpException("发送工作通知失败: " + respObj.getString("errmsg"));
            }
            return respObj;
        }
    }

    /**
     * 获取假期规则列表
     *
     * @param opUserid
     * @return
     */
    public JSONArray getVacationTypeList(String opUserid) {
        String url = String.format(DingTalkConstant.GET_VACATION_TYPE_LIST + "?access_token=%s", this.getToken());
        JSONObject params = new JSONObject();
        params.put("op_userid", opUserid);
        params.put("vacation_source", "all");
        try (HttpResponse response = HttpRequest.post(url)
                .body(JSONObject.toJSONString(params), "application/json;charset=utf-8").execute()) {
            if (!response.isOk()) {
                throw new HttpException("获取假期规则列表失败，status: " + response.getStatus());
            }
            if (!JSONValidator.from(response.body()).validate()) {
                throw new HttpException("获取假期规则列表失败，body: " + response.body());
            }
            JSONObject respObj = JSON.parseObject(response.body());
            if (respObj.getInteger("errcode") != 0) {
                log.info("=====获取假期规则列表失败====={}", JSONObject.toJSONString(respObj));
                throw new HttpException("获取假期规则列表失败: " + respObj.getString("errmsg"));
            }
            return respObj.getJSONArray("result");
        }
    }

    /**
     * 查询假期余额
     *
     * @param leaveCode
     * @param opUserId
     * @param userids
     */
    public JSONArray getVacationQuotaList(String leaveCode, String opUserId, String userids) {
        String url = String.format(DingTalkConstant.GET_VACATION_QUATO_LIST + "?access_token=%s", this.getToken());
        JSONObject params = new JSONObject();
        params.put("op_userid", opUserId);
        params.put("leave_code", leaveCode);
        params.put("userids", userids);
        params.put("offset", 0);
        params.put("size", 10);
        try (HttpResponse response = HttpRequest.post(url)
                .body(JSONObject.toJSONString(params), "application/json;charset=utf-8").execute()) {
            if (!response.isOk()) {
                throw new HttpException("查询假期余额失败，status: " + response.getStatus());
            }
            if (!JSONValidator.from(response.body()).validate()) {
                throw new HttpException("查询假期余额失败，body: " + response.body());
            }
            JSONObject respObj = JSON.parseObject(response.body());
            if (respObj.getInteger("errcode") != 0) {
                log.info("=====获取假期规则列表失败====={}", JSONObject.toJSONString(respObj));
                throw new HttpException("查询假期余额失败: " + respObj.getString("errmsg"));
            }
            return respObj.getJSONObject("result").getJSONArray("leave_quotas");
        }
    }

    /**
     * 批量发送人与机器人会话中机器人消息
     *
     * @param robotCode   机器人的编码，本接口只支持企业内部应用机器人调用，该参数使用企业内部应用机器人的robotCode。
     * @param dingUserIds 接收机器人消息的用户的userId列表。每次最多20个
     * @param msgKey      消息模板key。
     * @param msgParam    消息模板参数。
     * @return {
     * "processQueryKey" : "zcxxczasdafasd",  // 消息id，根据此id，可用于查询消息是否已读和撤回消息。
     * "invalidStaffIdList" : [ "manage25231" ], // 无效的用户userId列表。
     * "flowControlledStaffIdList" : [ "manage25232" ] // 被限流的userId列表。
     * }
     */
    public JSONObject robotOtomessageBatchSend(String robotCode, List<String> dingUserIds, String msgKey, String msgParam) {
        String url = DingTalkConstant.ROBOT_OTOMESSAGE_BATCH_SEND;
        JSONObject params = new JSONObject();
        params.put("robotCode", robotCode);
        params.put("userIds", dingUserIds);
        params.put("msgKey", msgKey);
        params.put("msgParam", msgParam);
        String body = this.request(url, Method.POST, params.toJSONString());
        return JSON.parseObject(body);
    }

    private String request(String url, Method method, String body) {
        HttpRequest request = HttpRequest.of(url);
        return request.method(method).timeout(30000)
                .header("x-acs-dingtalk-access-token", this.getToken())
                .body(body)
                .contentType("application/json")
                .charset(StandardCharsets.UTF_8)
                .execute().body();
    }
}
