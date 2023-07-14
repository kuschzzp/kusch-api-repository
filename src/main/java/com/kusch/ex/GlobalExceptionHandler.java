package com.kusch.ex;

import com.kusch.result.R;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 处理全局异常
 *
 * @author Mr.kusch
 * @date 2023/5/24 14:41
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public R<Object> handlerException(Exception e) {
        e.printStackTrace();
        return R.fail("未知错误，请查看日志！");
    }

    @ExceptionHandler
    public R<Object> handlerException(ApiException e) {
        e.printStackTrace();
        return R.fail(e.getMessage());
    }

}