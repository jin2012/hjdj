package com.hjdj.finance.wxgzh.utls;

public class TestUtil {
    public static void main(String[] arg){
        StringBuffer sValue = new StringBuffer();
        // 业务类型
        sValue.append("Buy");
        // 商户编号
        sValue.append("1564");
        // 商户订单号
        sValue.append("987412365");
        // 支付金额
        sValue.append("0.01");
        // 交易币种
        sValue.append("CNY");
        // 商品名称
        sValue.append("productname");
        // 商品种类
        sValue.append("producttype");
        // 商品描述
        sValue.append("productdesc");
        // 商户接收支付成功数据的地址
        sValue.append("http://localhost:8080/massage.html");
        // 送货地址
        sValue.append("0");
        // 商户扩展信息
        sValue.append("123456");
        // 银行编码
        sValue.append("wxgzh");
        // 应答机制
        sValue.append("1");

        String keyValue = "48i43KP7qErW8x8j6ECk1n4PAxUUlyyQ";
        String sNewString = null;
        sNewString = DigestUtil.hmacSign(sValue.toString(), keyValue);

        System.out.println(sNewString);


    }
}
