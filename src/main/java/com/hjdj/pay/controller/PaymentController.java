package com.hjdj.pay.controller;

import com.hjdj.pay.beans.Order;
import com.hjdj.pay.service.PaymentService;
import com.hjdj.pay.utls.DESUtil;
import com.hjdj.pay.utls.HttpUtil;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

@Controller
public class PaymentController {

    private String result;

    @Autowired
    private PaymentService paymentService;

    @RequestMapping("/payorder")
    public String pay(@RequestBody JSONObject order) {
        // 秘钥
        byte[] desKey = new HexBinaryAdapter().unmarshal("019DC1AEC1DFAEAE");
        // 获取orderStr
        if(order == null || "".equals(order)){
            return "订单对象不能为空";
        }
        String orderStr = order.getString("order");
        // 解密
        byte[] desPlain = new byte[0];
        try {
            desPlain = DESUtil.decrypt(new HexBinaryAdapter().unmarshal(orderStr), desKey);
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println(e.getMessage());
            return "解密数据失败！";
        }
        String desStr = new String(desPlain);
        JSONObject jsonObject = JSONObject.fromObject(desStr);
        Order orderBean = (Order) JSONObject.toBean(jsonObject, Order.class);
        orderBean.setP0_Cmd("Buy");
        orderBean.setP4_Cur("CNY");
        orderBean.setPr_NeedResponse("1");
        orderBean.setState("0");

        if (orderBean.getP8_Url() == null || "".equals(orderBean.getP8_Url())) {
            return "";
        }
        if (orderBean.getP1_MerId() == null || "".equals(orderBean.getP1_MerId())) {
            result = "商户ID不能为空";
            return "redirect:" + orderBean.getP8_Url() + "?result=" + result;
        }
        if (orderBean.getP2_Order() == null || "".equals(orderBean.getP2_Order())) {
            result = "订单号不能为空";
            return "redirect:" + orderBean.getP8_Url() + "?result=" + result;
        }
        if (orderBean.getP3_Amt() == null || "".equals(orderBean.getP3_Amt())) {
            result = "支付金额不能为空";
            return "redirect:" + orderBean.getP8_Url() + "?result=" + result;
        }
        if (orderBean.getP9_SAF() == null || "".equals(orderBean.getP9_SAF())) {
            result = "送货地址不能为空";
            return "redirect:" + orderBean.getP8_Url() + "?result=" + result;
        }
        if (orderBean.getPd_FrpId() == null || "".equals(orderBean.getPd_FrpId())) {
            result = "支付通道编码不能为空";
            return "redirect:" + orderBean.getP8_Url() + "?result=" + result;
        }

        /*// 拼接字符串、并加密
        StringBuffer sValue = new StringBuffer();
        sValue.append(orderBean.getP0_Cmd());
        sValue.append(orderBean.getP1_MerId());
        sValue.append(orderBean.getP2_Order());
        sValue.append(orderBean.getP3_Amt());
        sValue.append(orderBean.getP4_Cur());
        sValue.append(orderBean.getP5_Pid());
        sValue.append(orderBean.getP6_Pcat());
        sValue.append(orderBean.getP7_Pdesc());
        sValue.append(orderBean.getP8_Url());
        sValue.append(orderBean.getP9_SAF());
        sValue.append(orderBean.getPa_MP());
        sValue.append(orderBean.getPd_FrpId());
        sValue.append(orderBean.getPr_NeedResponse());
        String sNewString = null;
        sNewString = DigestUtil.hmacSign(sValue.toString(), orderBean.keyValue());*/

        //orderBean.setHmac(sNewString);

        // 将订单数据存储到redis
        if (order == null || "".equals(orderBean.getP2_Order())) {
            result = "订单错误";
            return "redirect:" + orderBean.getP8_Url() + "?result=" + result;
        }
        paymentService.saveOrderRedis(orderBean);

        // 获取code路径
        String codeUrl = "https://open.weixin.qq.com/connect/oauth2/authorize" +
        //        "?appid=wxbff989fe6461516c" +
        //        "?appid=wx98b22e6cdd9429db" +
        //        "?appid=wx352fe6a15b754354" +
        //        "?appid=wx3ac0e3526ab0e2fb" +
        //        "?appid=wx979307632cb303ca" +
        //        "?appid=wx3c5d08c639c80aad" +
        //        "?appid=wx1657d33073ed7ad5" +
        //        "?appid=wx21d7f581d2d3d51e" +
                "?appid=wx37cb4b56015f452f" +
                "&redirect_uri=http://topay.feigela.com/payment-pay/code" +
                "&response_type=code" +
                "&scope=snsapi_base" +
                "&state=" + orderBean.getP2_Order() +
                "#wechat_redirect";
        return "redirect:" + codeUrl;
    }

