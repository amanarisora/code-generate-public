package com.util.codegenerate.fileManage.service;

import com.util.codegenerate.common.exceptions.WrongTempGroupSettingException;
import com.util.codegenerate.fileManage.entity.TempGroup;
import com.baomidou.mybatisplus.extension.service.IService;
import com.util.codegenerate.fileManage.vo.TempGroupDetailVO;

import java.util.List;

/**
*
* 
*
* @author author
* @since 2024-11-26
*/
public interface ITempGroupService extends IService<TempGroup> {
    List<TempGroup> listTempGroup(String username);

    TempGroupDetailVO getTempGroupSetting(String groupName,String username) throws WrongTempGroupSettingException;

    void addNewTempGroup(TempGroup tempGroup);

    void updateTempGroup(TempGroup tempGroup);

    void renameTempGroup(TempGroup tempGroup);
}
