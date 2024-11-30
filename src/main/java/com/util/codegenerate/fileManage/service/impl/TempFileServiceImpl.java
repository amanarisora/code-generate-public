package com.util.codegenerate.fileManage.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.util.codegenerate.fileManage.entity.Folder;
import com.util.codegenerate.fileManage.entity.TempFile;
import com.util.codegenerate.fileManage.mapper.FolderMapper;
import com.util.codegenerate.fileManage.mapper.TempFileMapper;
import com.util.codegenerate.fileManage.service.ITempFileService;
import com.util.codegenerate.fileManage.vo.FolderTempFileTreeVO;
import com.util.codegenerate.fileManage.vo.PasteVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.util.codegenerate.fileManage.service.impl.FolderServiceImpl.getBaseResult;
import static com.util.codegenerate.utils.CommonUtil.getSnowflakeId;
@DS("w51sa35Mz9AW")
@Service
public class TempFileServiceImpl extends ServiceImpl<TempFileMapper, TempFile> implements ITempFileService {
    @Autowired
    private FolderMapper folderMapper;
    @Autowired
    private TempFileMapper tempFileMapper;
    private final Logger logger = LoggerFactory.getLogger(TempFileServiceImpl.class);

    @Override
    public Map<String, List<FolderTempFileTreeVO>> getChildrenFolderAndFileList(String username, String parentId) {
        if (parentId == null){
            List<FolderTempFileTreeVO> baseFolderList = getBaseResult();
            return new HashMap<>() {{
                put("folderList", baseFolderList);
                put("fileList",new ArrayList<>());
            }};
        }
        List<FolderTempFileTreeVO> folderList = folderMapper.selectChildrenFolder(parentId, username);
        List<FolderTempFileTreeVO> fileList = tempFileMapper.selectChildrenFile(parentId, username);

        return new HashMap<>() {{
            put("folderList", folderList);
            put("fileList",fileList);
        }};
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<String> uploadTempFile(MultipartFile[] files, String username, Integer fileType, String folderId) {
        List<TempFile> fileList = new ArrayList<>();
        Map<String,TempFile> tempFileMap = new HashMap<>();
        for (MultipartFile file : files) {
            TempFile tempFile = new TempFile();
            tempFile.setId(getSnowflakeId());
            try {
                tempFile.setFile(new String(file.getBytes(), StandardCharsets.UTF_8));
            } catch (IOException e) {
                logger.error("上传失败",e);
                throw new RuntimeException("上传失败");
            }
            tempFile.setFileName(file.getOriginalFilename());
            tempFile.setFileType(fileType);
            tempFile.setUsername(username);
            tempFile.setUploadTime(new Date());
            tempFile.setFolderId(folderId);
            fileList.add(tempFile);
            tempFileMap.put(tempFile.getFileName(),tempFile);
        }
        List<String> collect = fileList.stream().map(TempFile::getFileName).collect(Collectors.toList());
        List<TempFile> duplicate = list(Wrappers.lambdaQuery(TempFile.class).select(TempFile::getFileName)
                .in(TempFile::getFileName, collect).eq(TempFile::getUsername, username)
                .eq(TempFile::getFileType, fileType).eq(TempFile::getFolderId, folderId));
        for (TempFile tempFile : duplicate) {
            String oldFileName = tempFile.getFileName();
            TempFile currentTemp = tempFileMap.get(tempFile.getFileName());
            do {
                handleDuplicateFileName(currentTemp);
            } while (tempFileMap.containsKey(currentTemp.getFileName()));
            tempFileMap.remove(oldFileName);
            tempFileMap.put(currentTemp.getFileName(),currentTemp);
        }
        ((TempFileServiceImpl) AopContext.currentProxy()).saveBatch(fileList);
        return fileList.stream().map(TempFile::getId).collect(Collectors.toList());
    }

    public void handleDuplicateFileName(TempFile tempFile){
        if (tempFile == null){
            return;
        }
        String fileName = tempFile.getFileName();

        long duplicate = 1;
        while (duplicate !=0){
            fileName += "-副本";
            duplicate = count(Wrappers.lambdaQuery(TempFile.class).select(TempFile::getFileName)
                    .eq(TempFile::getFileName, fileName).eq(TempFile::getUsername, tempFile.getUsername())
                    .eq(TempFile::getFileType, tempFile.getFileType()).eq(TempFile::getFolderId, tempFile.getFolderId()));
        }
        tempFile.setFileName(fileName);
    }

    public void checkAndHandleDuplicateFileName(List<TempFile> tempFileList){
        if (tempFileList == null || tempFileList.isEmpty()){
            return;
        }
        List<String> collect = tempFileList.stream().map(TempFile::getFileName).collect(Collectors.toList());
        Set<String> set = list(Wrappers.lambdaQuery(TempFile.class).select(TempFile::getFileName)
                .in(TempFile::getFileName, collect).eq(TempFile::getUsername, tempFileList.get(0).getUsername())
                .eq(TempFile::getFolderId, tempFileList.get(0).getFolderId())).stream().map(TempFile::getFileName).collect(Collectors.toSet());

        for (TempFile tempFile : tempFileList) {
            if (set.contains(tempFile.getFileName())){
                handleDuplicateFileName(tempFile);
            }
        }
    }

    @Override
    public void editTempFileContent(TempFile tempFile) {
        TempFile oldOne = getById(tempFile.getId());
        if (oldOne == null) {
            throw new RuntimeException("模板文件不存在");
        }
        oldOne.setFile(tempFile.getFileContent());
        updateById(oldOne);
    }

    @Override
    public void renameTempFile(TempFile tempFile) {
        TempFile oldOne = getByIdWithoutFile(tempFile.getId());
        if (oldOne == null) {
            throw new RuntimeException("模板文件不存在");
        }
        long count = count(Wrappers.lambdaQuery(TempFile.class).select(TempFile::getFileName)
                .eq(TempFile::getFileName, tempFile.getFileName()).eq(TempFile::getUsername, oldOne.getUsername())
                .eq(TempFile::getFileType, oldOne.getFileType()).eq(TempFile::getFolderId, oldOne.getFolderId()));
        if (count > 0) {
            throw new RuntimeException("存在重名文件");
        }
        update(Wrappers.lambdaUpdate(TempFile.class).eq(TempFile::getId, oldOne.getId()).set(TempFile::getFileName,tempFile.getFileName())
                .set(TempFile::getEditTime,new Date()));
    }

    @Override
    public void deleteTempFileBatch(List<String> ids) {
        removeByIds(ids);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void pasteFolderAndTempFile(PasteVO vo) {
        List<Folder> allChildrenFolderAndSelf = new ArrayList<>();
        List<TempFile> fileList = listByIds(vo.getTempFileIdList());
        List<TempFile> fileListInFolder = new ArrayList<>();
        Map<String,List<TempFile>> parentIdTempFileMap = new HashMap<>();
        Map<String,List<Folder>> parentIdFolderMap = new HashMap<>();
        Map<String,Folder> folderMap = new HashMap<>();

        if (vo.getFolderIdList() !=null && !vo.getFolderIdList().isEmpty()){
            allChildrenFolderAndSelf = folderMapper.getAllChildrenFolderAndSelf(vo.getFolderIdList());
            List<String> allFolderIds = new ArrayList<>();
            for (Folder folder : allChildrenFolderAndSelf) {
                allFolderIds.add(folder.getId());
                if (parentIdFolderMap.containsKey(folder.getParentId())) {
                    parentIdFolderMap.get(folder.getParentId()).add(folder);
                }else {
                    parentIdFolderMap.put(folder.getParentId(),new ArrayList<>(){{
                        add(folder);
                    }});
                }
                folderMap.put(folder.getId(),folder);
            }
            if (!allFolderIds.isEmpty()){
                fileListInFolder.addAll(list(Wrappers.lambdaQuery(TempFile.class).in(TempFile::getFolderId,allFolderIds)));
            }
        }
        for (TempFile tempFile : fileListInFolder) {
            if (parentIdTempFileMap.containsKey(tempFile.getFolderId())) {
                parentIdTempFileMap.get(tempFile.getFolderId()).add(tempFile);
            }else {
                parentIdTempFileMap.put(tempFile.getFolderId(),new ArrayList<>(){{
                    add(tempFile);
                }});
            }
            tempFile.setId(getSnowflakeId());
        }

        for(TempFile tempFile : fileList){
            tempFile.setId(getSnowflakeId());
            tempFile.setFolderId(vo.getTargetFolderId());
        }

        for(Map.Entry<String,List<Folder>> entry : parentIdFolderMap.entrySet()){
            String newParentId = getSnowflakeId();
            if (entry.getKey().equals(vo.getOldParentId())){
                newParentId = vo.getTargetFolderId();
            }
            for (Folder folder : entry.getValue()) {
                folder.setParentId(newParentId);
            }
            Folder folder = folderMap.get(entry.getKey());
            List<TempFile> filesInFolder = parentIdTempFileMap.get(folder.getId());
            if (filesInFolder != null) {
                for (TempFile tempFile : filesInFolder) {
                    tempFile.setFolderId(newParentId);
                }
            }
            folder.setId(newParentId);
            folderMap.remove(entry.getKey());
        }
        for(Map.Entry<String,Folder> entry : folderMap.entrySet()){
            String newId = getSnowflakeId();
            Folder folder = entry.getValue();
            folder.setId(newId);
            List<TempFile> filesInFolder = parentIdTempFileMap.get(folder.getId());
            if (filesInFolder != null) {
                for (TempFile tempFile : filesInFolder) {
                    tempFile.setFolderId(newId);
                }
            }
        }
        checkAndHandleDuplicateFileName(fileList);
        fileList.addAll(fileListInFolder);
        folderMapper.insert(allChildrenFolderAndSelf);
        ((TempFileServiceImpl)AopContext.currentProxy()).saveBatch(fileList);
    }

    @Override
    public void showTempFile(String id, HttpServletResponse response) {
        TempFile tempFile = getById(id);
        if (tempFile == null){
            throw new RuntimeException("模板不存在");
        }
        // 设置响应头
        response.setContentType("text/plain; charset=UTF-8"); // 设置为文本类型
        response.setHeader("Content-Disposition", "attachment; filename=\"" + tempFile.getFileName() + "\"");
        response.setContentLength(tempFile.getFile().length());

        try (PrintWriter pw = response.getWriter()) {
            pw.write(tempFile.getFile());
            pw.flush();
        } catch (IOException e) {
            throw new RuntimeException("获取模板文件失败");
        }
    }


    public TempFile getByIdWithoutFile(Serializable id) {

        return getOne(Wrappers.lambdaQuery(TempFile.class).select(
                TempFile::getId,
                TempFile::getFolderId,
                TempFile::getFileName,
                TempFile::getFileType,
                TempFile::getUsername,
                TempFile::getUploadTime,
                TempFile::getEditTime).eq(TempFile::getId, id));
    }
}
