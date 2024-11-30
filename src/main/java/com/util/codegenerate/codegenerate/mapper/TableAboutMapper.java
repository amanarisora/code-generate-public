package com.util.codegenerate.codegenerate.mapper;

import com.util.codegenerate.codegenerate.vo.TableColumnInfoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface TableAboutMapper {
    @Select("SELECT COLUMn_NAME,COLUMN_DEFAULT,IS_NULLABLE,DATA_TYPE,COLUMN_TYPE,COLUMN_KEY,COLUMN_COMMENT " +
            "FROM INFORMATION_SCHEMA.COLUMNS " +
            "WHERE TABLE_SCHEMA = #{databaseName} AND TABLE_NAME = #{tableName} ORDER BY ORDINAL_POSITION;")
    List<TableColumnInfoVO> getTableColumnInfo(@Param("databaseName") String databaseName, @Param("tableName") String tableName);

    @Select("SELECT count(*) FROM ${databaseName}.`${tableName}`")
    long countTableData(@Param("databaseName") String databaseName, @Param("tableName") String tableName);

    @Select("<script>" +
            "SELECT * FROM " +
            "${databaseName}.`${tableName}` " +
            "LIMIT #{pageSize} OFFSET #{offset}" +
            "</script>")
    List<Map<String, Object>> selectTableData(@Param("databaseName") String databaseName, @Param("tableName") String tableName,
                                              @Param("pageSize") Integer pageSize, @Param("offset") Integer offset);

//    List<Map<String, Object>>
}
