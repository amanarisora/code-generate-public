package com.util.codegenerate.codegenerate.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class QueryTreeVO {
    private String key;
    private String title;
    /**0数据源1数据库2表 3 4 5查询*/
    private Integer type;
    private String parentId;
    private String datasourceName;
    private String queryText;
    private List<DataBaseTreeVO> children = new ArrayList<>();
}
