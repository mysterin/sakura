package com.mysterin.sakura.response;

public enum Code {
    SUCCESS(0),
    UNKNOW_ERROR(-1),
    UNCONNECTION_ERROR(400),
    UNSUPPORT_TYPE(401);

    private int code;

    private Code(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}
