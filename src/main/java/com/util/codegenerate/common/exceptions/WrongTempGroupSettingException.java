package com.util.codegenerate.common.exceptions;

public class WrongTempGroupSettingException extends Exception{
    public WrongTempGroupSettingException() {
    }

    public WrongTempGroupSettingException(String message) {
        super(message);
    }

    public WrongTempGroupSettingException(String message, Throwable cause) {
        super(message, cause);
    }
}
