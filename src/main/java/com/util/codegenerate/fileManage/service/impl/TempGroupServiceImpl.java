package com.util.codegenerate.fileManage.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.util.codegenerate.common.exceptions.WrongTempGroupSettingException;
import com.util.codegenerate.fileManage.entity.TempFile;
import com.util.codegenerate.fileManage.entity.TempGroup;
import com.util.codegenerate.fileManage.mapper.TempFileMapper;
import com.util.codegenerate.fileManage.mapper.TempGroupMapper;
    import com.util.codegenerate.fileManage.service.ITempGroupService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.util.codegenerate.fileManage.vo.TempGroupDetailVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.util.codegenerate.common.constant.CommonConstant.CURRENT_TEMP_GROUP_SETTING;
import static com.util.codegenerate.utils.CommonUtil.getSnowflakeId;

/**
*
*  服务实现类
*
* @author author
* @since 2024-11-26
*/
@DS("w51sa35Mz9AW")
@Service
public class TempGroupServiceImpl extends ServiceImpl<TempGroupMapper, TempGroup> implements ITempGroupService {
    @Autowired
    private TempGroupMapper tempGroupMapper;
    @Autowired
    private TempFileMapper tempFileMapper;
    @Override
    public List<TempGroup> listTempGroup(String username) {
        List<TempGroup> list = list(Wrappers.lambdaQuery(TempGroup.class).eq(TempGroup::getUsername, username));
        list.sort((o1, o2) -> {
            boolean isBlank1 = StringUtils.isBlank(o1.getGroupName());
            boolean isBlank2 = StringUtils.isBlank(o2.getGroupName());
            return Boolean.compare(isBlank2, isBlank1);
        });
        return list;
    }

