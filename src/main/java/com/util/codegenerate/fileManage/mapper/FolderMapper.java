package com.util.codegenerate.fileManage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.util.codegenerate.fileManage.entity.Folder;
import com.util.codegenerate.fileManage.vo.FolderTempFileTreeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FolderMapper extends BaseMapper<Folder> {

    @Select("SELECT id AS key,folder_name AS title,id,parent_id,folder_name AS name," +
            "false AS isFile,false AS isRoot,type," +
            "NOT EXISTS (SELECT 1 FROM temp_file tf " +
            "WHERE tf.folder_id = f.id) AND " +
            "NOT EXISTS (SELECT 1 " +
            "FROM folder sub_f " +
            "WHERE sub_f.parent_id = f.id) AS isEmpty " +
            "FROM folder f " +
            "WHERE f.username = #{username} " +
            "ORDER BY f.folder_name DESC")
    List<FolderTempFileTreeVO> selectAllFolder(@Param("username") String username);

    @Select("SELECT id AS key,folder_name AS title,id,parent_id,folder_name AS name," +
            "false AS isFile,false AS isRoot,type," +
            "NOT EXISTS (SELECT 1 FROM temp_file tf " +
            "WHERE tf.folder_id = f.id) AND " +
            "NOT EXISTS (SELECT 1 " +
            "FROM folder sub_f " +
            "WHERE sub_f.parent_id = f.id) AS isEmpty " +
            "FROM folder f " +
            "WHERE f.parent_id = #{parentId} AND f.username = #{username} " +
            "ORDER BY f.folder_name DESC")
    List<FolderTempFileTreeVO> selectChildrenFolder(@Param("parentId") String parentId,@Param("username") String username);

    @Select({
            "<script>",
            "WITH RECURSIVE subordinates AS (",
            "    SELECT id, parent_id, name",  // 假设 folder 表中有 name 字段
            "    FROM folder",
            "    WHERE id IN",
            "    <foreach item='id' collection='parentIds' open='(' separator=',' close=')'>",
            "        #{id}",
            "    </foreach>",
            "    UNION",  // 使用 UNION 而不是 UNION ALL
            "    SELECT n.id, n.parent_id, n.name",  // 确保选择所有需要的字段
            "    FROM folder n",
            "    INNER JOIN subordinates s ON n.parent_id = s.id",
            ")",
            "SELECT * FROM subordinates",
            "</script>"
    })
    List<Folder> getAllChildrenFolderAndSelf(@Param("parentIds") List<String> parentIds);
}
