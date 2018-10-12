package com.hjdj.finance.utls;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

public class DESUtil {
    /*
     * 生成密钥
     */
    public static byte[] initKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
        keyGen.init(56);
        SecretKey secretKey = keyGen.generateKey();
        return secretKey.getEncoded();
    }


    /*
     * DES 加密
     */
    public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key, "DES");

        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] cipherBytes = cipher.doFinal(data);
        return cipherBytes;
    }


    /*
     * DES 解密
     */
    public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key, "DES");

        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] plainBytes = cipher.doFinal(data);
        return plainBytes;
    }

    /*public static void main(String[] args) throws Exception {
        byte[] desKey = new HexBinaryAdapter().unmarshal("019DC1AEC1DFAEAE");
        String str = "{\"p1_MerId\":\"1234\",\"keyValue\":\"kikikikikikikikikiki\",\"p2_Order\":\"789461515\",\"p3_Amt\":\"0.01\",\"p5_Pid\":\"productname\",\"p6_Pcat\":\"producttype\",\"p7_Pdesc\":\"productdesc\",\"p8_Url\":\"http://localhost:8080/callback.jsp\",\"p9_SAF\":\"1\",\"pa_MP\":\"123456\",\"pd_FrpId\":\"wxgzh\"}";
        byte[] bytes = str.getBytes();
        byte[] encrypt = DESUtil.encrypt(bytes, desKey);
        String res = new String(encrypt);
        System.out.println(new HexBinaryAdapter().marshal(encrypt));

        byte[] decrypt = DESUtil.decrypt(encrypt, desKey);
        String qes = new String(decrypt);
        System.out.println(qes);

    }*/

}
