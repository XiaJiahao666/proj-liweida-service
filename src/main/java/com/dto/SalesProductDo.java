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

    /**
     * 单价
     */
    private BigDecimal noTaxPrice;

    /**
     * 是否赠品
     */
    private String flsFree;

    /**
     * 价税合计
     */
    private BigDecimal allAmount;

    /**
     * 库存组织
     */
    private String stockOrgId;

    /**
     * 备注
     */
    private String note;

    /**
     * 运费
     */
    private BigDecimal decimal1;

    /**
     * 回扣
     */
    private BigDecimal decimal2;

    /**
     * 基准单价
     */
    private BigDecimal price;

    /**
     * 基准金额
     */
    private BigDecimal amount;

    /**
     * 其他费用
     */
    private BigDecimal decimal;
}
