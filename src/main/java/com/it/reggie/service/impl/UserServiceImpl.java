package com.it.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.reggie.entity.User;
import com.it.reggie.mapper.UserMapper;
import com.it.reggie.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    //邮件发送人
    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendMsg(String to, String subject, String context) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(from);
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(context);

        //真正的发送邮件操作，从from到to
        mailSender.send(mailMessage);
    }
}
