package com.leyou.user.service.impl;

import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.service.UserService;
import com.leyou.user.utils.CodecUtils;
import com.leyou.utils.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private AmqpTemplate amqpTemplate;

    private static final String KEY_PREFIX = "user:code:phone:";

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

    @Override
    public Boolean sendVerifyCode(String phone) {
        if (StringUtils.isBlank(phone)){
            return false;
        }
        try {
            // 生成验证码
            String code = NumberUtils.generateCode(6);

            //将code存入redis
            this.stringRedisTemplate.opsForValue().set(KEY_PREFIX+phone,code,5, TimeUnit.MINUTES);

            //发送短信
            Map<String,String> msg=new HashMap<>();
            msg.put("phone",phone);
            msg.put("code",code);
            this.amqpTemplate.convertAndSend("LEYOU.SMS.EXCHANGE","verifycode.sms",msg);
            return true;
        } catch (AmqpException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean registerUser(User user, String code) {

        //取出redis中保存的验证码
        String cacheCode = this.stringRedisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
        // 校验短信验证码
        if (!StringUtils.equals(cacheCode,code)){
            return false;
        }
        //生成盐
        String salt = CodecUtils.generateSalt();

        //对密码加密,加密赋值给user对象保存到数据库
        user.setPassword(CodecUtils.md5Hex(user.getPassword(),salt));
        //写入数据库
        user.setSalt(salt);
        user.setCreated(new Date());
        user.setId(null);

        boolean result=this.userMapper.insertSelective(user) == 1;
        //判断是否添加成功
        if(result){
            //删除Redis中的验证码
            this.stringRedisTemplate.delete(KEY_PREFIX + user.getPhone());
        }

        return result;

    }

    @Override
    public User queryUser(String userName, String password) {
        User record=new User();
        record.setUsername(userName);
        //用用户名查询出改对象，好获取盐进行密码的比较
        User user = this.userMapper.selectOne(record);
        if(user == null){
            return null;
        }
        //密码校验
        if (!(StringUtils.equals(user.getPassword(),CodecUtils.md5Hex(password,user.getSalt())))){
            return null;
        }
        return user;

    }
}
