package com.leyou.user.service.impl;

import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Boolean checkUser(String data, Integer type) {
        User user=new User();
        if (type == 1){
            user.setUsername(data);
        }else if(type == 2){
            user.setPhone(data);
        }else {
            return null;
        }
       return this.userMapper.selectCount(user) == 0;

    }
}
