package com.util.codegenerate.codegenerate.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.util.codegenerate.codegenerate.entity.DatasourceInfo;
import com.util.codegenerate.codegenerate.entity.Query;
import com.util.codegenerate.codegenerate.mapper.DatasourceInfoMapper;
import com.util.codegenerate.codegenerate.mapper.QueryMapper;
import com.util.codegenerate.codegenerate.mapper.TableAboutMapper;
import com.util.codegenerate.codegenerate.service.ITableAboutService;
import com.util.codegenerate.codegenerate.vo.SaveQueryVO;
import com.util.codegenerate.codegenerate.vo.SqlRunResultVO;
import com.util.codegenerate.codegenerate.vo.TableColumnInfoVO;
import com.util.codegenerate.codegenerate.vo.TableColumnVueVO;
import com.util.codegenerate.common.aspect.annotation.SelectDataSource;
import org.apache.ibatis.jdbc.SqlRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.Date;

import static com.util.codegenerate.common.constant.CommonConstant.DATASOURCE_MYSQL;
import static com.util.codegenerate.utils.CommonUtil.getSnowflakeId;

@Service
public class TableAboutService implements ITableAboutService {
    @Autowired
    private TableAboutMapper tableAboutMapper;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private QueryMapper queryMapper;
    @Autowired
    private DatasourceInfoMapper datasourceInfoMapper;

    private final Logger logger = LoggerFactory.getLogger(TableAboutService.class);

    @SelectDataSource
    @Override
    public List<TableColumnVueVO> getTableColumnVueList(String databaseName, String tableName, String user, String ds) {
        List<TableColumnInfoVO> tableColumnInfo = tableAboutMapper.getTableColumnInfo(databaseName, tableName);
        List<TableColumnVueVO> tableColumnVueVOList = initColumnVOList();
        for (TableColumnInfoVO tableColumnInfoVO : tableColumnInfo) {
            TableColumnVueVO tableColumnVueVO = new TableColumnVueVO();
            tableColumnVueVO.setDataIndex(tableColumnInfoVO.getColumnName());
            tableColumnVueVO.setKey(tableColumnInfoVO.getColumnName());
            tableColumnVueVO.setTitle(tableColumnInfoVO.getColumnName());
            tableColumnVueVO.setWidth(getWidth(tableColumnInfoVO));
            tableColumnVueVO.setResizable(true);
            tableColumnVueVO.setEllipsis(true);
            tableColumnVueVOList.add(tableColumnVueVO);
        }
        tableColumnVueVOList.get(tableColumnVueVOList.size()-1).setWidth(null);
        tableColumnVueVOList.get(tableColumnVueVOList.size()-1).setResizable(false);
        return tableColumnVueVOList;
    }

    public Integer getWidth(TableColumnInfoVO vo) {
        if ("datetime".equals(vo.getDataType()) || "timestamp".equals(vo.getDataType())) {
            return 180;
        }
        if ("date".equals(vo.getDataType()) || "time".equals(vo.getDataType())) {
            return 100;
        }

        return 80;
    }

    @SelectDataSource
    @Override
    public Page<Map<String, Object>> queryTableDateByPage(String databaseName, String tableName,
                                                          Integer pageNo, Integer pageSize, String user, String ds) {
        long count = tableAboutMapper.countTableData(databaseName, tableName);
        if (count == 0) {
            return new Page<>(pageNo, pageSize);
        }
        int offset = (pageNo - 1) * pageSize;
        if (offset > count) {
            return new Page<>(pageNo, pageSize,count);
        }
        List<Map<String, Object>> records = tableAboutMapper.selectTableData(databaseName, tableName, pageSize, offset);
        return new Page<Map<String, Object>>(pageNo, pageSize,count).setRecords(records);
    }

    @DS("w51sa35Mz9AW")
    @Override
    public void saveQuery(SaveQueryVO vo) {
        Query query = queryMapper.selectOne(Wrappers.lambdaQuery(Query.class).eq(Query::getDatabaseName, vo.getDatabaseName())
                .eq(Query::getQueryName, vo.getQueryName()));
        if (vo.getIsNewQuery()){
            if (query!=null){
                throw new RuntimeException("查询名称重复，查询在该库下已存在");
            }
            query = new Query();
            query.setId(getSnowflakeId());
            query.setQueryName(vo.getQueryName());
            query.setQueryText(vo.getQueryText());
            query.setDatabaseName(vo.getDatabaseName());
            query.setDatasourceName(vo.getDatasourceName());
            query.setUser(vo.getUser());
            query.setCreateTime(new Date());
        }else {
            query.setQueryText(vo.getQueryText());
            query.setEditTime(new Date());
        }
        queryMapper.insertOrUpdate(query);
    }

