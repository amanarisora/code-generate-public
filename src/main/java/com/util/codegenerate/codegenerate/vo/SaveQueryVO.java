package com.util.codegenerate.codegenerate.vo;

import lombok.Data;

@Data
public class SaveQueryVO {
    private String queryName;
    private String queryText;
    private String databaseName;
    private String datasourceName;
    private String user;
    private Boolean isNewQuery;
}
