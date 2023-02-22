package com.it.reggie.controller;

import com.it.reggie.common.R;
import com.it.reggie.config.EmailConfig;
import com.it.reggie.entity.User;
import com.it.reggie.service.UserService;
import com.it.reggie.util.EmailUtil;
import com.it.reggie.util.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 发送验证码
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        String email = user.getEmail();
        if(email == null){
            return R.error("邮箱号为空!");
        }
        //随机生成4位验证码
        String code = String.valueOf(ValidateCodeUtils.generateValidateCode(4));
        log.info("数字验证码为: {}", code);

        //发送验证码到邮箱
        EmailUtil.sendAuthCodeEmail(email, code);
        return null;
    }
}
