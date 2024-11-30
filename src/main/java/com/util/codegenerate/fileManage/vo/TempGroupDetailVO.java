package com.util.codegenerate.fileManage.vo;

import lombok.Data;

@Data
public class TempGroupDetailVO {

    private String id;

    private String controllerTempId;
    private String controllerTempName;

    private String entityTempId;
    private String entityTempName;

    private String mapperTempId;
    private String mapperTempName;

    private String xmlTempId;
    private String xmlTempName;

    private String serviceTempId;
    private String serviceTempName;

    private String serviceImplTempId;
    private String serviceImplTempName;
}
