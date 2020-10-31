package com.yj.service;

public interface OrderService {
    int kill(Integer id);

    String getMd5(Integer id, String userId);

    //用来 处理秒杀的下单方法 并放回订单id 加入 MD5 接口隐藏
    int kill(Integer id, String userId, String md5);
}
