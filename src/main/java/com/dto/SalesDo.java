package com.dto;

import lombok.Data;

import java.util.List;

@Data
public class SalesDo {

    /**
     * 单据类型
     */
    private String type;

    /**
     * 单据编号
     */
    private String serialNumber;

    /**
     * 日期
     */
    private String date;

    /**
     * 销售组织
     */
    private String salesOrd;

    /**
     * 客户
     */
    private String customer;

    /**
     * 销售员
     */
    private String saler;

    /**
     * 线上线下
     */
    private String assistant1;

    /**
     * 物流公司
     */
    private String assistant2;

    /**
     * 税运情况
     */
    private String assistant3;

    /**
     * 开票类型
     */
    private String assistant4;

    /**
     * 订单来源
     */
    private String assistant5;

    /**
     * 收货信息
     */
    private String remarks;

    /**
     * 网店单号
     */
    private String text1;

    /**
     * 需求数量
     */
    private String text2;

    private List<SalesProductDo> salesProductList;
}
