package com.mysterin.sakura.response;

public class Response {

    private int code;
    private String msg;

    public static Response success() {
        Response response = new Response();
        response.setCode(Code.SUCCESS.getCode());
        response.setMsg("");
        return response;
    }

    public static Response error(Code code, String msg) {
        Response response = new Response();
        response.setCode(code.getCode());
        response.setMsg(msg);
        return response;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
