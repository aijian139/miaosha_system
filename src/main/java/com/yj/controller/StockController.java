package com.yj.controller;

import com.google.common.util.concurrent.RateLimiter;
import com.yj.service.OrderService;
import com.yj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("stock")
@Slf4j
public class StockController {



    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;


    // 设置令牌桶 中令牌的个数
    private RateLimiter rateLimiter = RateLimiter.create(20);


    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }

    //令牌桶 的简单使用
    @GetMapping("sale")
    public String sale(Integer id){
        double acquire = rateLimiter.acquire();
        //log.info("每秒等待的时间：[{}]",acquire);
        if(rateLimiter.tryAcquire(2, TimeUnit.SECONDS)){
            System.out.println("当前请求被限流,直接抛弃,无法调用后续秒杀逻辑....");
            return "抢购失败！";
        }
        System.out.println("业务逻辑代码执行");
        return "成功";
    }


    // 根据id 秒杀商品
    @GetMapping("kill")
    public String kill(Integer id){
        System.out.println(id);
        try {
            int orderId;
//            synchronized (this) {
                // 根据秒杀的id, 调用秒杀业务
                orderId = orderService.kill(id);
//            }
            // 秒杀成功，返回一个订单号
            return "秒杀成功，订单id为:"+String.valueOf(orderId);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
    //开发一个秒杀方法 乐观锁防止超卖+ 令牌桶算法限流令牌桶 加 乐观锁

    @GetMapping("killToken")
    public String killToken(Integer id){
        if(!rateLimiter.tryAcquire(3, TimeUnit.SECONDS)){
            System.out.println("请求超时，抢购失败！请重新再试！");
            return "请求超时，抢购失败！请重新再试！";
        }
        try {
            int orderId = orderService.kill(id);
            return "秒杀成功，订单id为:"+String.valueOf(orderId);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

       //开发一个秒杀方法 乐观锁防止超卖+ 令牌桶算法限流令牌桶 加 乐观锁 + md5 接口隐藏

    @GetMapping("killTokenMd5")
    public String killTokenMd5(Integer id,String userId,String md5){
        if(!rateLimiter.tryAcquire(3, TimeUnit.SECONDS)){
            System.out.println("请求超时，抢购失败！请重新再试！");
            return "请求超时，抢购失败！请重新再试！";
        }
        try {
            int orderId = orderService.kill(id,userId,md5);
            return "秒杀成功，订单id为:"+String.valueOf(orderId);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    //开发一个秒杀方法 防止超卖+ 令牌桶算法限流令牌桶 加 乐观锁 + md5 接口隐藏 + 访问次数限制

    @GetMapping("killTokenMd5Limit")
    public String killTokenMd5Limit(Integer id,String userId,String md5){
        if(!rateLimiter.tryAcquire(3, TimeUnit.SECONDS)){
            System.out.println("请求超时，抢购失败！请重新再试！");
            return "请求超时，抢购失败！请重新再试！";
        }
        try {
            // 加入单用户限制频率
            int count = userService.saveUserId(userId);
            boolean fboolean = userService.getUserIdCount(userId);
            if(fboolean){
                return "购买失败！访问超过频率限制！";
            }

            int orderId = orderService.kill(id,userId,md5);
            return "秒杀成功，订单id为:"+String.valueOf(orderId);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

     // 获取md5
    @RequestMapping("md5")
    public String getMd5(Integer id,String userId){
        try {
            String md5 = orderService.getMd5(id,userId);
            return md5;
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

}
