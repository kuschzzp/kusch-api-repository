package com.kusch.ex;

/**
 * API 异常
 *
 * @author Mr.kusch
 * @date 2023/5/24 14:37
 */
public final class ApiException extends RuntimeException {

    private static final long serialVersionUID = 123321L;

    public ApiException(Throwable e) {
        super(e.getMessage(), e);
    }

    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
