package com.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class JinDieSalesDo {

    private Map<String, String> FBillTypeID;

    private String FBillNo;

    private String F_LWD_Remarks;

    private String FDate;

    private Map<String, String> F_LWD_Assistant2;

    private Map<String, String> F_LWD_Assistant1;

    private String F_LWD_Text1;

    private Map<String, String> FSaleOrgId;

    private String F_LWD_Text2;

    private Map<String, String> F_LWD_Assistant5;

    private Map<String, String> F_LWD_Assistant4;

    private Map<String, String> F_LWD_Assistant3;

    private String Note;

    private Map<String, String> FCustId;

    private JinDieFinanceDo FSaleOrderFinance;

    private String F_LWD_Remarks1;

    private List<JinDieSalesProductDo> FSaleOrderEntry;

    private Map<String, String> FSalerId;
}
