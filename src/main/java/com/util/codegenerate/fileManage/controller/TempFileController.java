package com.util.codegenerate.fileManage.controller;

import com.util.codegenerate.common.Result;
import com.util.codegenerate.fileManage.entity.TempFile;
import com.util.codegenerate.fileManage.service.ITempFileService;
import com.util.codegenerate.fileManage.vo.PasteVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/tempFile")
public class TempFileController {
    @Autowired
    private ITempFileService tempFileService;


    @GetMapping("/getChildrenFolderAndFileList")
    public Result<?> getChildrenFolderAndFileList(@RequestParam String username,String parentId) {
        return Result.ok(tempFileService.getChildrenFolderAndFileList(username,parentId));
    }

    @PostMapping("/uploadTempFile")
    public Result<?> uploadTempFile(@RequestParam MultipartFile[] files,
                                     @RequestParam String username,
                                     @RequestParam Integer fileType,@RequestParam String folderId) {
        return Result.ok(tempFileService.uploadTempFile(files,username, fileType, folderId));
    }

    @PostMapping("/editTempFileContent")
    public Result<?> editTempFileContent(@RequestBody TempFile tempFile) {
        tempFileService.editTempFileContent(tempFile);
        return Result.ok();
    }

    @PostMapping("/renameTempFile")
    public Result<?> renameTempFile(@RequestBody TempFile tempFile) {
        tempFileService.renameTempFile(tempFile);
        return Result.ok();
    }

    @DeleteMapping("/deleteTempFileBatch")
    public Result<?> deleteTempFileBatch(@RequestBody List<String> ids) {
        tempFileService.deleteTempFileBatch(ids);
        return Result.ok();
    }

    @PostMapping("/pasteFolderAndTempFile")
    public Result<?> pasteFolderAndTempFile(@RequestBody PasteVO vo) {
        tempFileService.pasteFolderAndTempFile(vo);
        return Result.ok();
    }

    @GetMapping("/showTempFile")
    public void showTempFile(@RequestParam String id, HttpServletResponse response) {
        tempFileService.showTempFile(id,response);
    }


}
