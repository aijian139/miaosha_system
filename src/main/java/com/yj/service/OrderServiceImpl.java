package com.yj.service;

import com.alibaba.druid.sql.visitor.functions.If;
import com.yj.dao.OrderDao;
import com.yj.dao.StockDao;
import com.yj.dao.UserDao;
import com.yj.entity.Order;
import com.yj.entity.Stock;
import com.yj.entity.User;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {


    @Autowired
    private StockDao stockDao;


    @Autowired
    private OrderDao orderDao;


    @Autowired
    private UserDao userDao;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;



    @Override //synchronized
    public int kill(Integer id) {

        // 先验证 商品是否存在
        if(!stringRedisTemplate.hasKey("kill_"+id)){
            throw new RuntimeException("当前商品已经结束抢购！");
        }
        // 然后验证签名


        // 1.校验库存
        Stock stock = checkStock(id);
        // 2.扣除库存
        updateStock(stock);
        // 3.创建订单
        return createOrder(stock);
    }

    @Override
    public String getMd5(Integer id, String userId) {
        // 验证用户信息是否合法
        if(userDao.findUserById(userId) == null){
            throw new RuntimeException("用户信息不存在！");
        }
        // 验证商品信息是否合法
        if(stockDao.checkStock(id)==null){
            throw new RuntimeException("商品信息不存在！");
        }
        // 生成hashkey
        String key = "key_"+userId+"_"+id;
        // 盐
        String salt = "!@#$";

        String md5 = DigestUtils.md5DigestAsHex((userId+id+salt).getBytes());

        stringRedisTemplate.opsForValue().set(key, md5,120, TimeUnit.MINUTES);

        return md5;
    }

    @Override
    public int kill(Integer id, String userId, String md5) {

        // 先验证 商品是否存在
        if(!stringRedisTemplate.hasKey("kill_"+id)){
            throw new RuntimeException("当前商品已经结束抢购！");
        }
        // 然后验证签名

        String key = "key_"+userId+"_"+id;
        String salt = "!@#$";
        String s = stringRedisTemplate.opsForValue().get(key);
        if(s==null) {
            throw new RuntimeException("当前签名不合法，请稍后再试！");
        }
        if(!s.equals(md5)){
            throw new RuntimeException("当前请求不合法，请稍后再试！");
        }


        // 1.校验库存
        Stock stock = checkStock(id);
        // 2.扣除库存
        updateStock(stock);
        // 3.创建订单
        return createOrder(stock);


    }





    // 检验库存
    private Stock checkStock(Integer id) {
        Stock stock = stockDao.checkStock(id);
        if (stock.getCount().equals(stock.getSale())) {
            throw new RuntimeException("库存不足！！！");
        }
        return stock;
    }

    // 更新库存
    private void updateStock(Stock stock) {

        //stock.setSale(stock.getSale() + 1);
        int resultRow =  stockDao.updateStock(stock);
        if(resultRow==0){
            throw new RuntimeException("抢购失败，请重新抢购");
        }
    }

    // 更新订单
    private Integer createOrder(Stock stock) {
        Order order = new Order();
        order.setSid(stock.getId()).setName(stock.getName()).setCreate_time(new Date());
        orderDao.createOrder(order);
        return Integer.parseInt(order.getId());
    }
}