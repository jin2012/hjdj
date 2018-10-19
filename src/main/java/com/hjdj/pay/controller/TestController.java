package com.hjdj.pay.controller;

import com.hjdj.pay.beans.Order;
import com.hjdj.pay.service.PaymentService;
import com.hjdj.pay.utls.DigestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class TestController {

    // 返回结果
    private String result;

    @Value("${wxgzh.appid}")
    private String appid;

    @Value("${wxgzh.secret}")
    private String secret;

    @Autowired
    private PaymentService paymentService;

    @RequestMapping("/paytest")
    // public String payTest(HttpServletRequest request, HttpServletResponse response){
    public String payTest(@RequestParam("p0_Cmd") String p0_Cmd,
                          @RequestParam("p1_MerId") String p1_MerId,
                          @RequestParam("p2_Order") String p2_Order,
                          @RequestParam("p3_Amt") String p3_Amt,
                          @RequestParam("p4_Cur") String p4_Cur,
                          @RequestParam("p5_Pid") String p5_Pid,
                          @RequestParam("p6_Pcat") String p6_Pcat,
                          @RequestParam("p7_Pdesc") String p7_Pdesc,
                          @RequestParam("p8_Url") String p8_Url,
                          @RequestParam("p9_SAF") String p9_SAF,
                          @RequestParam("pa_MP") String pa_MP,
                          @RequestParam("pd_FrpId") String pd_FrpId,
                          @RequestParam("pr_NeedResponse") String pr_NeedResponse){
        // 商户接收支付成功数据的地址
        if (p8_Url == null || "".equals(p8_Url)) {
            return "";
        }
        // 业务类型
        if ( !"Buy".equals(p0_Cmd)) {
            result = "P0_CmdError";
            return "redirect:" + p8_Url + "?result=" + result;
        }
        // 商户ID
        if (p1_MerId == null || "".equals(p1_MerId)) {
            result = "P1_MerIdError";
            return "redirect:" + p8_Url + "?result=" + result;
        }
        // 商户订单号
        if (p2_Order == null || "".equals(p2_Order)) {
            result = "P2_OrderError";
            return "redirect:" + p8_Url + "?result=" + result;
        }
        // 支付金额
        if (p3_Amt == null || "".equals(p3_Amt)) {
            result = "P3_AmtError";
            return "redirect:" + p8_Url + "?result=" + result;
        }
        // 交易币种
        if ( !"CNY".equals(p4_Cur)) {
            result = "P4_CurError";
            return "redirect:" + p8_Url + "?result=" + result;
        }

        // 商品名称
        // 商品种类
        // 商品描述
        // 送货地址
        if (p9_SAF == null || "".equals(p9_SAF)) {
            result = "P9_SAFError";
            return "redirect:" + p8_Url + "?result=" + result;
        }
        // 商户扩展信息
        if (pa_MP == null || "".equals(pa_MP)) {
            pa_MP = "0";
        }
        // 支付通道编码
        if (pd_FrpId == null || "".equals(pd_FrpId)) {
            result = "Pd_FrpIdError";
            return "redirect:" + p8_Url + "?result=" + result;
        }
        // 应答机制
        if ( !"1".equals(pr_NeedResponse)) {
            result = "Pr_NeedResponseError";
            return "redirect:" + p8_Url + "?result=" + result;
        }

        StringBuffer sValue = new StringBuffer();
        sValue.append(p0_Cmd);
        sValue.append(p1_MerId);
        sValue.append(p2_Order);
        sValue.append(p3_Amt);
        sValue.append(p4_Cur);
        sValue.append(p5_Pid);
        sValue.append(p6_Pcat);
        sValue.append(p7_Pdesc);
        sValue.append(p8_Url);
        sValue.append(p9_SAF);
        sValue.append(pa_MP);
        sValue.append(pd_FrpId);
        sValue.append(pr_NeedResponse);

        String keyValue = "48i43KP7qErW8x8j6ECk1n4PAxUUlyyQ";
        String hmac = DigestUtil.hmacSign(sValue.toString(), keyValue);

        return "redirect:pay" +
                "?p0_Cmd="+ p0_Cmd +
                "&p1_MerId=" + p1_MerId +
                "&p2_Order=" + p2_Order +
                "&p3_Amt=" + p3_Amt +
                "&p4_Cur=" + p4_Cur +
                "&p5_Pid=" + p5_Pid +
                "&p6_Pcat=" + p6_Pcat +
                "&p7_Pdesc=" + p7_Pdesc +
                "&p8_Url=" + p8_Url +
                "&p9_SAF=" + p9_SAF +
                "&pa_MP=" + pa_MP +
                "&pd_FrpId=" + pd_FrpId +
                "&pr_NeedResponse=" + pr_NeedResponse +
                "&hmac=" + hmac;
    }

    @RequestMapping("/payQrcodetest")
    // public String payTest(HttpServletRequest request, HttpServletResponse response){
    public String payQrcodeTest(@RequestParam("p0_Cmd") String p0_Cmd,
                          @RequestParam("p1_MerId") String p1_MerId,
                          @RequestParam("p2_Order") String p2_Order,
                          @RequestParam("p3_Amt") String p3_Amt,
                          @RequestParam("p4_Cur") String p4_Cur,
                          @RequestParam("p5_Pid") String p5_Pid,
                          @RequestParam("p6_Pcat") String p6_Pcat,
                          @RequestParam("p7_Pdesc") String p7_Pdesc,
                          @RequestParam("p8_Url") String p8_Url,
                          @RequestParam("p9_SAF") String p9_SAF,
                          @RequestParam("pa_MP") String pa_MP,
                          @RequestParam("pd_FrpId") String pd_FrpId,
                          @RequestParam("pr_NeedResponse") String pr_NeedResponse){
        // 商户接收支付成功数据的地址
        if (p8_Url == null || "".equals(p8_Url)) {
            return "";
        }
        // 业务类型
        if ( !"Buy".equals(p0_Cmd)) {
            result = "P0_CmdError";
            return "redirect:" + p8_Url + "?result=" + result;
        }
        // 商户ID
        if (p1_MerId == null || "".equals(p1_MerId)) {
            result = "P1_MerIdError";
            return "redirect:" + p8_Url + "?result=" + result;
        }
        // 商户订单号
        if (p2_Order == null || "".equals(p2_Order)) {
            result = "P2_OrderError";
            return "redirect:" + p8_Url + "?result=" + result;
        }
        // 支付金额
        if (p3_Amt == null || "".equals(p3_Amt)) {
            result = "P3_AmtError";
            return "redirect:" + p8_Url + "?result=" + result;
        }
        // 交易币种
        if ( !"CNY".equals(p4_Cur)) {
            result = "P4_CurError";
            return "redirect:" + p8_Url + "?result=" + result;
        }

        // 商品名称
        // 商品种类
        // 商品描述
        // 送货地址
        if (p9_SAF == null || "".equals(p9_SAF)) {
            result = "P9_SAFError";
            return "redirect:" + p8_Url + "?result=" + result;
        }
        // 商户扩展信息
        if (pa_MP == null || "".equals(pa_MP)) {
            pa_MP = "0";
        }
        // 支付通道编码
        if (pd_FrpId == null || "".equals(pd_FrpId)) {
            result = "Pd_FrpIdError";
            return "redirect:" + p8_Url + "?result=" + result;
        }
        // 应答机制
        if ( !"1".equals(pr_NeedResponse)) {
            result = "Pr_NeedResponseError";
            return "redirect:" + p8_Url + "?result=" + result;
        }

        StringBuffer sValue = new StringBuffer();
        sValue.append(p0_Cmd);
        sValue.append(p1_MerId);
        sValue.append(p2_Order);
        sValue.append(p3_Amt);
        sValue.append(p4_Cur);
        sValue.append(p5_Pid);
        sValue.append(p6_Pcat);
        sValue.append(p7_Pdesc);
        sValue.append(p8_Url);
        sValue.append(p9_SAF);
        sValue.append(pa_MP);
        sValue.append(pd_FrpId);
        sValue.append(pr_NeedResponse);

        String keyValue = "48i43KP7qErW8x8j6ECk1n4PAxUUlyyQ";
        String hmac = DigestUtil.hmacSign(sValue.toString(), keyValue);

        return "redirect:qrpay" +
                "?p0_Cmd="+ p0_Cmd +
                "&p1_MerId=" + p1_MerId +
                "&p2_Order=" + p2_Order +
                "&p3_Amt=" + p3_Amt +
                "&p4_Cur=" + p4_Cur +
                "&p5_Pid=" + p5_Pid +
                "&p6_Pcat=" + p6_Pcat +
                "&p7_Pdesc=" + p7_Pdesc +
                "&p8_Url=" + p8_Url +
                "&p9_SAF=" + p9_SAF +
                "&pa_MP=" + pa_MP +
                "&pd_FrpId=" + pd_FrpId +
                "&pr_NeedResponse=" + pr_NeedResponse +
                "&hmac=" + hmac;
    }

}
