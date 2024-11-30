package com.util.codegenerate.common.constant;

import java.util.HashMap;
import java.util.Map;

public interface CommonConstant {
	/** {@code 500 Server Error} (HTTP/1.0 - RFC 1945) */
    Integer SC_INTERNAL_SERVER_ERROR_500 = 500;
    /** {@code 200 OK} (HTTP/1.0 - RFC 1945) */
    Integer SC_OK_200 = 200;

    int DATASOURCE = 0;
    int DATABASE = 1;
    int TABLE = 2;
    int QUERY = 5;

    int GENERATE_CODE = -1;
    int DATASOURCE_MYSQL = 0;
    int DATASOURCE_SQLITE = 1;

    String DRIVER_CLASS_MYSQL = "com.mysql.cj.jdbc.Driver";
    String DRIVER_CLASS_SQLITE = "org.sqlite.JDBC";

    String URL_PRE_MYSQL = "jdbc:mysql://";
    String URL_PRE_SQLITE = "jdbc:sqlite:";

    String URL_POST_MYSQL = "?characterEncoding=UTF-8&useUnicode=true&useSSL=false&tinyInt1isBit=false&allowPublicKeyRetrieval=true" +
            "&serverTimezone=Asia/Shanghai&useAffectedRows=true";

    Map<String, String> TYPE_PARENT_ID_MAP = new HashMap<>() {{
        put("1", "Controller");
        put("2", "Entity");
        put("3", "Mapper");
        put("4", "Xml");
        put("5", "Service");
        put("6", "ServiceImpl");
    }};

    String CURRENT_TEMP_GROUP_SETTING = "当前设置";

}
