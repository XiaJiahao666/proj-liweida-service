package com.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SalesProductDo {

    /**
     * 物料编码
     */
    private String materialId;

    /**
     * 尺寸块数
     */
    private String text;

    /**
     * 估算重量
     */
    private BigDecimal qty;

    /**
     * 销售数量
     */
    private BigDecimal fqty;

    /**
     * 销售单位
     */
    private String unit;

    /**
     * 计价单位
     */
    private String priceUnit;

    /**
     * 要货日期
     */
    private String deliveryDate;
}
