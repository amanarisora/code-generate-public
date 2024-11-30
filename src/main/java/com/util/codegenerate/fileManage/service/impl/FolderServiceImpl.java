package com.util.codegenerate.fileManage.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.util.codegenerate.fileManage.entity.Folder;
import com.util.codegenerate.fileManage.entity.TempFile;
import com.util.codegenerate.fileManage.mapper.FolderMapper;
import com.util.codegenerate.fileManage.mapper.TempFileMapper;
import com.util.codegenerate.fileManage.service.IFolderService;
import com.util.codegenerate.fileManage.vo.FolderTempFileTreeVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

import static com.util.codegenerate.utils.CommonUtil.getSnowflakeId;


@Service
public class FolderServiceImpl extends ServiceImpl<FolderMapper, Folder> implements IFolderService {
    @Resource
    private TempFileMapper tempFileMapper;
    @Resource
    private FolderMapper folderMapper;
    @Override
    public List<FolderTempFileTreeVO> getAllFolderTree(String username) {
        List<FolderTempFileTreeVO> result = getBaseResult();
        List<FolderTempFileTreeVO> fileTreeVOList = folderMapper.selectAllFolder(username);
        fileTreeVOList.addAll(result);

        Map<String,FolderTempFileTreeVO> folderTempFileTreeVOMap = new HashMap<>();
        for (FolderTempFileTreeVO folder : fileTreeVOList) {
            folderTempFileTreeVOMap.put(folder.getKey(), folder);
        }
        folderTempFileTreeVOMap.forEach((key,value)->{
            if (StringUtils.isNotBlank(value.getParentId())){
                folderTempFileTreeVOMap.get(value.getParentId()).getChildren().add(value);
            }
        });
        return result;
    }

    @Override
    public List<FolderTempFileTreeVO> reloadFolderTree(String id, String username) {
        return folderMapper.selectChildrenFolder(id, username);
    }

    @Override
    public void addFolder(Folder folder) {
        long count = count(Wrappers.lambdaQuery(Folder.class).eq(Folder::getFolderName, folder.getFolderName())
                .eq(Folder::getParentId, folder.getParentId()).eq(Folder::getUsername, folder.getUsername()));
        if (count >0){
            throw new RuntimeException("存在重名文件");
        }
        folder.setId(getSnowflakeId());
        folder.setCreateTime(new Date());
        save(folder);
    }

    @Override
    public void editFolderName(Folder folder) {
        Folder byId = getById(folder.getId());
        if (byId == null){
            throw new RuntimeException("文件夹不存在");
        }
        long count = count(Wrappers.lambdaQuery(Folder.class).eq(Folder::getFolderName, folder.getFolderName())
                .eq(Folder::getParentId, byId.getParentId()).eq(Folder::getUsername, byId.getUsername()));
        if (count >0){
            throw new RuntimeException("存在重名文件");
        }
        byId.setFolderName(folder.getFolderName());
        byId.setEditTime(new Date());
        updateById(byId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteFolder(String id) {
        remove(Wrappers.<Folder>lambdaQuery().eq(Folder::getId, id).or().eq(Folder::getParentId, id));
        tempFileMapper.delete(Wrappers.<TempFile>lambdaQuery().eq(TempFile::getFolderId, id));
    }

    public static List<FolderTempFileTreeVO> getBaseResult(){
        return new ArrayList<>(){{
            add(new FolderTempFileTreeVO(){{
                setKey("Controller");
                setTitle("Controller");
                setId("Controller");
                setParentId(null);
                setName("Controller");
                setIsFile(false);
                setIsRoot(true);
                setType(1);
                setChildren(new ArrayList<>());
            }});
            add(new FolderTempFileTreeVO(){{
                setKey("Entity");
                setTitle("Entity");
                setId("Entity");
                setParentId(null);
                setName("Entity");
                setIsFile(false);
                setIsRoot(true);
                setType(2);
                setChildren(new ArrayList<>());
            }});
            add(new FolderTempFileTreeVO(){{
                setKey("Mapper");
                setTitle("Mapper");
                setId("Mapper");
                setParentId(null);
                setName("Mapper");
                setIsFile(false);
                setIsRoot(true);
                setType(3);
                setChildren(new ArrayList<>());
            }});
            add(new FolderTempFileTreeVO(){{
                setKey("Xml");
                setTitle("Xml");
                setId("Xml");
                setParentId(null);
                setName("Xml");
                setIsFile(false);
                setIsRoot(true);
                setType(4);
                setChildren(new ArrayList<>());
            }});
            add(new FolderTempFileTreeVO(){{
                setKey("Service");
                setTitle("Service");
                setId("Service");
                setParentId(null);
                setName("Service");
                setIsFile(false);
                setIsRoot(true);
                setType(5);
                setChildren(new ArrayList<>());
            }});
            add(new FolderTempFileTreeVO(){{
                setKey("ServiceImpl");
                setTitle("ServiceImpl");
                setId("ServiceImpl");
                setParentId(null);
                setName("ServiceImpl");
                setIsFile(false);
                setIsRoot(true);
                setType(6);
                setChildren(new ArrayList<>());
            }});
        }};
    }
}
