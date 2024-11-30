package com.util.codegenerate.common.enums;

public enum TempTypeEnum {

    CONTROLLER(1),
    ENTITY(2),
    MAPPER(3),
    XML(4),
    SERVICE(5),
    SERVICEIMPL(6);
    private final Integer value;
    TempTypeEnum(Integer code) {
        value = code;
    }
    public Integer getValue() {
        return value;
    }
}
