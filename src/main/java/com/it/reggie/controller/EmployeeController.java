package com.it.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.reggie.common.R;
import com.it.reggie.entity.Employee;
import com.it.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    //注入service
    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){

        //1.将页面提交的密码用md5码进行加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2.根据页面提交的用户名查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername()); //双冒号用来对类中的方法进行引用
        Employee emp = employeeService.getOne(queryWrapper);

        //3.如果没有查询到则返回登陆失败结果
        if(emp == null){
            return R.error("登陆失败");
        }

        //4.密码比对，如果不一致则返回登陆失败
        if(!emp.getPassword().equals(password)){
            return R.error("登陆失败");
        }

        //5.查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if(emp.getStatus() == 0){
            return R.error("账号已禁用");
        }

        //6.登陆成功，将员工id存入session中，键名为employee对应值为Id
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee){
        log.info("新增员工, 员工信息: {}", employee.toString());

        //页面中没有密码输入框，设置初始密码123456并进行加密，后续员工可自行更改
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//
//        //获得当前登录用户的id
//        Long empId = (Long) request.getSession().getAttribute("employee");
//
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

        employeeService.save(employee);

        return R.success("新增员工成功");
    }

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("page = {}, pageSize = {}, name = {}", page, pageSize, name);
        //构造分页构造器
        Page pageInfo = new Page(page, pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件, 双冒号代表获取数据库中employee表的name字段名
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 根据id修改员工信息
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee){
        log.info(employee.toString());

        Long id = Thread.currentThread().getId();
        log.info("线程id: {}", id);

        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(empId);
        employeeService.updateById(employee);

        return R.success("员工信息修改成功");
    }

    /**
     * 编辑员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息...");
        Employee employee = employeeService.getById(id);
        if(employee != null){
            return R.success(employee);
        }
        return R.error("没有查询到对应员工信息");
    }
}
