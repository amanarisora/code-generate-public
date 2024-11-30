package com.util.codegenerate.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.util.codegenerate.user.entity.User;

public interface IUserService extends IService<User> {
    boolean login(String username,String password);
    boolean signUp(User user);
}
