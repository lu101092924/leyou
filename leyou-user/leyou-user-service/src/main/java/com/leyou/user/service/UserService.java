package com.leyou.user.service;

import com.leyou.user.pojo.User;

public interface UserService {
    Boolean checkUser(String data, Integer type);

    Boolean sendVerifyCode(String phone);

    Boolean registerUser(User user, String code);

    User queryUser(String userName, String password);
}
