package com.util.codegenerate.fileManage.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class FolderTempFileTreeVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String key;
    private String title;
    private String id;
    private String parentId;
    private String name;
    private Boolean isFile;
    private Boolean isRoot;
    /**属于什么*/
    private Integer type;

    private Boolean isEmpty;

    private List<FolderTempFileTreeVO> children = new ArrayList<>();

}
