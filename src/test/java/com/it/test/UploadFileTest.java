package com.it.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.Test;

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

    @Test
    public void testDate(){
        //获得对应格式的日期
        String format = "yyyy/MM";
        DateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date();
        String dateStr = dateFormat.format(date);

        //base64编码生成
        String imgBase64;
        byte[] imgBytes = ImageUtil.getFormatDateImg(214f * 4f, 32f * 4f, 100, format);
        imgBase64 = Base64.encodeBase64String(imgBytes);

        System.out.println("dateStr: " + dateStr  + "\nimgBase64: " + imgBase64);

    }

}