    @SelectDataSource
    @DSTransactional
    @Override
    public List<SqlRunResultVO> runQuerySql(String databaseName, String sql, String user, String ds) {
        List<SqlRunResultVO> results = new ArrayList<>();
        DatasourceInfo datasourceInfo = datasourceInfoMapper.selectOne(Wrappers.lambdaQuery(DatasourceInfo.class)
                .eq(DatasourceInfo::getUser, user).eq(DatasourceInfo::getDatasourceName, ds));
        try (Connection connection = dataSource.getConnection()) {
            // 切换数据库
            if (DATASOURCE_MYSQL == datasourceInfo.getDatasourceType()){
                try (Statement useDbStatement = connection.createStatement()) {
                    useDbStatement.execute("USE " + databaseName);
                }
            }


            List<String> sqlList = processSql(sql);
            if (sqlList.isEmpty()){
                return new ArrayList<>();
            }

            for (String sqlStr : sqlList) {
                // 执行查询
                try (Statement statement = connection.createStatement()) {
                    boolean isResultSet = statement.execute(sqlStr);

                    SqlRunResultVO sqlRunResultVO = new SqlRunResultVO();
                    sqlRunResultVO.setSql(sqlStr);

                    if (isResultSet) {
                        // 处理 SELECT 语句
                        try (ResultSet resultSet = statement.getResultSet()) {
                            ResultSetMetaData metaData = resultSet.getMetaData();
                            List<TableColumnVueVO> tableColumnVueVOList = new ArrayList<>();

                            // 获取列信息
                            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                                TableColumnVueVO tableColumnVueVO = new TableColumnVueVO();
                                String columnName = metaData.getColumnName(i);
                                tableColumnVueVO.setDataIndex(columnName);
                                tableColumnVueVO.setKey(columnName);
                                tableColumnVueVO.setTitle(columnName);
                                tableColumnVueVO.setWidth(180);
                                tableColumnVueVO.setResizable(true);
                                tableColumnVueVO.setEllipsis(true);
                                tableColumnVueVOList.add(tableColumnVueVO);
                            }

                            // 构建结果集
                            List<Map<String, Object>> maps = new ArrayList<>();
                            while (resultSet.next()) {
                                Map<String, Object> row = new LinkedHashMap<>();
                                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                                    row.put(metaData.getColumnName(i), resultSet.getObject(i));
                                }
                                maps.add(row);
                            }

                            // 设置最后一列的宽度和可调整性
                            if (!tableColumnVueVOList.isEmpty()) {
                                tableColumnVueVOList.get(tableColumnVueVOList.size() - 1).setWidth(null);
                                tableColumnVueVOList.get(tableColumnVueVOList.size() - 1).setResizable(false);
                            }

                            sqlRunResultVO.setSqlType("select");
                            sqlRunResultVO.setSelectResultList(maps);
                            sqlRunResultVO.setColumnVueList(tableColumnVueVOList);
                        }
                    } else {
                        // 处理非 SELECT 语句
                        int updateCount = statement.getUpdateCount();
                        sqlRunResultVO.setSqlType("update");
                        sqlRunResultVO.setAffectedRowNumber(updateCount);
                    }
                    results.add(sqlRunResultVO);

                } catch (SQLException e) {
                    logger.error("运行sql失败,sql: {}", sql, e);
                    throw new RuntimeException("运行sql失败,sql: " + sql);
                }

            }

        } catch (SQLException e) {
            logger.error("获取数据库连接失败", e);
            throw new RuntimeException("获取数据库连接失败");
        }
        return results;
    }

    public static List<String> processSql(String sql) {
        // 去掉注释
        String noComments = sql.replaceAll("(?s)/\\*.*?\\*/", "") // 去掉多行注释
                .replaceAll("--.*?(\r?\n|$)", ""); // 去掉单行注释

        // 按分号拆分
        String[] statements = noComments.split("(?<=;)(?=\\s*\\S)");

        // 去掉每个语句的前后空白并过滤掉空语句
        List<String> sqlList = new ArrayList<>();
        for (String statement : statements) {
            String trimmed = statement.trim();
            if (!trimmed.isEmpty()) {
                sqlList.add(trimmed);
            }
        }

        return sqlList;
    }

    public List<TableColumnVueVO> initColumnVOList(){
        return new ArrayList<>(){{
            TableColumnVueVO tableColumnVueVO = new TableColumnVueVO();
            tableColumnVueVO.setKey("cs121sn801n");
            tableColumnVueVO.setTitle("序号");
            tableColumnVueVO.setResizable(true);
            tableColumnVueVO.setWidth(60);
            tableColumnVueVO.setMaxWidth(60);
            add(tableColumnVueVO);
        }};
    }

    @SelectDataSource
    @Override
    public String dumpTableStructureSQL(String databaseName, String tableName, String user, String ds) {

        return "";
    }
}
