package com.util.codegenerate.codegenerate.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.util.codegenerate.codegenerate.vo.SaveQueryVO;
import com.util.codegenerate.codegenerate.vo.SqlRunResultVO;
import com.util.codegenerate.codegenerate.vo.TableColumnVueVO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface ITableAboutService {
    List<TableColumnVueVO> getTableColumnVueList(String databaseName, String tableName, String user, String ds);
    Page<Map<String, Object>> queryTableDateByPage(String databaseName, String tableName,
                                                        Integer pageNo, Integer pageSize,
                                                        String user, String ds);

    void saveQuery(SaveQueryVO vo);

    List<SqlRunResultVO> runQuerySql(String databaseName, String sql,
                                     String user, String ds);

    /**转储表结构sql*/
    String dumpTableStructureSQL(String databaseName, String tableName, String user, String ds);
}