    /**
     * 获取商城用户支付信息
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/pay")
    public String payment(HttpServletRequest request, HttpServletResponse response) {
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
        // 签名数据
        String Hmac = request.getParameter("hmac");
        if (Hmac == null || "".equals(Hmac)) {
            result = "HmacError";
            return "redirect:" + P8_Url + "?result=" + result;
        }
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
        //        "?appid=wxbff989fe6461516c" +
        //        "?appid=wx98b22e6cdd9429db" +
        //        "?appid=wx352fe6a15b754354" +
        //        "?appid=wx3ac0e3526ab0e2fb" +
        //        "?appid=wx979307632cb303ca" +
        //        "?appid=wx3c5d08c639c80aad" +
        //        "?appid=wx1657d33073ed7ad5" +
        //        "?appid=wx21d7f581d2d3d51e" +
                "?appid=wx37cb4b56015f452f" +
                "&redirect_uri=http://topay.feigela.com/payment-pay/code" +
                "&response_type=code" +
                "&scope=snsapi_base" +
                "&state=" + order.getP2_Order() +
                "#wechat_redirect";

        return "redirect:" + codeUrl;
    }

    /**
     * 获取code
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/code")
    public String obtainCode(HttpServletRequest request, HttpServletResponse response) {

        String code = request.getParameter("code");
        if (code == null || "".equals(code)) {
            return "";
        }
        String p2_Order = request.getParameter("state");
        if (p2_Order == null || "".equals(p2_Order)) {
            return "";
        }
        // 获取openid路径
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token";
        // String param = "appid=wxbff989fe6461516c" +
        // String param = "appid=wx98b22e6cdd9429db" +
        // String param = "appid=wx352fe6a15b754354" +
        // String param = "appid=wx3ac0e3526ab0e2fb" +
        // String param = "appid=wx979307632cb303ca" +
        // String param = "appid=wx3c5d08c639c80aad" +
        // String param = "appid=wx1657d33073ed7ad5" +
        // String param = "appid=wx21d7f581d2d3d51e" +
         String param = "appid=wx37cb4b56015f452f" +
        //        "&secret=319faaef993ff6071b537854819f9964" +
        //        "&secret=06aa43c5f88ab49953ef96f93e953f76" +
        //        "&secret=9967f335887be1384913327073abf686" +
        //        "&secret=d98ba887416da5804a45ca74850dbd2d" +
        //        "&secret=cbc86755a898155738cf91c09c65b3fe" +
        //        "&secret=6486392713f261ad10d41b9a5fa561c2" +
        //        "&secret=19daf37d7162c33ba54bfed053c32ed0" +
        //        "&secret=a948ac0b0d7cffb867c3a5eb74662636" +
                "&secret=970df66e92e5d00fdc98ffbd1632b10b" +
                "&code=" + code +
                "&grant_type=authorization_code";
        String jsonStr = HttpUtil.sendGet(url, param);

        String openid = JSONObject.fromObject(jsonStr).getString("openid");

        boolean isExist = paymentService.isRedisExist(p2_Order);
        if (isExist) {
            Order order = paymentService.query(p2_Order);
            // 锋锐请求URL
            StringBuilder sbd = new StringBuilder();
            sbd.append("http://payment.feigela.com/GateWay/ReceiveBank.aspx" );
            sbd.append("?p0_Cmd=" + order.getP0_Cmd());
            sbd.append("&p1_MerId=" + order.getP1_MerId());
            sbd.append("&p2_Order=" + order.getP2_Order());
            sbd.append("&p3_Amt=" + order.getP3_Amt());
            sbd.append("&p4_Cur=" + order.getP4_Cur());
            if(order.getP5_Pid() != null && !"".equals(order.getP5_Pid())){
                sbd.append("&p5_Pid=" + order.getP5_Pid());
            }
            if(order.getP6_Pcat() != null && !"".equals(order.getP6_Pcat())){
                sbd.append("&p6_Pcat=" + order.getP6_Pcat());
            }
            if(order.getP7_Pdesc() != null && !"".equals(order.getP7_Pdesc())){
                sbd.append("&p7_Pdesc=" + order.getP7_Pdesc());
            }
            sbd.append("&p8_Url=" + order.getP8_Url());
            sbd.append("&p9_SAF=" + order.getP9_SAF());
            if(order.getPa_MP() != null && !"".equals(order.getPa_MP())){
                sbd.append("&pa_MP=" + order.getPa_MP());
            }
            sbd.append("&pd_FrpId=" + order.getPd_FrpId());
            sbd.append("&pr_NeedResponse=" + order.getPr_NeedResponse());
            sbd.append("&hmac=" + order.getHmac());
            sbd.append("&pr_Openid=" + openid);

            /*if(Runner.val < 5){
                Runner.val ++ ;
            }else {
                Runner.val = 0;
                wxgzhService.findRedis();
           }*/

            return "redirect:" + sbd;
        }
        return "OrderOvertime";
    }

}
