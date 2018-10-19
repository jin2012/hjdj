package com.hjdj.pay.utls;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    /**
     * ʱ���ת�������ڸ�ʽ�ַ���
     * @param seconds ��ȷ������ַ���
     * @param formatStr
     * @return
     */
    public static String timeStamp2Date(String seconds,String format) {
        if(seconds == null || seconds.isEmpty() || seconds.equals("null")){
            return "";
        }
        if(format == null || format.isEmpty()){
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds+"000")));
    }
    /**
     * ���ڸ�ʽ�ַ���ת����ʱ���
     * @param date �ַ�������
     * @param format �磺yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String date2TimeStamp(String date_str,String format){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return String.valueOf(sdf.parse(date_str).getTime()/1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * ȡ�õ�ǰʱ�������ȷ���룩
     * @return
     */
    public static String timeStamp(){
        long time = System.currentTimeMillis();
        String t = String.valueOf(time/1000);
        return t;
    }

    /*public static void main(String[] args){

        int i1 = Integer.parseInt(timeStamp());
        System.out.println(i1);

        int i2 = Integer.parseInt(timeStamp());
        System.out.println(i2);

        System.out.println(i2-i1);
    }*/

}
