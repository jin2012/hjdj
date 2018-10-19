package com.hjdj.pay.controller;

import com.hjdj.pay.beans.Order;
import com.hjdj.pay.service.PaymentService;
import com.hjdj.pay.utls.DigestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String payTest(HttpServletRequest request, HttpServletResponse response){
        // 商户接收支付成功数据的地址
        String P8_Url = request.getParameter("p8_Url");
        if (P8_Url == null || "".equals(P8_Url)) {
            return "";
        }
        // 业务类型
        String P0_cmd = request.getParameter("p0_Cmd");
        if ( !"Buy".equals(P0_cmd)) {
            result = "P0_CmdError";
            return "redirect:" + P8_Url + "?result=" + result;
        }
        // 商户ID
        String P1_MerId = request.getParameter("p1_MerId");
        if (P1_MerId == null || "".equals(P1_MerId)) {
            result = "P1_MerIdError";
            return "redirect:" + P8_Url + "?result=" + result;
        }
        // 商户订单号
        String P2_Order = request.getParameter("p2_Order");
        if (P2_Order == null || "".equals(P2_Order)) {
            result = "P2_OrderError";
            return "redirect:" + P8_Url + "?result=" + result;
        }
        // 支付金额
        String P3_Amt = request.getParameter("p3_Amt");
        if (P3_Amt == null || "".equals(P3_Amt)) {
            result = "P3_AmtError";
            return "redirect:" + P8_Url + "?result=" + result;
        }
        // 交易币种
        String P4_Cur = request.getParameter("p4_Cur");
        if ( !"CNY".equals(P4_Cur)) {
            result = "P4_CurError";
            return "redirect:" + P8_Url + "?result=" + result;
        }

        // 商品名称
        String P5_Pid = request.getParameter("p5_Pid");
        // 商品种类
        String P6_Pcat = request.getParameter("p6_Pcat");
        // 商品描述
        String P7_Pdesc = request.getParameter("p7_Pdesc");
        // 送货地址
        String P9_SAF = request.getParameter("p9_SAF");
        if (P9_SAF == null || "".equals(P9_SAF)) {
            result = "P9_SAFError";
            return "redirect:" + P8_Url + "?result=" + result;
        }
        // 商户扩展信息
        String Pa_MP = request.getParameter("pa_MP");
        if (Pa_MP == null || "".equals(Pa_MP)) {
            Pa_MP = "0";
        }
        // 支付通道编码
        String Pd_FrpId = request.getParameter("pd_FrpId");
        if (Pd_FrpId == null || "".equals(Pd_FrpId)) {
            result = "Pd_FrpIdError";
            return "redirect:" + P8_Url + "?result=" + result;
        }
        // 应答机制
        String Pr_NeedResponse = request.getParameter("pr_NeedResponse");
        if ( !"1".equals(Pr_NeedResponse)) {
            result = "Pr_NeedResponseError";
            return "redirect:" + P8_Url + "?result=" + result;
        }

        StringBuffer sValue = new StringBuffer();
        sValue.append(P0_cmd);
        sValue.append(P1_MerId);
        sValue.append(P2_Order);
        sValue.append(P3_Amt);
        sValue.append(P4_Cur);
        sValue.append(P5_Pid);
        sValue.append(P6_Pcat);
        sValue.append(P7_Pdesc);
        sValue.append(P8_Url);
        sValue.append(P9_SAF);
        sValue.append(Pa_MP);
        sValue.append(Pd_FrpId);
        sValue.append(Pr_NeedResponse);

        String keyValue = "48i43KP7qErW8x8j6ECk1n4PAxUUlyyQ";
        String Hmac = null;
        Hmac = DigestUtil.hmacSign(sValue.toString(), keyValue);

        System.out.println(Hmac);
        // 创建订单
        Order order = new Order();
        order.setP0_Cmd(P0_cmd);
        order.setP1_MerId(P1_MerId);
        order.setP2_Order(P2_Order);
        order.setP3_Amt(P3_Amt);
        order.setP4_Cur(P4_Cur);
        order.setP5_Pid(P5_Pid);
        order.setP6_Pcat(P6_Pcat);
        order.setP7_Pdesc(P7_Pdesc);
        order.setP8_Url(P8_Url);
        order.setP9_SAF(P9_SAF);
        order.setPa_MP(Pa_MP);
        order.setPd_FrpId(Pd_FrpId);
        order.setPr_NeedResponse(Pr_NeedResponse);
        order.setHmac(Hmac);
        // 后台使用，订单状态
        order.setState("0");

        // 将订单数据存储到redis
        if (order == null || "".equals(order.getP2_Order())) {
            result = "OrderError";
            return "redirect:" + P8_Url + "?result=" + result;
        }
        paymentService.saveOrderRedis(order);

        // 获取code路径
        String codeUrl = "https://open.weixin.qq.com/connect/oauth2/authorize" +
        //        "?appid=wx21d7f581d2d3d51e" +
        //        "?appid=wx37cb4b56015f452f" +
                "?appid=" + appid +
                "&redirect_uri=http://topay.feigela.com/payment-pay/code" +
                "&response_type=code" +
                "&scope=snsapi_base" +
                "&state=" + order.getP2_Order() +
                "#wechat_redirect";

        return "redirect:" + codeUrl;
    }

}
