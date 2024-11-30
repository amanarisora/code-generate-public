package com.util.codegenerate.codegenerate.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName("query")
@Data
public class Query {
    private String id;
    private String queryName;
    private String queryText;
    private String databaseName;
    private String datasourceName;
    private String user;
    private Date createTime;
    private Date editTime;
}
