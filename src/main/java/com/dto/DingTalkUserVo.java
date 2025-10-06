package com.dto;

import lombok.Data;

import java.util.List;

@Data
public class DingTalkUserVo {

    private String userid;

    private String unionid;

    private String name;

    private String avatar;

    /**
     * 手机号码。
     */
    private String mobile;

    /**
     * 员工工号。
     */
    private String jobNumber;

    /**
     * 职位。
     */
    private String title;

    /**
     * 所属部门ID列表。
     */
    private List<Long> deptIdList;

    /**
     * 入职时间
     */
    private Long hiredDate;

    /**
     * 是否激活了钉钉
     */
    private Boolean active;

    /**
     * 是否为企业的管理员
     */
    private Boolean admin;

    /**
     * 是否为企业的老板
     */
    private Boolean boss;

    /**
     * 是否是部门的主管
     */
    private Boolean leader;

    /**
     * 邮箱
     */
    private String email;

}
