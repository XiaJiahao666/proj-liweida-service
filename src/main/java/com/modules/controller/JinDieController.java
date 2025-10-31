package com.modules.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.client.JinDieClient;
import com.common.R;
import com.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("jindie")
public class JinDieController {

    @PostMapping("page")
    public R<JSONArray> queryPage(@RequestBody JinDieDo jinDieDo) {
        JinDieClient jinDieClient = new JinDieClient();
        JSONObject bodyParam = new JSONObject();
        bodyParam.put("parameters", jinDieDo.getParameters());
        return R.ok(jinDieClient.queryPage(bodyParam));
    }

    @PostMapping("info")
    public R<JSONObject> queryInfo(@RequestBody JinDieDo jinDieDo) {
        JinDieClient jinDieClient = new JinDieClient();
        JSONObject bodyParam = new JSONObject();
        bodyParam.put("parameters", jinDieDo.getParameters());
        return R.ok(jinDieClient.queryInfo(bodyParam));
    }

//    @PostMapping("sales")
//    public R<JSONObject> sales(@RequestBody SalesDo salesDo) {
//        log.info("销售订单参数======>{}", JSON.toJSONString(salesDo));
//        List<JinDieSalesProductDo> FSaleOrderEntry = new ArrayList<>();
//        for (int i = 0; i < salesDo.getSalesProductList().size(); i++) {
//            SalesProductDo salesProductDo = salesDo.getSalesProductList().get(i);
//            JinDieSalesProductDo jinDieSalesProductDo = new JinDieSalesProductDo();
//            jinDieSalesProductDo.setFEntryId(i);
//            jinDieSalesProductDo.setFMaterialId(Map.of("FNUMBER", salesProductDo.getMaterialId()));
//            jinDieSalesProductDo.setFUnitID(Map.of("FNUMBER", salesProductDo.getUnit()));
//            jinDieSalesProductDo.setFPriceUnitId(Map.of("FNUMBER", salesProductDo.getPriceUnit()));
//            jinDieSalesProductDo.setFQty(salesProductDo.getFqty());
//            jinDieSalesProductDo.setFStockOrgId(Map.of("FNUMBER", salesProductDo.getStockOrgId()));
//            jinDieSalesProductDo.setFPrice(salesProductDo.getNoTaxPrice());
//            jinDieSalesProductDo.setFAllAmount(salesProductDo.getAllAmount());
//            jinDieSalesProductDo.setF_LWD_Qty(salesProductDo.getQty());
//            jinDieSalesProductDo.setF_LWD_Price(salesProductDo.getPrice());
//            jinDieSalesProductDo.setF_LWD_Decimal1(salesProductDo.getDecimal1());
//            jinDieSalesProductDo.setF_LWD_Decimal(salesProductDo.getDecimal());
//            jinDieSalesProductDo.setF_LWD_Text(salesProductDo.getText());
//            jinDieSalesProductDo.setF_LWD_Decimal2(salesProductDo.getDecimal2());
//            jinDieSalesProductDo.setFDeliveryDate(salesProductDo.getDeliveryDate());
//            jinDieSalesProductDo.setNote(salesProductDo.getNote());
//            jinDieSalesProductDo.setF_LWD_Amount(salesProductDo.getAmount());
//            jinDieSalesProductDo.setFIsFree(salesProductDo.getFlsFree());
//            FSaleOrderEntry.add(jinDieSalesProductDo);
//        }
//        JinDieFinanceDo FSaleOrderFinance = new JinDieFinanceDo();
//        FSaleOrderFinance.setFSettleCurrId(Map.of("FNumber", salesDo.getCurrency()));
//        FSaleOrderFinance.setFEntryId(0);
//
//        JinDieSalesDo jinDieSalesDo = new JinDieSalesDo();
//        jinDieSalesDo.setFBillTypeID(Map.of("FNUMBER", salesDo.getType()));
//        jinDieSalesDo.setFBillNo(salesDo.getSerialNumber());
//        jinDieSalesDo.setF_LWD_Remarks(salesDo.getRemarks());
//        jinDieSalesDo.setFDate(salesDo.getDate());
//        jinDieSalesDo.setF_LWD_Assistant2(Map.of("FNUMBER", salesDo.getAssistant2()));
//        jinDieSalesDo.setF_LWD_Assistant1(Map.of("FNUMBER", salesDo.getAssistant1()));
//        jinDieSalesDo.setF_LWD_Text1(salesDo.getText1());
//        jinDieSalesDo.setFSaleOrgId(Map.of("FNUMBER", salesDo.getSalesOrd()));
//        jinDieSalesDo.setF_LWD_Text2(salesDo.getText2());
//        jinDieSalesDo.setF_LWD_Assistant5(Map.of("FNUMBER", salesDo.getAssistant5()));
//        jinDieSalesDo.setF_LWD_Assistant4(Map.of("FNUMBER", salesDo.getAssistant4()));
//        jinDieSalesDo.setF_LWD_Assistant3(Map.of("FNUMBER", salesDo.getAssistant3()));
//        jinDieSalesDo.setNote(salesDo.getNote());
//        jinDieSalesDo.setFCustId(Map.of("FNUMBER", salesDo.getCustomer()));
//        jinDieSalesDo.setFSaleOrderFinance(FSaleOrderFinance);
//        jinDieSalesDo.setF_LWD_Remarks1(salesDo.getRemarks1());
//        jinDieSalesDo.setFSaleOrderEntry(FSaleOrderEntry);
//        jinDieSalesDo.setFSalerId(Map.of("FNUMBER", salesDo.getSaler()));
//
//        JSONObject bodyParam = new JSONObject();
//        bodyParam.put("parameters", List.of("SAL_SaleOrder", JSON.toJSONString(Map.of("Model", jinDieSalesDo))));
//        JinDieClient jinDieClient = new JinDieClient();
//        JSONObject result = jinDieClient.sales(bodyParam);
//        return R.ok(result);
//    }

