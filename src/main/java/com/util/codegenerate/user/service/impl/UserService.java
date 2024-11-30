package com.util.codegenerate.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.util.codegenerate.user.entity.User;
import com.util.codegenerate.user.mapper.UserMapper;
import com.util.codegenerate.codegenerate.service.ICodeGenerateService;
import com.util.codegenerate.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    private ICodeGenerateService codeGenerateService;
    @Override
    public boolean login(String username, String password) {
        User one = getOne(Wrappers.lambdaQuery(User.class).eq(User::getUsername, username).eq(User::getPassword, password));
        if (one !=null){
            codeGenerateService.initDataSource(username);
            return true;
        }
        return false;
    }

    @Override
    public boolean signUp(User user) {
        boolean login = login(user.getUsername(), user.getPassword());
        if (!login){
            save(user);
            return true;
        }
        return false;
    }
}
