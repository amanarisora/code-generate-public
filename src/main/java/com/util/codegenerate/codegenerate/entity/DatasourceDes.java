package com.util.codegenerate.codegenerate.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@TableName("datasource")
public class DatasourceDes implements IDataSourceProp{

    private String id;
    private String name;
    private String url;
    private String username;
    private String password;
    private String user;

    @TableField(exist = false)
    private String oldName;
    @TableField(exist = false)
    private String driverType;
    /**树相关*/
    @TableField(exist = false)
    private String title;
    @TableField(exist = false)
    private String key;
    /**0数据源1数据库*/
    @TableField(exist = false)
    private String type;
    @TableField(exist = false)
    private List<DatasourceDes> children = new ArrayList<>();
}