    @PostMapping("sales")
    public R<JSONObject> sales(@RequestBody SalesDo salesDo) {
        log.info("销售订单参数======>{}", JSON.toJSONString(salesDo));
        List<Map<String, Object>> FSaleOrderEntry = new ArrayList<>();
        for (int i = 0; i < salesDo.getSalesProductList().size(); i++) {
            SalesProductDo salesProductDo = salesDo.getSalesProductList().get(i);
            Map<String, Object> product = new LinkedHashMap<>();
            product.put("FEntryID", i);
            product.put("FMaterialId", Map.of("FNUMBER", salesProductDo.getMaterialId()));
            product.put("FUnitID", Map.of("FNUMBER", salesProductDo.getUnit()));
            product.put("FPriceUnitId", Map.of("FNUMBER", salesProductDo.getPriceUnit()));
            product.put("FQty", salesProductDo.getFqty());
            product.put("FStockOrgId", Map.of("FNUMBER", salesProductDo.getStockOrgId()));
            product.put("FPrice", salesProductDo.getNoTaxPrice());
            product.put("FAllAmount", salesProductDo.getAllAmount());
            product.put("F_LWD_Qty", salesProductDo.getQty());
            product.put("F_LWD_Price", salesProductDo.getPrice());
            product.put("F_LWD_Decimal1", salesProductDo.getDecimal1());
            product.put("F_LWD_Decimal", salesProductDo.getDecimal());
            product.put("F_LWD_Text", salesProductDo.getText());
            product.put("F_LWD_Decimal2", salesProductDo.getDecimal2());
            product.put("FDeliveryDate", salesProductDo.getDeliveryDate());
            product.put("Note", salesProductDo.getNote());
            product.put("F_LWD_Amount", salesProductDo.getAmount());
            product.put("FIsFree", salesProductDo.getFlsFree());
            FSaleOrderEntry.add(product);
        }

        Map<String, Object> FSaleOrderFinance = new LinkedHashMap<>();
        FSaleOrderFinance.put("FSettleCurrId", Map.of("FNumber", salesDo.getCurrency()));
        FSaleOrderFinance.put("FEntryId", 0);

        Map<String, Object> Model = new LinkedHashMap<>();
        Model.put("FBillTypeID", Map.of("FNUMBER", salesDo.getType()));
        Model.put("FBillNo", salesDo.getSerialNumber());
        Model.put("F_LWD_Remarks", salesDo.getRemarks());
        Model.put("FDate", salesDo.getDate());
        Model.put("F_LWD_Assistant2", Map.of("FNUMBER", salesDo.getAssistant2()));
        Model.put("F_LWD_Assistant1", Map.of("FNUMBER", salesDo.getAssistant1()));
        Model.put("F_LWD_Text1", salesDo.getText1());
        Model.put("FSaleOrgId", Map.of("FNUMBER", salesDo.getSalesOrd()));
        Model.put("F_LWD_Text2", salesDo.getText2());
        Model.put("F_LWD_Assistant5", Map.of("FNUMBER", salesDo.getAssistant5()));
        Model.put("F_LWD_Assistant4", Map.of("FNUMBER", salesDo.getAssistant4()));
        Model.put("F_LWD_Assistant3", Map.of("FNUMBER", salesDo.getAssistant3()));
        Model.put("Note", salesDo.getNote());
        Model.put("FCustId", Map.of("FNUMBER", salesDo.getCustomer()));
        Model.put("FSaleOrderFinance", FSaleOrderFinance);
        Model.put("F_LWD_Remarks1", salesDo.getRemarks1());
        Model.put("FSaleOrderEntry", FSaleOrderEntry);
        Model.put("FSalerId", Map.of("FNUMBER", salesDo.getSaler()));

        JSONObject bodyParam = new JSONObject();
        bodyParam.put("parameters", List.of("SAL_SaleOrder", JSON.toJSONString(Map.of("Model", Model))));
        JinDieClient jinDieClient = new JinDieClient();
        JSONObject result = jinDieClient.sales(bodyParam);
        return R.ok(result);
    }
}
