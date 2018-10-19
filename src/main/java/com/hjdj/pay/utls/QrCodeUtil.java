package com.hjdj.pay.utls;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.QRCode;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class QrCodeUtil {
    public static String encode(String contents, BarcodeFormat barcodeFormat, Integer margin,
                                ErrorCorrectionLevel errorLevel, int width, int height) {
        BufferedImage bufImg;
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, errorLevel);
        hints.put(EncodeHintType.MARGIN, margin);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(contents, barcodeFormat, width, height, hints);
            MatrixToImageConfig config = new MatrixToImageConfig(0xFF000001, 0xFFFFFFFF);
            bufImg = MatrixToImageWriter.toBufferedImage(bitMatrix, config);
            return imgToBase64String(bufImg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String imgToBase64String(final RenderedImage img){
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        try{
            ImageIO.write(img, "png", os);
            byte[] bytes = os.toByteArray();
            BASE64Encoder encoder = new BASE64Encoder();
            String png_base64 =  encoder.encodeBuffer(bytes).trim();
            png_base64 = png_base64.replaceAll("\n", "").replaceAll("\r", "");
            return png_base64;
        }
        catch(final IOException e){
            e.printStackTrace();
        }
        return null;
    }

}
