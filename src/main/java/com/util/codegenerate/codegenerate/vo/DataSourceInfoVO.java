package com.util.codegenerate.codegenerate.vo;

import com.util.codegenerate.codegenerate.entity.IDataSourceProp;
import lombok.Data;

@Data
public class DataSourceInfoVO implements IDataSourceProp {
    private String id;
    private String user;
    private String oldName;
    private String name;
    private String url;
    private String username;
    private String password;
    private String driverType;
}
