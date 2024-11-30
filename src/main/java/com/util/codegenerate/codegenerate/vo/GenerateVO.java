package com.util.codegenerate.codegenerate.vo;

import lombok.Data;

import java.util.List;

@Data
public class GenerateVO {
    private String user;
    private String datasourceName;
    private String databaseName;
    private String model;
    private String packageName;
    private String author;
    private String tablePrefix;
    private Boolean needEntity = true;
    private String entityFilePathName;
    private Boolean needMapper = true;
    private String mapperFilePathName;
    private Boolean needController = true;
    private String controllerFilePathName;
    private Boolean needService = true;
    private String serviceFilePathName;
    private Boolean needServiceImpl = true;
    private String serviceImplFilePathName;
    private Boolean needLombok = true;
    private Boolean needChainModel = true;
    private Boolean needRestController = true;
    private Boolean needMapperAnno = false;
    private Boolean needSwagger = false;
    private Boolean needSpringDoc = true;
    private List<String> tableNameList;

}
