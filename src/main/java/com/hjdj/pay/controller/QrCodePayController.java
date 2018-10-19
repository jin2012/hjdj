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

    // ���ؽ��
    private String result;

    @Value("${wxgzh.appid}")
    private String appid;

    @Value("${wxgzh.secret}")
    private String secret;

    @Autowired
    private PaymentConfig paymentConfig;

    @Autowired
    private PaymentService paymentService;

    /**
     * ��ȡ�̻��������ݲ�����
     * @param request
     * @param response
     * @return ����һ����ά�� ����֧��������
     */
    @RequestMapping("/qrpay")
    public String qrCodePay(Map map, HttpServletRequest request, HttpServletResponse response) {
        // �̻�����֧���ɹ����ݵĵ�ַ
        String P8_Url = request.getParameter("p8_Url");
        if (P8_Url == null || "".equals(P8_Url)) {
            return "";
        }
        // ҵ������
        String P0_cmd = request.getParameter("p0_Cmd");
        if (!"Buy".equals(P0_cmd)) {
            result = "P0_CmdError";
            return "redirect:" + P8_Url + "?result=" + result;
        }
        // �̻�ID
        String P1_MerId = request.getParameter("p1_MerId");
        if (P1_MerId == null || "".equals(P1_MerId)) {
            result = "P1_MerIdError";
            return "redirect:" + P8_Url + "?result=" + result;
        }
        // �̻�������
        String P2_Order = request.getParameter("p2_Order");
        if (P2_Order == null || "".equals(P2_Order)) {
            result = "P2_OrderError";
            return "redirect:" + P8_Url + "?result=" + result;
        }
        // ֧�����
        String P3_Amt = request.getParameter("p3_Amt");
        if (P3_Amt == null || "".equals(P3_Amt)) {
            result = "P3_AmtError";
            return "redirect:" + P8_Url + "?result=" + result;
        }
        // ���ױ���
        String P4_Cur = request.getParameter("p4_Cur");
        if (!"CNY".equals(P4_Cur)) {
            result = "P4_CurError";
            return "redirect:" + P8_Url + "?result=" + result;
        }
        // ��Ʒ����
        String P5_Pid = request.getParameter("p5_Pid");
        // ��Ʒ����
        String P6_Pcat = request.getParameter("p6_Pcat");
        // ��Ʒ����
        String P7_Pdesc = request.getParameter("p7_Pdesc");
        // �ͻ���ַ
        String P9_SAF = request.getParameter("p9_SAF");
        if (P9_SAF == null || "".equals(P9_SAF)) {
            result = "P9_SAFError";
            return "redirect:" + P8_Url + "?result=" + result;
        }
        // �̻���չ��Ϣ
        String Pa_MP = request.getParameter("pa_MP");
        if (Pa_MP == null || "".equals(Pa_MP)) {
            Pa_MP = "0";
        }
        // ֧��ͨ������
        String Pd_FrpId = request.getParameter("pd_FrpId");
        if (Pd_FrpId == null || "".equals(Pd_FrpId)) {
            result = "Pd_FrpIdError";
            return "redirect:" + P8_Url + "?result=" + result;
        }
        // Ӧ�����
        String Pr_NeedResponse = request.getParameter("pr_NeedResponse");
        if (!"1".equals(Pr_NeedResponse)) {
            result = "Pr_NeedResponseError";
            return "redirect:" + P8_Url + "?result=" + result;
        }
        // ǩ������
        String Hmac = request.getParameter("hmac");
        if (Hmac == null || "".equals(Hmac)) {
            result = "HmacError";
            return "redirect:" + P8_Url + "?result=" + result;
        }
        // ��������
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
        // ��̨ʹ�ã�����״̬
        order.setState("0");
        // ���������ݴ洢��redis
        if (order == null || "".equals(order.getP2_Order())) {
            result = "OrderError";
            return "redirect:" + P8_Url + "?result=" + result;
        }
        paymentService.saveOrderRedis(order);

        // String order = "123421331";
        String time = DateUtil.timeStamp();

        String url = "http://topay.feigela.com/code?order=" + order.getP2_Order() + "&time=" + time;
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
        //����order������Ψһ��ݲ���
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
        //��ȡ��������
        //ƴ��code��ȡ����
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