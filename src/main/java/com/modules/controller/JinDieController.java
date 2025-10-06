package com.modules.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.client.JinDieClient;
import com.common.R;
import com.dto.JinDieDo;
import com.dto.SalesDo;
import com.dto.SalesProductDo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
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

    @PostMapping("sales")
    public R<JSONObject> sales(@RequestBody SalesDo salesDo) {
        log.info("销售订单参数======>{}", salesDo);
        List<JSONObject> FSaleOrderEntry = new ArrayList<>();
        for (int i = 0; i < salesDo.getSalesProductList().size(); i++) {
            SalesProductDo salesProductDo = salesDo.getSalesProductList().get(i);
            JSONObject product = new JSONObject();
            product.put("FEntryID", i);
            product.put("FMaterialId", Map.of("FNUMBER", salesProductDo.getMaterialId()));
            product.put("F_LWD_Text", salesProductDo.getText());
            product.put("F_LWD_Qty", salesProductDo.getQty());
            product.put("FQty", salesProductDo.getFqty());
            product.put("FUnitID", Map.of("FNUMBER", salesProductDo.getUnit()));
            product.put("FPriceUnitId", Map.of("FNUMBER", salesProductDo.getPriceUnit()));
            product.put("FDeliveryDate", salesProductDo.getDeliveryDate());
            FSaleOrderEntry.add(product);
        }

        JSONObject Model = new JSONObject();
        Model.put("FBillNo", salesDo.getSerialNumber());
        Model.put("FBillTypeID", Map.of("FNUMBER", salesDo.getType()));
        Model.put("FDate", salesDo.getDate());
        Model.put("FSaleOrgId", Map.of("FNUMBER", salesDo.getSalesOrd()));
        Model.put("FCustId", Map.of("FNUMBER", salesDo.getCustomer()));
        Model.put("FSalerId", Map.of("FNUMBER", salesDo.getSaler()));
        Model.put("F_LWD_Assistant1", Map.of("FNUMBER", salesDo.getAssistant1()));
        Model.put("F_LWD_Assistant2", Map.of("FNUMBER", salesDo.getAssistant2()));
        Model.put("F_LWD_Assistant3", Map.of("FNUMBER", salesDo.getAssistant3()));
        Model.put("F_LWD_Assistant4", Map.of("FNUMBER", salesDo.getAssistant4()));
        Model.put("F_LWD_Assistant5", Map.of("FNUMBER", salesDo.getAssistant5()));
        Model.put("F_LWD_Remarks", salesDo.getRemarks());
        Model.put("F_LWD_Text1", salesDo.getText1());
        Model.put("FSaleOrderEntry", FSaleOrderEntry);

        JSONObject bodyParam = new JSONObject();
        bodyParam.put("parameters", List.of("SAL_SaleOrder", JSON.toJSONString(Map.of("Model", Model))));
        JinDieClient jinDieClient = new JinDieClient();
        JSONObject result = jinDieClient.sales(bodyParam);
        return R.ok(result);
    }
}
