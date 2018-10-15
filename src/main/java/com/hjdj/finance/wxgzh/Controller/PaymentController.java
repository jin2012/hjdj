package com.hjdj.finance.wxgzh.Controller;

import com.hjdj.finance.wxgzh.Constants.Constant;
import com.hjdj.finance.wxgzh.beans.Order;
import com.hjdj.finance.wxgzh.beans.Wxgzh;
import com.hjdj.finance.wxgzh.service.PaymentService;
import com.hjdj.finance.wxgzh.service.WxgzhService;
import com.hjdj.finance.wxgzh.utls.HttpUtil;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.List;

@Controller
public class PaymentController {

    // 轮循次数
    public static int val = 1;

    // 返回结果
    private String result;

    @Autowired
    private WxgzhService wxgzhService;

    @Autowired
    private PaymentService paymentService;

    /**
     * 获取商城用户支付信息
     *
     * @param p0_Cmd 业务类型
     * @param p1_MerId 商户ID
     * @param p2_Order 商户订单号
     * @param p3_Amt 支付金额
     * @param p4_Cur 交易币种
     * @param p5_Pid 商品名称
     * @param p6_Pcat 商品种类
     * @param p7_Pdesc 商品描述
     * @param p8_Url 商户接收支付成功数据的地址
     * @param p9_SAF 送货地址
     * @param pa_MP 商户扩展信息
     * @param pd_FrpId 支付通道编码
     * @param pr_NeedResponse 应答机制
     * @param hmac 签名数据
     * @return
     */
    @RequestMapping("/pay")
    public String payment(@RequestParam("p0_Cmd") String p0_Cmd ,
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
                          @RequestParam("pr_NeedResponse") String pr_NeedResponse,
                          @RequestParam("hmac") String hmac) {
        if (p8_Url == null || "".equals(p8_Url)) {
            return "";
        }
        if (!"Buy".equals(p0_Cmd)) {
            result = "P0_CmdError";
            return "redirect:" + p8_Url + "?result=" + result;
        }
        if (p1_MerId == null || "".equals(p1_MerId)) {
            result = "P1_MerIdError";
            return "redirect:" + p8_Url + "?result=" + result;
        }
        if (p2_Order == null || "".equals(p2_Order)) {
            result = "P2_OrderError";
            return "redirect:" + p8_Url + "?result=" + result;
        }
        if (p3_Amt == null || "".equals(p3_Amt)) {
            result = "P3_AmtError";
            return "redirect:" + p8_Url + "?result=" + result;
        }
        if (!"CNY".equals(p4_Cur)) {
            result = "P4_CurError";
            return "redirect:" + p8_Url + "?result=" + result;
        }
        if (p9_SAF == null || "".equals(p9_SAF)) {
            result = "P9_SAFError";
            return "redirect:" + p8_Url + "?result=" + result;
        }
        if (pa_MP == null || "".equals(pa_MP)) {
            pa_MP = "0";
        }
        if (pd_FrpId == null || "".equals(pd_FrpId)) {
            result = "Pd_FrpIdError";
            return "redirect:" + p8_Url + "?result=" + result;
        }
        if (!"1".equals(pr_NeedResponse)) {
            result = "Pr_NeedResponseError";
            return "redirect:" + p8_Url + "?result=" + result;
        }
        if (hmac == null || "".equals(hmac)) {
            result = "HmacError";
            return "redirect:" + p8_Url + "?result=" + result;
        }
        // 创建订单
        Order order = getOrder(p8_Url, p0_Cmd, p1_MerId, p2_Order, p3_Amt, p4_Cur, p5_Pid, p6_Pcat
                , p7_Pdesc, p9_SAF, pa_MP, pd_FrpId, pr_NeedResponse, hmac);
        // 将订单数据存储到redis
        if (order == null || "".equals(order.getP2_Order())) {
            result = "OrderError";
            return "redirect:" + p8_Url + "?result=" + result;
        }
        // 订单保存到Redis
        paymentService.saveOrderRedis(order);
        // 从Redis查询wxgzh信息
        List<Wxgzh> wxgzhs = wxgzhService.queryWxgzhsRedis();
        Wxgzh wxgzh = wxgzhs.get(0);
        // 获取code路径
        String codeUrl = getCodeUrl(order, wxgzh);
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
        // 从redis中获取微信公众号信息
        List<Wxgzh> wxgzhs = wxgzhService.queryWxgzhsRedis();
        Wxgzh wxgzh = wxgzhs.get(0);
        // 获取openid路径
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token";
        String param = "appid=" + wxgzh.getAppid() +
                "&secret=" + wxgzh.getSecret() +
                "&code=" + code +
                "&grant_type=authorization_code";
        String jsonStr = HttpUtil.sendGet(url, param);
        // 获取返回的openid
        String openid = JSONObject.fromObject(jsonStr).getString("openid");
        // 判断订单是否存在
        boolean isExist = paymentService.isRedisExist(p2_Order);
        if (isExist) {
            Order order = paymentService.query(p2_Order);
            // 请锋锐请求URL
            StringBuilder sbd = getFRUrl(openid, order);
            // wxgzh轮循
            wxgzhLX();
            return "redirect:" + sbd;
        }
        return "OrderOvertime";
    }