    @Override
    public TempGroupDetailVO getTempGroupSetting(String groupName, String username) throws WrongTempGroupSettingException {
        if (groupName.equals(CURRENT_TEMP_GROUP_SETTING)){
            LambdaQueryWrapper<TempGroup> wrapper = Wrappers.lambdaQuery(TempGroup.class)
                    .eq(TempGroup::getGroupName,CURRENT_TEMP_GROUP_SETTING)
                    .eq(TempGroup::getUsername, username);
            long count = count(wrapper);
            if (count !=1){
                remove(wrapper);
                TempGroup tempGroup = new TempGroup();
                tempGroup.setId(getSnowflakeId());
                tempGroup.setUsername(username);
                save(tempGroup);
                throw new WrongTempGroupSettingException("模板配置错误");
            }
        }
        TempGroupDetailVO detailVO = tempGroupMapper.selectDetail(groupName,username);
        if (detailVO ==null){
            throw new WrongTempGroupSettingException("模板组不存在");
        }

        List<String> ids = new ArrayList<>();
        if (StringUtils.isNotBlank(detailVO.getControllerTempId())) {
            ids.add(detailVO.getControllerTempId());
        }
        if (StringUtils.isNotBlank(detailVO.getEntityTempId())) {
            ids.add(detailVO.getEntityTempId());
        }
        if (StringUtils.isNotBlank(detailVO.getMapperTempId())) {
            ids.add(detailVO.getMapperTempId());
        }
        if (StringUtils.isNotBlank(detailVO.getXmlTempId())) {
            ids.add(detailVO.getXmlTempId());
        }
        if (StringUtils.isNotBlank(detailVO.getServiceTempId())) {
            ids.add(detailVO.getServiceTempId());
        }
        if (StringUtils.isNotBlank(detailVO.getServiceImplTempId())) {
            ids.add(detailVO.getServiceImplTempId());
        }
        //检查是否有失效模板
        boolean isHaveLostTemp = false;
        if (!ids.isEmpty()){
            List<TempFile> tempFileList = tempFileMapper.selectList(Wrappers.lambdaQuery(TempFile.class)
                    .select(TempFile::getId, TempFile::getFileName).in(TempFile::getId, ids));
            Map<String, String> idNameMap = tempFileList.stream().collect(Collectors.toMap(TempFile::getId, TempFile::getFileName));
            if (StringUtils.isBlank(idNameMap.get(detailVO.getControllerTempId()))){
                detailVO.setControllerTempId(null);
                isHaveLostTemp = true;
            }
            if (StringUtils.isBlank(idNameMap.get(detailVO.getEntityTempId()))) {
                detailVO.setEntityTempId(null);
                isHaveLostTemp = true;
            }
            if (StringUtils.isBlank(idNameMap.get(detailVO.getMapperTempId()))) {
                detailVO.setMapperTempId(null);
                isHaveLostTemp = true;
            }

            if (StringUtils.isBlank(idNameMap.get(detailVO.getXmlTempId()))) {
                detailVO.setXmlTempId(null);
                isHaveLostTemp = true;
            }
            if (StringUtils.isBlank(idNameMap.get(detailVO.getServiceTempId()))) {
                detailVO.setServiceTempId(null);
                isHaveLostTemp = true;
            }
            if (StringUtils.isBlank(idNameMap.get(detailVO.getServiceImplTempId()))) {
                detailVO.setServiceImplTempId(null);
                isHaveLostTemp = true;
            }
            detailVO.setControllerTempName(idNameMap.get(detailVO.getControllerTempId()));
            detailVO.setEntityTempName(idNameMap.get(detailVO.getEntityTempId()));
            detailVO.setMapperTempName(idNameMap.get(detailVO.getMapperTempId()));
            detailVO.setXmlTempName(idNameMap.get(detailVO.getXmlTempId()));
            detailVO.setServiceTempName(idNameMap.get(detailVO.getServiceTempId()));
            detailVO.setServiceImplTempName(idNameMap.get(detailVO.getServiceImplTempId()));
        }
        if (isHaveLostTemp){
            TempGroup tempGroup = getById(detailVO.getId());
            tempGroup.setControllerTempId(detailVO.getControllerTempId());
            tempGroup.setEntityTempId(detailVO.getEntityTempId());
            tempGroup.setMapperTempId(detailVO.getMapperTempId());
            tempGroup.setXmlTempId(detailVO.getXmlTempId());
            tempGroup.setServiceTempId(detailVO.getServiceTempId());
            tempGroup.setServiceImplTempId(detailVO.getServiceImplTempId());
            tempGroup.setEditTime(new Date());
            updateById(tempGroup);
        }

        return detailVO;
    }

    @Override
    public void addNewTempGroup(TempGroup tempGroup) {
        long count = count(Wrappers.lambdaQuery(TempGroup.class).eq(TempGroup::getGroupName, tempGroup.getGroupName())
                .eq(TempGroup::getUsername, tempGroup.getUsername()));
        if (count >0){
            throw new RuntimeException("模板组名重复");
        }
        tempGroup.setId(getSnowflakeId());
        save(tempGroup);
    }

    @Override
    public void updateTempGroup(TempGroup tempGroup) {
        TempGroup oldOne = getById(tempGroup.getId());
        if (oldOne == null){
            throw new RuntimeException("模板组不存在");
        }
        tempGroup.setId(oldOne.getId());
        tempGroup.setGroupName(oldOne.getGroupName());
        tempGroup.setCreateTime(oldOne.getCreateTime());
        tempGroup.setEditTime(new Date());
        tempGroup.setUsername(oldOne.getUsername());
        updateById(tempGroup);

    }

    @Override
    public void renameTempGroup(TempGroup tempGroup) {
        TempGroup oldOne = getById(tempGroup.getId());
        if (oldOne == null){
            throw new RuntimeException("模板组不存在");
        }
        oldOne.setGroupName(tempGroup.getGroupName());
        oldOne.setEditTime(new Date());
        updateById(oldOne);
    }
}
