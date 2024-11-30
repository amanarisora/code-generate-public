package com.util.codegenerate.fileManage.controller;

import com.util.codegenerate.common.Result;
import com.util.codegenerate.fileManage.entity.Folder;
import com.util.codegenerate.fileManage.service.IFolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/folder")
public class FolderController {
    @Autowired
    private IFolderService folderService;

    @GetMapping("/getAllFolderTree")
    public Result<?> getAllFolderTree(@RequestParam String username){
        return Result.ok(folderService.getAllFolderTree(username));
    }

    @GetMapping("/reloadFolderTree")
    public Result<?> reloadFolderTree(@RequestParam String id,@RequestParam String username){
        return Result.ok(folderService.reloadFolderTree(id,username));
    }

    @PostMapping("/addFolder")
    public Result<?> addFolder(@RequestBody Folder folder){
        folderService.addFolder(folder);
        return Result.ok();
    }

    @PostMapping("/editFolderName")
    public Result<?> editFolderName(@RequestBody Folder folder){
        folderService.editFolderName(folder);
        return Result.ok();
    }

    @DeleteMapping("/deleteFolder")
    public Result<?> deleteFolder(@RequestParam String id){
        folderService.deleteFolder(id);
        return Result.ok();
    }

}
