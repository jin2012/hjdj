package com.hjdj.pay.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.hjdj.pay.beans.Order;
import com.hjdj.pay.config.PaymentConfig;
import com.hjdj.pay.service.PaymentService;
import com.hjdj.pay.utls.DateUtil;
import com.hjdj.pay.utls.QrCodeUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Controller
public class QrCodePayController {

    // 返回结果
    private String result;

    @Value("${wxgzh.appid}")
    private String appid;

    @Value("${wxgzh.secret}")
    private String secret;

    @Autowired
    private PaymentConfig paymentConfig;

    @Autowired
    private PaymentService paymentService;

    @RequestMapping("/qrpay")
    public String qrCodePay(@RequestParam("p0_Cmd") String p0_Cmd,
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
                            @RequestParam("pr_NeedResponse") String  pr_NeedResponse,
                            @RequestParam("hmac") String hmac,
                            HttpServletResponse response) {
        // 商户接收支付成功数据的地址
        if (p8_Url == null || "".equals(p8_Url)) {
            return "";
        }
        // 业务类型
        if (!"Buy".equals(p0_Cmd)) {
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
        if (!"CNY".equals(p4_Cur)) {
            result = "P4_CurError";
            return "redirect:" + p8_Url + "?result=" + result;
        }
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
        if (!"1".equals(pr_NeedResponse)) {
            result = "Pr_NeedResponseError";
            return "redirect:" + p8_Url + "?result=" + result;
        }
        // 签名数据
        if (hmac == null || "".equals(hmac)) {
            result = "HmacError";
            return "redirect:" + p8_Url + "?result=" + result;
        }
        // 创建订单
        Order order = new Order();
        order.setP0_Cmd(p0_Cmd);
        order.setP1_MerId(p1_MerId);
        order.setP2_Order(p2_Order);
        order.setP3_Amt(p3_Amt);
        order.setP4_Cur(p4_Cur);
        order.setP5_Pid(p5_Pid);
        order.setP6_Pcat(p6_Pcat);
        order.setP7_Pdesc(p7_Pdesc);
        order.setP8_Url(p8_Url);
        order.setP9_SAF(p9_SAF);
        order.setPa_MP(pa_MP);
        order.setPd_FrpId(pd_FrpId);
        order.setPr_NeedResponse(pr_NeedResponse);
        order.setHmac(hmac);
        // 后台使用，订单状态
        order.setState("0");
        // 将订单数据存储到redis
        if (order == null || "".equals(order.getP2_Order())) {
            result = "OrderError";
            return "redirect:" + p8_Url + "?result=" + result;
        }
        paymentService.saveOrderRedis(order);

        // String order = "123421331";
        String time = DateUtil.timeStamp();

        String url = "http://topay.feigela.com/wxqrcode?order=" + order.getP2_Order() + "&time=" + time;
        // String url = "http://w1.zhexi.tech:44126/wxqrcode?order="+ order+ "&time=" + time;

        try {
            String base64Str =
                    QrCodeUtil.encode(
                            url, BarcodeFormat.QR_CODE, 3,
                            ErrorCorrectionLevel.H, 400, 400);
            response.getWriter().write(
                    StringUtils.replace(
                            paymentConfig.getQrcodeHtmlTemplate(),
                            "${qrcode}",
                            base64Str));
            System.out.println("code");
            response.getWriter().flush();
            response.getWriter().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping("/wxqrcode")
    public String qrCode(@RequestParam("order") String order, @RequestParam("time") String time) {
        int timeNow = Integer.parseInt(DateUtil.timeStamp());
        //接收order参数和唯一身份参数
        if(order == null || "".equals(order)){
            return "redirect:error/ordertiomeout.html";
        }
        if(time != null || !"".equals(time)){
            int timeOld = Integer.parseInt(time);
            int sum = timeNow - timeOld;
            if(sum > 120){
                paymentService.deleteOrderRedis(order);
                return "redirect:error/qrerror.html";
            }
        }
        //获取订单数据
        //拼接code获取连接
        String codeUrl = "https://open.weixin.qq.com/connect/oauth2/authorize" +
                "?appid=" + appid +
                "&redirect_uri=http://topay.feigela.com/payment-pay/code" +
                "&response_type=code" +
                "&scope=snsapi_base" +
                "&state=" + order +
                "#wechat_redirect";
        System.out.println(codeUrl);
        return "redirect:" + codeUrl;
    }
}