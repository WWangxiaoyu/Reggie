package com.it.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Slf4j
public class UploadFileTest {
    /**
     * 测试截取的文件名后缀是否含点
     */
    @Test
    public void test1(){
        String fileName = "huefnjfewop.jpg";
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        System.out.println(suffix);
    }

    /**
     * yyyy-MM、yyyy/MM: 100f * 4f
     *
     */
    @Test
    public void testDate(){
        //获得对应格式的日期
        String format = "yyyy@@MM";
        DateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date();

        String dateStr = dateFormat.format(date);
        String fileHash = "Anysign-DateImg-" + dateStr;
        String imgBase64;

        float w;
        float h;
        Font font = new Font("微软雅黑", Font.PLAIN, 100);
        FontMetrics fm = sun.font.FontDesignMetrics.getMetrics(font);
        w = fm.stringWidth(format);
        h = fm.getHeight();


        w = format.contains("dd") ? (w-70f) * 1f : (w-52f)*1f;

        //base64编码生成
        byte[] imgBytes = ImageUtil.getFormatDateImg(w, 32f * 4f, 100, format);
//        byte[] imgBytes = ImageUtil.getFormatDateImg(w, h, 100, format);
        imgBase64 = Base64.encodeBase64String(imgBytes);

        System.out.println("dateStr: " + dateStr  + "\nimgBase64: " + imgBase64);

    }

}
