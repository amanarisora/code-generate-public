package com.util.codegenerate.codegenerate.service.impl;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import com.baomidou.dynamic.datasource.creator.DefaultDataSourceCreator;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.builder.Entity;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.util.codegenerate.CustomFreeMarkerTemEngine;
import com.util.codegenerate.codegenerate.entity.DatasourceInfo;
import com.util.codegenerate.codegenerate.entity.Query;
import com.util.codegenerate.codegenerate.mapper.QueryMapper;
import com.util.codegenerate.codegenerate.vo.*;
import com.util.codegenerate.common.aspect.annotation.SelectDataSource;
import com.util.codegenerate.codegenerate.mapper.CommonMapper;
import com.util.codegenerate.codegenerate.mapper.DatasourceInfoMapper;
import com.util.codegenerate.codegenerate.service.ICodeGenerateService;
import com.util.codegenerate.common.exceptions.WrongTempGroupSettingException;
import com.util.codegenerate.fileManage.entity.TempGroup;
import com.util.codegenerate.fileManage.mapper.TempGroupMapper;
import com.util.codegenerate.fileManage.service.ITempGroupService;
import com.util.codegenerate.fileManage.vo.TempGroupDetailVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.util.codegenerate.codegenerate.DatasourceUtil.buildUrl;
import static com.util.codegenerate.common.constant.CommonConstant.*;
import static com.util.codegenerate.utils.CommonUtil.getSnowflakeId;

@Service
public class CodeGenerateServiceImpl implements ICodeGenerateService {

    @Autowired
    private CommonMapper commonMapper;
    @Autowired
    private DefaultDataSourceCreator dataSourceCreator;
    @Autowired
    private DataSource dataSource;

    @Autowired
    private DatasourceInfoMapper datasourceInfoMapper;

    @Autowired
    private QueryMapper queryMapper;
    @Autowired
    private ITempGroupService tempGroupService;

    @Autowired
    private CustomFreeMarkerTemEngine customFreeMarkerTemEngine;

    private final Logger logger = LoggerFactory.getLogger(CodeGenerateServiceImpl.class);
    private DynamicRoutingDataSource dynamicRoutingDataSource;
    private final Map<String, String> dataBaseNameMap = new HashMap<>();



    @PostConstruct
    public void init() {
        dynamicRoutingDataSource = (DynamicRoutingDataSource) dataSource;
    }

    @Override
    public void initDataSource(String username) {
        List<DatasourceInfo> allDataSource = getAllDataSource(username);
        for(DatasourceInfo info:allDataSource){
            DataSource dataSource = create(info);
            dynamicRoutingDataSource.addDataSource(getRealName(info), dataSource);
            dataBaseNameMap.put(info.getDatasourceName(), "");
        }
    }

    @DS("w51sa35Mz9AW")
    @Override
    public List<DatasourceInfo> getAllDataSource(String user) {
        List<DatasourceInfo> datasourceDesList = datasourceInfoMapper.selectList(Wrappers.lambdaQuery(DatasourceInfo.class).eq(DatasourceInfo::getUser, user));
        Map<String, DataSource> dataSources = dynamicRoutingDataSource.getDataSources();
        for (DatasourceInfo info: datasourceDesList){
            info.setKey(info.getId());
            info.setTitle(info.getDatasourceName());
            info.setType(DATASOURCE);
            if (dataSources.get(getRealName(info))==null){
                DataSource dataSource = create(info);
                dynamicRoutingDataSource.addDataSource(getRealName(info), dataSource);
                dataBaseNameMap.put(info.getDatasourceName(), "");
            }
        }
        return datasourceDesList;
    }

    @SelectDataSource
    @Override
    public List<DataBaseTreeVO> getAllDataBases(String user, String ds) {
        List<String> dataBases = commonMapper.getDataBases();
        List<DataBaseTreeVO> dataBaseTreeVOList = new ArrayList<>();
        for (String dataBase:dataBases){
            DataBaseTreeVO dataBaseTreeVO = new DataBaseTreeVO();
            dataBaseTreeVO.setKey(getSnowflakeId());
            dataBaseTreeVO.setTitle(dataBase);
            dataBaseTreeVO.setType(DATABASE);
            dataBaseTreeVO.setParentId(ds);
            dataBaseTreeVOList.add(dataBaseTreeVO);
        }
        return dataBaseTreeVOList;
    }

