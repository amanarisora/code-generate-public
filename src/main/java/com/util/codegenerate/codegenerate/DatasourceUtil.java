package com.util.codegenerate.codegenerate;

import com.util.codegenerate.codegenerate.entity.DatasourceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.util.codegenerate.common.constant.CommonConstant.*;

public class DatasourceUtil {
    private final static Logger logger = LoggerFactory.getLogger(DatasourceUtil.class);
    public static String buildUrl(DatasourceInfo datasourceInfo){
        switch (datasourceInfo.getDatasourceType()){
            case DATASOURCE_MYSQL:
                return URL_PRE_MYSQL + datasourceInfo.getIp() + ":" + datasourceInfo.getPort() + URL_POST_MYSQL;
            case DATASOURCE_SQLITE:
                return URL_PRE_SQLITE + datasourceInfo.getSqliteFilePath();
            default:
                logger.error("不支持的数据库类型");
                throw new RuntimeException();
        }
    }
    public static String buildUrl(DatasourceInfo datasourceInfo,String databaseName){
        switch (datasourceInfo.getDatasourceType()){
            case DATASOURCE_MYSQL:
                return URL_PRE_MYSQL + datasourceInfo.getIp() + ":" + datasourceInfo.getPort() + "/" + databaseName + URL_POST_MYSQL;
            case DATASOURCE_SQLITE:
                return URL_PRE_SQLITE + datasourceInfo.getSqliteFilePath();
            default:
                logger.error("不支持的数据库类型");
                throw new RuntimeException();
        }

    }

}
