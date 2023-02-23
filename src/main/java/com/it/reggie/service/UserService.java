package com.it.reggie.service;

public interface UserService {
    /**
     * 发送邮箱
     * @param to
     * @param subject
     * @param context
     */
    void sendMsg(String to, String subject, String context);
}