    @SelectDataSource
    @Override
    public Map<String, List<?>> getAllTableList(String databaseName, Integer datasourceType, String user, String ds) {
        Map<String, List<?>> result = new HashMap<>();
        List<Map<String, Object>> maps = new ArrayList<>();
        if (DATASOURCE_MYSQL == datasourceType){
            maps = commonMapper.listMysqlTableInfo(databaseName);
        }else if (DATASOURCE_SQLITE == datasourceType){
            maps = commonMapper.listSqliteTableInfo();
        }
        List<TableTreeVO> tableTreeVOList = new ArrayList<>();
        List<TableColumnVO> tableColumnVOList = new ArrayList<>();
        for (Map<String, Object> map:maps){
            TableTreeVO tableTreeVO = new TableTreeVO();
            tableTreeVO.setKey(Objects.toString(map.get("tableName"),""));
            tableTreeVO.setTitle(Objects.toString(map.get("tableName"),""));
            tableTreeVO.setType(TABLE);
            tableTreeVO.setParentId(databaseName);
            tableTreeVO.setDatasourceName(ds);
            tableTreeVO.setTableComment(Objects.toString(map.get("tableComment"),""));
            tableTreeVO.setTableCreatTime(Objects.toString(map.get("creatTime"),""));
            tableTreeVO.setChildren(new ArrayList<>());
            tableTreeVOList.add(tableTreeVO);

            TableColumnVO tableColumnVO = new TableColumnVO();
            tableColumnVO.setId(Objects.toString(map.get("tableName"),""));
            tableColumnVO.setTableName(Objects.toString(map.get("tableName"),""));
            tableColumnVO.setEditTime(Objects.toString(map.get("updateTime"),""));
            tableColumnVO.setDataLength(Objects.toString(map.get("dataLength"),""));
            tableColumnVO.setEngine(Objects.toString(map.get("engine"),""));
            tableColumnVO.setTableRows(Objects.toString(map.get("tableRows"),""));
            tableColumnVO.setTableComment(Objects.toString(map.get("tableComment"),""));
            tableColumnVO.setCreateTime(Objects.toString(map.get("creatTime"),""));
            tableColumnVOList.add(tableColumnVO);
        }

        Map<String, List<?>> queryResult = ((CodeGenerateServiceImpl) AopContext.currentProxy()).getAllQueryInDatabase(databaseName, user, ds);
        result.put("tableTree",tableTreeVOList);
        result.put("tableColumn",tableColumnVOList);
        result.putAll(queryResult);
        return result;
    }

    @DS("w51sa35Mz9AW")
    @Override
    public Map<String, List<?>> getAllQueryInDatabase(String databaseName, String user, String ds) {
        Map<String, List<?>> result = new HashMap<>();
        List<Query> queryList = queryMapper.selectList(Wrappers.lambdaQuery(Query.class).eq(Query::getDatabaseName, databaseName)
                .eq(Query::getDatasourceName, ds).eq(Query::getUser, user));
        List<QueryTreeVO> queryTreeVOList = new ArrayList<>();
        for (Query query:queryList){
            QueryTreeVO queryTreeVO = new QueryTreeVO();
            queryTreeVO.setKey(query.getId());
            queryTreeVO.setTitle(query.getQueryName());
            queryTreeVO.setType(QUERY);
            queryTreeVO.setParentId(databaseName);
            queryTreeVO.setDatasourceName(ds);
            queryTreeVO.setQueryText(query.getQueryText());
            queryTreeVOList.add(queryTreeVO);

        }
        result.put("queryTree",queryTreeVOList);
        result.put("queryColumn",queryList);
        return result;
    }

