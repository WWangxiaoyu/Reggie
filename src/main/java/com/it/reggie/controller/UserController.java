package com.it.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.it.reggie.common.R;
import com.it.reggie.entity.User;
import com.it.reggie.service.UserService;
import com.it.reggie.util.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

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
            String context = "欢迎使使用瑞吉，登录验证码为: " + code + ", 五分钟内有效，请妥善保管!";
            log.info("code={}", code);

            //真正的发送邮箱验证码
            userService.sendMsg(email, subject, context);

            //将随机生成的验证码保存到session中
//            session.setAttribute(email, code);

            //将生成的验证码缓存到Redis中，并设置有效期为5分钟
            redisTemplate.opsForValue().set(email, code, 5, TimeUnit.HOURS);

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
    public R<User> login(@RequestBody Map map, HttpSession session){
        log.info(map.toString());

        //获取邮箱
        String email = map.get("email").toString();

        //获取验证码
        String code = map.get("code").toString();

        //从session中获取保存的验证码
//        Object codeInSession = session.getAttribute(email);

        //丛Redis中获取缓存的验证码
        Object codeInSession = redisTemplate.opsForValue().get(email);

        //进行验证码的对比（页面提交的和session中的验证码）
        if(codeInSession != null && codeInSession.equals(code)){
            //比对成功-->登陆成功
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getEmail, email);

            User user = userService.getOne(queryWrapper);
            if(user == null){
                //判断当前邮箱对应的是否是新用户，是就自动注册
                user = new User();
                user.setEmail(email);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());

            //如果用户登陆成功，删除redis中缓存的验证码
            redisTemplate.delete(email);

            return R.success(user);
        }
        return R.error("登录失败");
    }
}
