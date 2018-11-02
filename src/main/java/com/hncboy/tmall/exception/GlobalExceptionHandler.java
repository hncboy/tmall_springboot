package com.hncboy.tmall.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by IntelliJ IDEA.
 * User: hncboy
 * Date: 2018/11/2
 * Time: 7:46
 *
 * 异常处理
 */
@RestController
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public String defaultErrorHandler(HttpServletRequest request, Exception e) throws Exception {
        e.printStackTrace();
        Class constrainViolationException = Class.forName("org.hibernate.exception.ConstraintViolationException");
        if (null != e.getCause() && constrainViolationException == e.getCause().getClass()) {
            return "应该是违反了外键约束吧";
        }
        return e.getMessage();
    }
}
