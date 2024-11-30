package com.util.codegenerate.codegenerate.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@TableName("datasource_info")
public class DatasourceInfo {
    private String id;
    private String datasourceName;
    private Integer datasourceType;
    private String ip;
    private Integer port;
    private String username;
    private String password;
    private String sqliteFilePath;
    private String user;
    private Date createTime;
    private Date editTime;

    @TableField(exist = false)
    private String oldName;
    /**树相关*/
    @TableField(exist = false)
    private String title;
    @TableField(exist = false)
    private String key;
    /**0数据源1数据库*/
    @TableField(exist = false)
    private Integer type;
    @TableField(exist = false)
    private List<DatasourceDes> children = new ArrayList<>();
}
