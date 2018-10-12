package com.hjdj.finance.Controller;

import com.hjdj.finance.Constants.Constant;
import com.hjdj.finance.beans.Order;
import com.hjdj.finance.beans.Wxgzh;
import com.hjdj.finance.service.PaymentService;
import com.hjdj.finance.service.WxgzhService;
import com.hjdj.finance.utls.HttpUtil;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.List;

@Controller
public class PaymentController {

    // ��ѭ����
    public static int val = 1;

    // ���ؽ��
    private String result;

    @Autowired
    private WxgzhService wxgzhService;

    @Autowired
    private PaymentService paymentService;

    /**
     * ��ȡ�̳��û�֧����Ϣ
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/pay")
    public String payment(HttpServletRequest request, HttpServletResponse response) {
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
        Order order = getOrder(P8_Url, P0_cmd, P1_MerId, P2_Order, P3_Amt, P4_Cur, P5_Pid, P6_Pcat
                , P7_Pdesc, P9_SAF, Pa_MP, Pd_FrpId, Pr_NeedResponse, Hmac);
        // ���������ݴ洢��redis
        if (order == null || "".equals(order.getP2_Order())) {
            result = "OrderError";
            return "redirect:" + P8_Url + "?result=" + result;
        }
        // �������浽Redis
        paymentService.saveOrderRedis(order);
        // ��Redis��ѯwxgzh��Ϣ
        List<Wxgzh> wxgzhs = wxgzhService.queryWxgzhsRedis();
        Wxgzh wxgzh = wxgzhs.get(0);
        // ��ȡcode·��
        String codeUrl = getCodeUrl(order, wxgzh);
        return "redirect:" + codeUrl;
    }

    /**
     * ��ȡcode
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
        // ��redis�л�ȡ΢�Ź��ں���Ϣ
        List<Wxgzh> wxgzhs = wxgzhService.queryWxgzhsRedis();
        Wxgzh wxgzh = wxgzhs.get(0);
        // ��ȡopenid·��
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token";
        String param = "appid=" + wxgzh.getAppid() +
                "&secret=" + wxgzh.getSecret() +
                "&code=" + code +
                "&grant_type=authorization_code";
        String jsonStr = HttpUtil.sendGet(url, param);
        // ��ȡ���ص�openid
        String openid = JSONObject.fromObject(jsonStr).getString("openid");
        // �ж϶����Ƿ����
        boolean isExist = paymentService.isRedisExist(p2_Order);
        if (isExist) {
            Order order = paymentService.query(p2_Order);
            // ���������URL
            StringBuilder sbd = getFRUrl(openid, order);
            // wxgzh��ѭ
            wxgzhLX();
            return "redirect:" + sbd;
        }
        return "OrderOvertime";
    }

    /**
     * ��ȡcode·��
     * @param order
     * @param wxgzh
     * @return
     */
    private String getCodeUrl(Order order, Wxgzh wxgzh) {
        // ��ȡcode·��
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
     * ��������
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
        // ��̨ʹ�ã�����״̬
        order.setState("0");
        return order;
    }

    /**
     * �������ƴ��·��
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
     * ΢�Ź��ں���ѭ�������̰߳�ȫ��
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
