package com.yj.dao;

import com.yj.entity.User;

public interface UserDao {
    User findUserById(String id);
}
