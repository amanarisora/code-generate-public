package com.util.codegenerate.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("user")
@Data
public class User {
    private String username;
    private String password;
}
