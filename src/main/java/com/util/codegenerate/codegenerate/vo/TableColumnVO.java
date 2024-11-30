package com.util.codegenerate.codegenerate.vo;

import lombok.Data;

@Data
public class TableColumnVO {
    private String id;
    private String tableName;
    private String editTime;
    private String dataLength;
    private String engine;
    private String tableRows;
    private String tableComment;
    private String createTime;
}
