package com.leyou.user.controller;

import com.leyou.user.pojo.User;
import com.leyou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 对手机号、用户名的唯一性校验。
     * @param data 手机号/用户名
     * @param type 类型 1用户名 2手机号码
     * @return
     */
    @GetMapping("check/{data}/{type}")
    public ResponseEntity<Boolean> checkUser(@PathVariable("data") String data,@PathVariable("type")Integer type){
        Boolean bool=this.userService.checkUser(data,type);
        if(bool==null){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(bool);
    }

    /**
     * 根据手机号码获取验证码
     * @param phone 用户输入的手机号
     * @return
     */
    @PostMapping("code")
    public ResponseEntity<Void> sendVerifyCode(@RequestParam("phone") String phone){

        Boolean bool=this.userService.sendVerifyCode(phone);
        if (bool == null || !bool) {
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 用户注册并判断验证码输入是否正确
     * @param user 用户信息
     * @param code 验证码
     * @return
     */
    @PostMapping("register")
    public ResponseEntity<Void> registerUser(@Valid User user, @RequestParam("code") String code){
        Boolean bool=this.userService.registerUser(user,code);
        //判断是否添加成功
        if(bool==null || !bool){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    /**
     * 根据用户名跟密码查询用户信息
     * @param userName 用户名
     * @param password 密码
     * @return 查询到用户的JSON格式的字符串
     */
    @ResponseBody
    @GetMapping("query")
    public ResponseEntity<User> queryUser(@RequestParam("username")String userName,@RequestParam("password")String password){

        User user=this.userService.queryUser(userName,password);
        if (user == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(user);

    }
}
