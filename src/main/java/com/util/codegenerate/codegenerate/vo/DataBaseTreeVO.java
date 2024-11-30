package com.util.codegenerate.codegenerate.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DataBaseTreeVO {
    private String key;
    private String title;
    /**0数据源1数据库2表*/
    private Integer type;
    private String parentId;
    private List<DataBaseTreeVO> children = new ArrayList<>();
}
