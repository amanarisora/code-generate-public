package com.util.codegenerate.utils;

import com.util.codegenerate.common.SnowflakeIdWorker;

public class CommonUtil {
    private static final SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker(1, 1);
    public static String getSnowflakeId() {
        return snowflakeIdWorker.nextId();
    }
}
