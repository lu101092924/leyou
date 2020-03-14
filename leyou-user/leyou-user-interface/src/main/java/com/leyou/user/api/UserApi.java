package com.leyou.user.api;


import com.leyou.user.pojo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public interface UserApi {

    @GetMapping("query")
    public User queryUser(@RequestParam("username")String userName, @RequestParam("password")String password);
}
