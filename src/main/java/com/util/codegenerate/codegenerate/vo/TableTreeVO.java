package com.util.codegenerate.codegenerate.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TableTreeVO {
    private String key;
    private String title;
    /**0数据源1数据库2表 3 4 5查询*/
    private Integer type;
    private String parentId;
    private String datasourceName;
    private String tableComment;
    private String tableCreatTime;
    private List<DataBaseTreeVO> children = new ArrayList<>();
}