    @Override
    public void renameDataSource(DatasourceInfo info) {
        DatasourceInfo datasourceInfo = datasourceInfoMapper.selectById(info.getId());
        if (datasourceInfo == null){
            throw new RuntimeException("数据源不存在,请刷新！");
        }
        dynamicRoutingDataSource.removeDataSource(datasourceInfo.getUser()+"-"+datasourceInfo.getDatasourceName());
        dataBaseNameMap.remove(datasourceInfo.getDatasourceName());
        datasourceInfo.setDatasourceName(info.getDatasourceName());
        DataSource dataSource = create(datasourceInfo);
        dynamicRoutingDataSource.addDataSource(getRealName(datasourceInfo), dataSource);
        dataBaseNameMap.put(datasourceInfo.getDatasourceName(), "");
        datasourceInfo.setEditTime(new Date());
        datasourceInfoMapper.updateById(datasourceInfo);

    }

    @Override
    public Boolean testDataSourceConnection(DatasourceInfo vo) {
        return testConnection(vo);
    }

    public boolean testConnection(DatasourceInfo vo) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            // 加载数据库驱动
            Class.forName(DRIVER_CLASS_MYSQL);
            // 创建连接
            conn = DriverManager.getConnection(buildUrl(vo), vo.getUsername(), vo.getPassword());
            // 创建语句
            stmt = conn.createStatement();
            // 执行查询
            rs = stmt.executeQuery("SELECT 1");
            // 检查是否有返回结果
            return rs.next();
        } catch (ClassNotFoundException e) {
            logger.error("找不到驱动类",e);
            return false;
        } catch (SQLException e) {
            logger.error("数据库连接失败",e);
            return false;
        } finally {
            // 关闭资源
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                logger.error("关闭数据库连接时出错",e);
            }
        }
    }

    @DS("w51sa35Mz9AW")
    @Override
    public void addDataSource(DatasourceInfo vo) {
        Long count = datasourceInfoMapper.selectCount(Wrappers.lambdaQuery(DatasourceInfo.class)
                .eq(DatasourceInfo::getUser, vo.getUser()).eq(DatasourceInfo::getDatasourceName, vo.getDatasourceName()));
        if (count>0){
            throw new RuntimeException("连接名不能相同");
        }
        DataSource dataSource = create(vo);
        dynamicRoutingDataSource.addDataSource(getRealName(vo), dataSource);
        dataBaseNameMap.put(vo.getDatasourceName(), "");
        datasourceInfoMapper.insert(changeTo(vo));
    }

    @DS("w51sa35Mz9AW")
    @Override
    public void editDataSource(DatasourceInfo datasourceInfo) {
        DatasourceInfo byId = datasourceInfoMapper.selectById(datasourceInfo.getId());
        if (byId == null){
            throw new RuntimeException("数据源不存在");
        }
        DataSource dataSource = create(datasourceInfo);
        if (!datasourceInfo.getOldName().equals(datasourceInfo.getDatasourceName())){
            dynamicRoutingDataSource.removeDataSource(datasourceInfo.getUser()+"-"+datasourceInfo.getOldName());
            dataBaseNameMap.remove(datasourceInfo.getOldName());
        }
        dynamicRoutingDataSource.addDataSource(getRealName(datasourceInfo), dataSource);
        dataBaseNameMap.put(datasourceInfo.getDatasourceName(), "");
        datasourceInfo.setCreateTime(byId.getCreateTime());
        datasourceInfo.setEditTime(new Date());
        datasourceInfoMapper.updateById(datasourceInfo);

    }

    @DS("w51sa35Mz9AW")
    @Override
    public void deleteDataSource(String name, String user) {
        dynamicRoutingDataSource.removeDataSource(name);
        dataBaseNameMap.remove(name);
        datasourceInfoMapper.delete(Wrappers.lambdaQuery(DatasourceInfo.class).eq(DatasourceInfo::getUser,user)
                .eq(DatasourceInfo::getDatasourceName,name));
    }

    private DataSource create(DatasourceInfo datasourceInfo){
        DataSourceProperty dataSourceProperty = new DataSourceProperty();
        switch (datasourceInfo.getDatasourceType()){
            case DATASOURCE_MYSQL:
                String url = buildUrl(datasourceInfo);
                dataSourceProperty.setUrl(url);
                dataSourceProperty.setDriverClassName(DRIVER_CLASS_MYSQL);
                break;
            case DATASOURCE_SQLITE:
                String sqliteUrl = buildUrl(datasourceInfo);
                dataSourceProperty.setUrl(sqliteUrl);
                dataSourceProperty.setDriverClassName(DRIVER_CLASS_SQLITE);
                break;
        }

        dataSourceProperty.setUsername(datasourceInfo.getUsername());
        dataSourceProperty.setPassword(datasourceInfo.getPassword());
        return dataSourceCreator.createDataSource(dataSourceProperty);
    }

    private DatasourceInfo changeTo(DatasourceInfo datasourceInfo){
        datasourceInfo.setId(getSnowflakeId());
        datasourceInfo.setCreateTime(new Date());
        return datasourceInfo;
    }

    private String getRealName(DatasourceInfo info){
        return info.getUser() + "-" + info.getDatasourceName();
    }

    @Override
    public String getCurrentDatabase(String ds) {
        String dataBaseName = dataBaseNameMap.get(ds);
        if (StringUtils.isNotBlank(dataBaseName)) {
            return dataBaseName;
        }
        DataSource dataSource = dynamicRoutingDataSource.getDataSource(ds);
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DATABASE()")) {
            if (rs.next()) {
                dataBaseNameMap.put(ds, rs.getString(1));
                return rs.getString(1);  // 获取当前数据库名
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Value("${generate.path}")
    private String outPutPath;

    @Override
    public void generateCode(GenerateVO vo, HttpServletResponse response) {
        TempGroupDetailVO currentTempGroupSetting;
        try {
            currentTempGroupSetting = tempGroupService.getTempGroupSetting(CURRENT_TEMP_GROUP_SETTING,vo.getUser());
        } catch (WrongTempGroupSettingException e) {
            throw new RuntimeException("模板配置异常，请查看");
        }

        String snowId = getSnowflakeId();
        String realOutPutPath = outPutPath+ File.separator+snowId;
        DatasourceInfo datasourceInfo = datasourceInfoMapper.selectOne(Wrappers.lambdaQuery(DatasourceInfo.class)
                .eq(DatasourceInfo::getUser, vo.getUser()).eq(DatasourceInfo::getDatasourceName, vo.getDatasourceName()));
        //数据源配置
        DataSourceConfig dataSourceConfig = new DataSourceConfig.
                Builder(buildUrl(datasourceInfo,vo.getDatabaseName()), datasourceInfo.getUsername(), datasourceInfo.getPassword()).build();
        //全局配置
        GlobalConfig.Builder builder = new GlobalConfig.Builder()
                .disableOpenDir()
                .outputDir(realOutPutPath) // 设置输出目录
                .author(vo.getAuthor()) // 设置作者名
                .dateType(DateType.ONLY_DATE) // 设置时间类型策略
                .commentDate("yyyy-MM-dd");
        if (vo.getNeedSwagger()){
            builder.enableSwagger();
        }
        if (vo.getNeedSpringDoc()){
            builder.enableSpringdoc();
        }
        GlobalConfig globalConfig = builder.build();
        //包配置
        PackageConfig packageConfig = new PackageConfig.Builder()
                .parent(vo.getPackageName()) // 设置父包名
                .moduleName(vo.getModel()) // 设置父包模块名
                .entity(vo.getEntityFilePathName()) // 设置 Entity 包名
                .service(vo.getServiceFilePathName()) // 设置 Service 包名
                .serviceImpl(vo.getServiceImplFilePathName()) // 设置 Service Impl 包名
                .mapper(vo.getMapperFilePathName()) // 设置 Mapper 包名
                .xml(vo.getMapperFilePathName()+".xml") // 设置 Mapper XML 包名
                .controller(vo.getControllerFilePathName()) // 设置 Controller 包名
//                .pathInfo(Collections.singletonMap(OutputFile.mapperXml, "/path/to/xml")) // 设置路径配置信息
                .build();
        //模版配置等
        StrategyConfig.Builder strategyBuilder = new StrategyConfig.Builder();
        strategyBuilder.addInclude(vo.getTableNameList());
        Entity.Builder entityBuilder = strategyBuilder.entityBuilder();
        if (StringUtils.isNotBlank(vo.getTablePrefix())){
            strategyBuilder.addTablePrefix(vo.getTablePrefix());
        }

        if (vo.getNeedEntity()){
            entityBuilder.enableFileOverride().javaTemplate(currentTempGroupSetting.getEntityTempId());
            if (vo.getNeedLombok()){
                entityBuilder.enableLombok();
            }
            if (vo.getNeedChainModel()){
                entityBuilder.enableChainModel();
            }
        }else {
            entityBuilder.disable();
        }


        if (vo.getNeedService()){
            strategyBuilder.serviceBuilder().enableFileOverride().serviceTemplate(currentTempGroupSetting.getServiceTempId());
        }else {
            strategyBuilder.serviceBuilder().disableService();
        }


        if (vo.getNeedServiceImpl()){
            strategyBuilder.serviceBuilder().serviceImplTemplate(currentTempGroupSetting.getServiceImplTempId());
        }else {
            strategyBuilder.serviceBuilder().disableServiceImpl();
        }


        if (vo.getNeedController()){
            if (vo.getNeedRestController()){
                strategyBuilder.controllerBuilder().enableFileOverride().enableRestStyle();
            }
            strategyBuilder.controllerBuilder().template(currentTempGroupSetting.getControllerTempId());
        }else {
            strategyBuilder.controllerBuilder().disable();
        }

        if (vo.getNeedMapper()){
            strategyBuilder.mapperBuilder().enableFileOverride().mapperTemplate(currentTempGroupSetting.getMapperTempId());
            strategyBuilder.mapperBuilder().mapperXmlTemplate(currentTempGroupSetting.getXmlTempId());
            if (vo.getNeedMapperAnno()){
                strategyBuilder.mapperBuilder().enableMapperAnnotation();
            }
        }else {
            strategyBuilder.mapperBuilder().disableMapper();
            strategyBuilder.mapperBuilder().disableMapperXml();
        }
        StrategyConfig strategyConfig = strategyBuilder.build();
        AutoGenerator autoGenerator = new AutoGenerator(dataSourceConfig)
                .strategy(strategyConfig)
                .global(globalConfig).packageInfo(packageConfig);
        autoGenerator.execute(customFreeMarkerTemEngine);
        /*String zipFilePath = realOutPutPath+".zip";
        try {
            File fileToZip = new File(realOutPutPath);
            // 创建ZIP文件
            try (FileOutputStream fos = new FileOutputStream(zipFilePath);
                 ZipOutputStream zipOut = new ZipOutputStream(fos)) {
                zipFile(fileToZip, fileToZip.getName(), zipOut, true);
            } // 关闭ZipOutputStream后，所有数据才确保被写入文件

            // 删除原始文件夹
            deleteDirectory(fileToZip);

            // 设置响应头
            response.addHeader("Content-Disposition", "attachment; filename=\"" + fileToZip.getName() + ".zip\"");
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");

            // 将生成的ZIP文件发送给客户端
            try (InputStream inputStream = new FileInputStream(zipFilePath);
                 OutputStream outputStream = response.getOutputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
//        new File(zipFilePath).delete();

    }
    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut,boolean isRoot) throws IOException {
        if (fileToZip.isDirectory()) {
            File[] children = fileToZip.listFiles();
            if (children != null) {
                for (File childFile : children) {
                    // 如果是根目录，不添加根目录名到路径中
                    String childFileName = isRoot ? childFile.getName() : fileName + "/" + childFile.getName();
                    zipFile(childFile, childFileName, zipOut, false);
                }
            }
            return;
        }
        try(FileInputStream fis = new FileInputStream(fileToZip);){
            ZipEntry zipEntry = new ZipEntry(fileName);
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
        }

    }
    public static void deleteDirectory(File directory) throws IOException {
        if (directory.isDirectory()) {
            File[] entries = directory.listFiles();
            if (entries != null) {
                for (File entry : entries) {
                    deleteDirectory(entry);  // 递归删除子目录或文件
                }
            }
        }
        if (!directory.delete()) {
            throw new IOException("删除失败 :" + directory);
        }
    }


}
