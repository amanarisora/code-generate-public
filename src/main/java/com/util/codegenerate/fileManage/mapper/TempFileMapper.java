package com.util.codegenerate.fileManage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.util.codegenerate.fileManage.entity.TempFile;
import com.util.codegenerate.fileManage.vo.FolderTempFileTreeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TempFileMapper extends BaseMapper<TempFile> {
    @Select("SELECT id AS key,file_name AS title,id,folder_id as parentId,file_name AS name," +
            "true AS isFile,false AS isRoot,file_type AS type,true AS isEmpty " +
            "FROM temp_file " +
            "WHERE folder_id = #{parentId} AND username = #{username}")
    List<FolderTempFileTreeVO> selectChildrenFile(@Param("parentId") String parentId, @Param("username") String username);
}
