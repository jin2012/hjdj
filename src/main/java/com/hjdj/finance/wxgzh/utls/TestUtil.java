package com.hjdj.finance.wxgzh.utls;

public class TestUtil {
    public static void main(String[] arg){
        StringBuffer sValue = new StringBuffer();
        // ҵ������
        sValue.append("Buy");
        // �̻����
        sValue.append("1564");
        // �̻�������
        sValue.append("987412365");
        // ֧�����
        sValue.append("0.01");
        // ���ױ���
        sValue.append("CNY");
        // ��Ʒ����
        sValue.append("productname");
        // ��Ʒ����
        sValue.append("producttype");
        // ��Ʒ����
        sValue.append("productdesc");
        // �̻�����֧���ɹ����ݵĵ�ַ
        sValue.append("http://localhost:8080/massage.html");
        // �ͻ���ַ
        sValue.append("0");
        // �̻���չ��Ϣ
        sValue.append("123456");
        // ���б���
        sValue.append("wxgzh");
        // Ӧ�����
        sValue.append("1");

        String keyValue = "48i43KP7qErW8x8j6ECk1n4PAxUUlyyQ";
        String sNewString = null;
        sNewString = DigestUtil.hmacSign(sValue.toString(), keyValue);

        System.out.println(sNewString);


    }
}