    /**
     * 获取code路径
     * @param order
     * @param wxgzh
     * @return
     */
    private String getCodeUrl(Order order, Wxgzh wxgzh) {
        // 获取code路径
        return "https://open.weixin.qq.com/connect/oauth2/authorize" +
                // "?appid=wx979307632cb303ca" +
                "?appid=" + wxgzh.getId() +
                "&redirect_uri=http://topay.feigela.com/payment-pay/code" +
                "&response_type=code" +
                "&scope=snsapi_base" +
                "&state=" + order.getP2_Order() +
                "#wechat_redirect";
    }

    /**
     * 创建订单
     *
     * @param p8_Url
     * @param p0_cmd
     * @param p1_MerId
     * @param p2_Order
     * @param p3_Amt
     * @param p4_Cur
     * @param p5_Pid
     * @param p6_Pcat
     * @param p7_Pdesc
     * @param p9_SAF
     * @param pa_MP
     * @param pd_FrpId
     * @param pr_NeedResponse
     * @param hmac
     * @return
     */
    private Order getOrder(String p8_Url, String p0_cmd, String p1_MerId, String p2_Order, String p3_Amt, String p4_Cur, String p5_Pid, String p6_Pcat, String p7_Pdesc, String p9_SAF, String pa_MP, String pd_FrpId, String pr_NeedResponse, String hmac) {
        Order order = new Order();
        order.setP0_Cmd(p0_cmd);
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
        return order;
    }

    /**
     * 请求锋锐拼接路径
     *
     * @param openid
     * @param order
     * @return
     */
    private StringBuilder getFRUrl(String openid, Order order) {
        StringBuilder sbd = new StringBuilder();
        sbd.append("http://payment.feigela.com/GateWay/ReceiveBank.aspx");
        sbd.append("?p0_Cmd=" + order.getP0_Cmd());
        sbd.append("&p1_MerId=" + order.getP1_MerId());
        sbd.append("&p2_Order=" + order.getP2_Order());
        sbd.append("&p3_Amt=" + order.getP3_Amt());
        sbd.append("&p4_Cur=" + order.getP4_Cur());
        if (order.getP5_Pid() != null && !"".equals(order.getP5_Pid())) {
            sbd.append("&p5_Pid=" + order.getP5_Pid());
        }
        if (order.getP6_Pcat() != null && !"".equals(order.getP6_Pcat())) {
            sbd.append("&p6_Pcat=" + order.getP6_Pcat());
        }
        if (order.getP7_Pdesc() != null && !"".equals(order.getP7_Pdesc())) {
            sbd.append("&p7_Pdesc=" + order.getP7_Pdesc());
        }
        sbd.append("&p8_Url=" + order.getP8_Url());
        sbd.append("&p9_SAF=" + order.getP9_SAF());
        if (order.getPa_MP() != null && !"".equals(order.getPa_MP())) {
            sbd.append("&pa_MP=" + order.getPa_MP());
        }
        sbd.append("&pd_FrpId=" + order.getPd_FrpId());
        sbd.append("&pr_NeedResponse=" + order.getPr_NeedResponse());
        sbd.append("&hmac=" + order.getHmac());
        sbd.append("&pr_Openid=" + openid);
        return sbd;
    }

    /**
     * 微信公众号轮循方法（线程安全）
     */
    public synchronized void wxgzhLX() {
        if (val < Constant.LX_VAL) {
            val++;
        } else {
            val = 1;
            List<Wxgzh> wxgzhs = wxgzhService.queryWxgzhsRedis();
            wxgzhs.add(wxgzhs.get(0));
            wxgzhs.remove(0);
            List<Wxgzh> newWxgzhs = new LinkedList<>();
            newWxgzhs.addAll(wxgzhs);
            wxgzhService.addWxgzhRedis(newWxgzhs);
        }
    }

}
