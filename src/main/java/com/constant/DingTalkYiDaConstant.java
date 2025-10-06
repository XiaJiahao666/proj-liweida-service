package com.constant;

public interface DingTalkYiDaConstant {

    /**
     * 查询表单数据
     */
    String SEARCH_FORM = "/v1.0/yida/forms/instances/search";

    /**
     * 获取表单详情
     */
    String GET_FORM = "/v1.0/yida/forms/instances";

    /**
     * 保存表单数据
     */
    String CREATE_FORM = "/v1.0/yida/forms/instances";

    /**
     * 更新表单数据
     */
    String UPDATE_FORM = "/v1.0/yida/forms/instances";

    /**
     * 删除表单数据
     */
    String DELETE_FORM = "/v1.0/yida/forms/instances";

    /**
     * 获取流程详情
     */
    String GET_PROCESS = "/v1.0/yida/processes/instancesInfos";

    /**
     * 获取流程设计的节点信息
     */
    String GET_PROCESS_ACTIVITIES = "/v1.0/yida/processes/activities";

    /**
     * 查询流程运行任务
     */
    String GET_PROCESS_RUNNING_TASK = "/v1.0/yida/processes/tasks/getRunningTasks";

    /**
     * 发起宜搭审批流程
     */
    String CREATE_PROCESS = "/v1.0/yida/processes/instances/start";

    /**
     * 更新宜搭流程实例
     */
    String UPDATE_PROCESS = "/v1.0/yida/processes/instances";

    /**
     * 执行审批任务
     */
    String EXECUTE_TASK = "/v1.0/yida/tasks/execute";

    /**
     * 删除宜搭流程实例
     */
    String DELETE_PROCESS = "/v1.0/yida/processes/instances";

    /**
     * 终止宜搭流程实例
     */
    String TERMINATE_PROCESS = "/v1.0/yida/processes/instances/terminate";

    /**
     * 获取附件临时免登地址
     */
    String TEMPORARY_URLS = "/v1.0/yida/apps/temporaryUrls/%s";

    /**
     * 通过高级查询条件获取表单实例数据（包括子表单组件数据）
     */
    String SEARCH_FORM_EqOrLike = "/v1.0/yida/forms/instances/advances/queryAll";

    /**
     * 通过表单实例数据批量更新表单实例
     */
    String FORMS_INSTANCES_DATAS = "/v1.0/yida/forms/instances/datas";
}
