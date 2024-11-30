package com.util.codegenerate.user.controller;

import com.util.codegenerate.common.Result;
import com.util.codegenerate.user.entity.User;
import com.util.codegenerate.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    private IUserService userService;
    @GetMapping("/login")
    public Result<?> login(String username,String password){
        boolean login = userService.login(username, password);
        if (login){
            return Result.ok();
        }else {
            return Result.error("用户名或密码错误！");
        }
    }
    @PostMapping("/signUp")
    public Result<?> signUp(@RequestBody User user){
        boolean login = userService.signUp(user);
        if (login){
            return Result.ok();
        }else {
            return Result.error("注册失败，用户名已存在！");
        }
    }
}
