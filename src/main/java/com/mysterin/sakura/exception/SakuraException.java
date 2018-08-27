package com.mysterin.sakura.exception;

import com.mysterin.sakura.response.Code;

/**
 * @author linxb
 */
public class SakuraException extends Exception {

    private Code code;

    public SakuraException(Code code) {
        this.code = code;
    }

    public SakuraException(Code code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public SakuraException(Code code, String msg, Throwable cause) {
        super(msg, cause);
        this.code = code;
    }

    public Code getCode() {
        return code;
    }

    public void setCode(Code code) {
        this.code = code;
    }
}
