package com.hjdj.pay.controller;

import com.hjdj.pay.beans.Order;
import com.hjdj.pay.service.PaymentService;
import com.hjdj.pay.utls.DESUtil;
import com.hjdj.pay.utls.HttpUtil;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

@Controller
public class PaymentController {

    // 返回结果
    private String result;

    @Value("${wxgzh.appid}")
    private String appid;

    @Value("${wxgzh.secret}")
    private String secret;

    @Autowired
    private PaymentService paymentService;

    @RequestMapping("/pay")
    public String payment(@RequestParam("p0_Cmd") String p0_Cmd,
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
                          @RequestParam("hmac") String hmac){
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
        //        "?appid=wx37cb4b56015f452f" +
                "?appid=" + appid +
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
        // String param = "appid=wx37cb4b56015f452f" +
         String param = "appid=" + appid +
        //        "&secret=319faaef993ff6071b537854819f9964" +
        //        "&secret=06aa43c5f88ab49953ef96f93e953f76" +
        //        "&secret=9967f335887be1384913327073abf686" +
        //        "&secret=d98ba887416da5804a45ca74850dbd2d" +
        //        "&secret=cbc86755a898155738cf91c09c65b3fe" +
        //        "&secret=6486392713f261ad10d41b9a5fa561c2" +
        //        "&secret=19daf37d7162c33ba54bfed053c32ed0" +
        //        "&secret=a948ac0b0d7cffb867c3a5eb74662636" +
        //        "&secret=970df66e92e5d00fdc98ffbd1632b10b" +
                "&secret=" + secret +
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

            return "redirect:" + sbd;
        }
        return "OrderOvertime";
    }

}
