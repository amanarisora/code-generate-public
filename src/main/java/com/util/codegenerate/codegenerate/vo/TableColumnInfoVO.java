package com.util.codegenerate.codegenerate.vo;

import lombok.Data;

@Data
public class TableColumnInfoVO {
    private String columnName;
    private String columnDefault;
    private String isNullable;
    private String dataType;
    private String columnType;
    private String columnKey;
    private String columnComment;
}
