package com.yj.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public int saveUserId(String userId) {
        // 根据不同用户id 生成调用次数的key
        String limitKey = "limit_"+userId;

        String limitNum = stringRedisTemplate.opsForValue().get(limitKey);
        int limit = 0;
        if(limitNum == null){
            stringRedisTemplate.opsForValue().set(limitKey, "0",3600, TimeUnit.SECONDS);
        }else{
            limit += 1;
            stringRedisTemplate.opsForValue().increment(limitKey);
        }


        return limit;
    }

    @Override
    public boolean getUserIdCount(String userId) {
        String limitKey = "limit_"+userId;
        String limitNum = stringRedisTemplate.opsForValue().get(limitKey);

        if(limitNum == null){
            // 为空说明key 出现异常
            return true;
        }

        return Integer.parseInt(limitNum) > 10; //false 代表没有超过 ，true 代表超过
    }
}
