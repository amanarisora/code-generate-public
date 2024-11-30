package com.util.codegenerate.codegenerate.service;

import com.util.codegenerate.codegenerate.entity.DatasourceInfo;
import com.util.codegenerate.codegenerate.vo.DataBaseTreeVO;
import com.util.codegenerate.codegenerate.vo.GenerateVO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface ICodeGenerateService {

    void initDataSource(String username);

    List<DatasourceInfo> getAllDataSource(String user);
    Map<String,List<?>> getAllTableList(String databaseName,Integer datasourceType, String user, String ds);

    Map<String,List<?>> getAllQueryInDatabase(String databaseName, String user, String ds);
    /**
     * 获取所有数据库
     */
    List<DataBaseTreeVO> getAllDataBases(String user, String ds);

    void renameDataSource(DatasourceInfo info);


    /**测试数据源连接*/
    Boolean testDataSourceConnection(DatasourceInfo vo);
    void addDataSource(DatasourceInfo vo);

    void editDataSource(DatasourceInfo vo);

    void deleteDataSource(String name,String user);

    String getCurrentDatabase(String ds);



    void generateCode(GenerateVO vo, HttpServletResponse response);
}
