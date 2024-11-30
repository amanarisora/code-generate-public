package com.util.codegenerate.fileManage.vo;

import lombok.Data;

import java.util.List;

@Data
public class PasteVO {
    private List<String> folderIdList;
    private List<String> tempFileIdList;
    private String targetFolderId;
    private String oldParentId;
}
