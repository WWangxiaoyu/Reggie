package com.it.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.reggie.common.BaseContext;
import com.it.reggie.common.CustomException;
import com.it.reggie.entity.AddressBook;
import com.it.reggie.entity.Orders;
import com.it.reggie.entity.ShoppingCart;
import com.it.reggie.entity.User;
import com.it.reggie.mapper.OrderMapper;
import com.it.reggie.service.AddressBookService;
import com.it.reggie.service.OrderService;
import com.it.reggie.service.ShoppingCartService;
import com.it.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 用户下单
     * @param orders
     */
    @Transactional  //要对数据库中多个表进行操作，所以要加上此注解进行事务控制
    public void submit(Orders orders){
        //获得当前用户id
        Long userId = BaseContext.getCurrentId();

        //查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getId, userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(wrapper);

        if(shoppingCarts == null || shoppingCarts.size() == 0){
            throw new CustomException("购物车为空，不能下单");
        }

        //查询用户数据
        User user = userService.getById(userId);

        //查询地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if(addressBook == null){
            throw new CustomException("用户地址信息有误，不能下单");
        }

        long orderId = IdWorker.getId();    //生成订单号

        //向订单表插入数据，一条数据
        orders.setNumber(String.valueOf(orderId));

        this.save(orders);

        //向订单明细表插入数据，多条数据

        //清空购物车数据
    }
}
