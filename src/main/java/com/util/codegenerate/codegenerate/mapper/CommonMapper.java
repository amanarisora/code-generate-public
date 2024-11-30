package com.util.codegenerate.codegenerate.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommonMapper{
    @Select("SELECT TABLE_NAME as tableName,TABLE_COMMENT as tableComment,CREATE_TIME as creatTime, " +
            "ENGINE as engine,DATA_LENGTH as dataLength,TABLE_ROWS as tableRows,UPDATE_TIME as updateTime " +
            "FROM information_schema.tables " +
            "WHERE table_schema = #{databaseName}")
    List<Map<String,Object>> listMysqlTableInfo(@Param("databaseName") String databaseName);

    @Select("SELECT name as tableName FROM sqlite_master WHERE type='table'")
    List<Map<String,Object>> listSqliteTableInfo();

    @Select("SHOW DATABASES")
    List<String> getDataBases();

}
