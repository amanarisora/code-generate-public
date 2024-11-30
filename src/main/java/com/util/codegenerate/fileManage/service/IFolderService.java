package com.util.codegenerate.fileManage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.util.codegenerate.fileManage.entity.Folder;
import com.util.codegenerate.fileManage.vo.FolderTempFileTreeVO;

import java.util.List;

public interface IFolderService extends IService<Folder> {
    List<FolderTempFileTreeVO> getAllFolderTree(String username);

    List<FolderTempFileTreeVO> reloadFolderTree(String id,String username);

    void addFolder(Folder folder);

    void editFolderName(Folder folder);

    void deleteFolder(String id);

}
