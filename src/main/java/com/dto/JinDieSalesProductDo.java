package com.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class JinDieSalesProductDo {

    private Integer FEntryId;

    private Map<String, String> FMaterialId;

    private Map<String, String> FUnitID;

    private Map<String, String> FPriceUnitId;

    private BigDecimal FQty;

    private Map<String, String> FStockOrgId;

    private BigDecimal FPrice;

    private BigDecimal FAllAmount;

    private BigDecimal F_LWD_Qty;

    private BigDecimal F_LWD_Price;

    private BigDecimal F_LWD_Decimal1;

    private BigDecimal F_LWD_Decimal;

    private String F_LWD_Text;

    private BigDecimal F_LWD_Decimal2;

    private String FDeliveryDate;

    private String Note;

    private BigDecimal F_LWD_Amount;

    private String FIsFree;
}
