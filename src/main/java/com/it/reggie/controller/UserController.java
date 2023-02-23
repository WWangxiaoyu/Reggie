package com.it.reggie.controller;

import com.it.reggie.common.R;
import com.it.reggie.entity.User;
import com.it.reggie.service.UserService;
import com.it.reggie.util.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

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
        //获取邮箱账号
        String email = user.getEmail();

        String subject = "本次登录验证码";
        if(StringUtils.isNotEmpty(email)){
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            String context = "欢迎使用瑞吉餐购，登录验证码为: " + code + ", 五分钟内有效，请妥善保管!";
            log.info("code={}", code);

            //真正的发送邮箱验证码
            userService.sendMsg(email, subject, context);

            //将随机生成的验证码保存到session中
            session.setAttribute(email, code);

            return R.success("验证码发送成功，请及时查看!");
        }
        return R.error("验证码发送失败, 请重新输入!");
    }

    /**
     * 移动端用户登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<String> login(@RequestBody Map map, HttpSession session){
        log.info(map.toString());

        //

        return R.error("短信发送失败");
    }
}
