package com.util.codegenerate.codegenerate.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SqlRunResultVO {
    private String sql;
    private Integer affectedRowNumber;
    private String sqlType;
    private List<Map<String,Object>> selectResultList;
    private List<TableColumnVueVO> columnVueList;
}
