package com.util.codegenerate.fileManage.mapper;

import com.util.codegenerate.fileManage.entity.TempGroup;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.util.codegenerate.fileManage.vo.TempGroupDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
*
*  Mapper
*
* @author author
* @since 2024-11-26
*/
@Mapper
public interface TempGroupMapper extends BaseMapper<TempGroup> {
    @Select("SELECT * FROM temp_group WHERE group_name = #{groupName} AND username = #{username}")
    TempGroupDetailVO selectDetail(@Param("groupName") String groupName,@Param("username") String username);
}
