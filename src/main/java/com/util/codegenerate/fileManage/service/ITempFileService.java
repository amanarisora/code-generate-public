package com.util.codegenerate.fileManage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.util.codegenerate.fileManage.entity.TempFile;
import com.util.codegenerate.fileManage.vo.FolderTempFileTreeVO;
import com.util.codegenerate.fileManage.vo.PasteVO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface ITempFileService extends IService<TempFile> {

    Map<String,List<FolderTempFileTreeVO>> getChildrenFolderAndFileList(String username, String parentId);

    List<String> uploadTempFile(MultipartFile[] files, String username,Integer fileType, String folderId);

    void editTempFileContent(TempFile tempFile);

    void renameTempFile(TempFile tempFile);

    void deleteTempFileBatch(List<String> ids);

    void pasteFolderAndTempFile(PasteVO vo);

    void showTempFile(String id, HttpServletResponse response);
}
