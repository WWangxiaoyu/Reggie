package com.it.reggie.util;

import com.it.reggie.config.EmailConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 发送邮箱验证码工具类
 */
@Component
public class EmailUtil {

    @Autowired
    private JavaMailSender mailSender;

    public void sendAuthCodeEmail(String toEmail){
        String from = "461088277@qq.com";
        //创建邮件
        SimpleMailMessage message = new SimpleMailMessage();
        //设置发件人信息
        message.setFrom(from);
        //发给谁
        message.setTo(toEmail);
        message.setSubject("您本次的验证码是");
        //生成六位随机验证码
        String validateCode = ValidateCodeUtils.generateValidateCode(4);
        TimeAndValidateCode.validateCodeMap.put(toEmail, validateCode);
        System.out.println(validateCode);
        //获得当前时间
        TimeAndValidateCode.currentTimeMap.put(toEmail, new Date());
    }
}
