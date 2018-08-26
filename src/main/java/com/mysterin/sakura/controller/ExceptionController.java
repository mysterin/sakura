package com.mysterin.sakura.controller;


import com.mysterin.sakura.exception.SakuraException;
import com.mysterin.sakura.response.Code;
import com.mysterin.sakura.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {

    private static Logger logger = LoggerFactory.getLogger(ExceptionController.class);

    @ExceptionHandler(value = Exception.class)
    public Response exceptionHandler(Exception e) {
        Code code;
        String msg = e.getMessage();
        if (e instanceof SakuraException) {
            code = ((SakuraException) e).getCode();
        } else {
            code = Code.UNKNOW_ERROR;
        }
        logger.error(e.getMessage(), e);
        return Response.error(code, msg);
    }
}
