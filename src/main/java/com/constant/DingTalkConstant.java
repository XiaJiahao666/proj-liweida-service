package com.constant;

public interface DingTalkConstant {

    /**
     * 请求域名
     */
    String DOMAIN = "https://oapi.dingtalk.com";

    /**
     * 获取企业内部应用access_token
     */
    String GET_TOKEN = DOMAIN + "/gettoken";

    /**
     * 获取应用列表
     */
    String GET_APP_LIST = DOMAIN + "/microapp/list";

    /**
     * 获取员工可见的应用列表
     */
    String GET_APP_LIST_BY_USERID = DOMAIN + "/microapp/list_by_userid";

    /**
     * 获取企业所有应用列表
     */
    String GET_ALL_APP_LIST = "https://api.dingtalk.com/v1.0/microApp/allApps";

    /**
     * 获取用户信息（应用免登）
     */
    String GET_USER_INFO = DOMAIN + "/topapi/v2/user/getuserinfo";

    /**
     * 根据手机号获取用户id
     */
    String GET_USER_ID_BY_MOBILE = DOMAIN + "/topapi/v2/user/getbymobile";

    String GET_MANAGER_INFO = DOMAIN + "/sso/getuserinfo";

    /**
     * 获取用户详情
     */
    String GET_USER = DOMAIN + "/topapi/v2/user/get";

    /**
     * 获取部门列表
     */
    String GET_DEPARTMENT_LIST = DOMAIN + "/topapi/v2/department/listsub";

    /**
     * 获取部门用户列表
     */
    String GET_USER_LIST = DOMAIN + "/topapi/v2/user/list";

    /**
     * 更新用户信息
     */
    String UPDATE_USER = DOMAIN + "/topapi/v2/user/update";

    /**
     * 发起审批实例
     */
    String CREATE_PROCESS_INSTANCE = DOMAIN + "/topapi/processinstance/create";

    /**
     * 获取审批实例详情
     */
    String GET_PROCESS_INSTANCE = DOMAIN + "/topapi/processinstance/get";

    /**
     * 获取审批实例附件地址
     */
    String GET_PROCESS_FILE = DOMAIN + "/topapi/processinstance/file/url/get";

    /**
     * 创建角色组
     */
    String CREATE_ROLE_GROUP = DOMAIN + "/role/add_role_group";

    /**
     * 创建角色
     */
    String CREATE_ROLE = DOMAIN + "/role/add_role";

    /**
     * 获取角色列表
     */
    String GET_ROLE_LIST = DOMAIN + "/topapi/role/list";

    /**
     * 获取用户可查看的公告
     */
    String GET_NOTICE_LIST_BY_USERID = DOMAIN + "/topapi/blackboard/listtopten";

    /**
     * 获取公告详情
     */
    String GET_NOTICE_INFO = DOMAIN + "/topapi/blackboard/get";

    /**
     * 获取公告分类列表
     */
    String GET_NOTICE_TYPE_LIST = DOMAIN + "/topapi/blackboard/category/list";

    /**
     * 获取考勤报表列定义
     */
    String GET_ATTENDANCE_COLUMNS = DOMAIN + "/topapi/attendance/getattcolumns";

    /**
     * 获取考勤报表列值
     */
    String GET_ATTENDANCE_COLUMNS_VALUE = DOMAIN + "/topapi/attendance/getcolumnval";

    /**
     * 获取报表假期数据
     */
    String GET_LEAVE_TIME_VALUE = DOMAIN + "/topapi/attendance/getleavetimebynames";

    /**
     * 查询企业下用户待办列表
     */
    String GET_TASK_LIST = "https://api.dingtalk.com/v1.0/todo/users/{unionId}/org/tasks/query";

    /**
     * 添加待入职员工
     */
    String CREATE_EMPLOYEE = DOMAIN + "/topapi/smartwork/hrm/employee/addpreentry";

    /**
     * 获取员工花名册字段信息
     */
    String GET_SMARTWORK_HRM_EMPLOYEE = DOMAIN + "/topapi/smartwork/hrm/employee/v2/list";

    /**
     * 更新员工花名册信息
     */
    String UPDATE_SMARTWORK_HRM_EMPLOYEE = DOMAIN + "/topapi/smartwork/hrm/employee/v2/update";

    /**
     * 获取员工字段组详情
     */
    String GET_SMARTWORK_FIELD_GROUP = DOMAIN + "/topapi/smartwork/hrm/employee/field/grouplist";

    /**
     * 获取职位列表
     */
    String GET_POSITIONS = "https://api.dingtalk.com/v1.0/hrm/positions/query";

    /**
     * 获取职级列表
     */
    String GET_JOB_RANKS = "https://api.dingtalk.com/v1.0/hrm/jobRanks";

    /**
     * 获取职务列表
     */
    String GET_JOBS = "https://api.dingtalk.com/v1.0/hrm/jobs";

    /**
     * 发送工作通知消息
     */
    String SEND_NOTICE_MESSAGE = DOMAIN + "/topapi/message/corpconversation/asyncsend_v2";

    /**
     * 上传考勤记录
     */
    String UPLOAD_ATTENDANCE_RECORD = DOMAIN + "/topapi/attendance/record/upload";

    /**
     * 查询假期规则列表
     */
    String GET_VACATION_TYPE_LIST = DOMAIN + "/topapi/attendance/vacation/type/list";

    /**
     * 查询假期余额
     */
    String GET_VACATION_QUATO_LIST = DOMAIN + "/topapi/attendance/vacation/quota/list";

    /**
     * 批量发送人与机器人会话中机器人消息
     */
    String ROBOT_OTOMESSAGE_BATCH_SEND = "https://api.dingtalk.com/v1.0/robot/oToMessages/batchSend";
}
