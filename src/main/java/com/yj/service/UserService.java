package com.yj.service;

public interface UserService {
    // 向redis 存储用户 id 访问次数
    int saveUserId(String userId);

    // 从redis 中获取用户id 访问次数
    boolean getUserIdCount(String userId);
}
