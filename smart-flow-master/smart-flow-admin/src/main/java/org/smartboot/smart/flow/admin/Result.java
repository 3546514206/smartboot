package org.smartboot.smart.flow.admin;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * @author qinluo
 * @date 2023-01-30 17:23:14
 * @since 1.0.0
 */
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 8602038114418123662L;

    public static final int SUCCESS_CODE = 200;

    private String message = "";
    private int code = SUCCESS_CODE;
    private T data;
    private long total;

    public Result(int code) {
        this.code(code);
    }

    public Result() {
        this.code(SUCCESS_CODE);
    }

    public T getData() {
        return this.data;
    }

    public boolean isSuccess() {
        return code == SUCCESS_CODE;
    }

    public long getTotal() {
        return this.total;
    }

    public Result<T> total(long totalCount) {
        this.total = totalCount;
        return this;
    }

    public Result<T> data(T model) {
        this.data = model;
        return this;
    }


    public String getMessage() {
        return this.message;
    }

    public Result<T> message(String message) {
        this.message = message;
        return this;
    }

    public int getCode() {
        return this.code;
    }

    public Result<T> code(int code) {
        this.code = code;
        return this;
    }

    public static <T> String ok(T data) {
        Result<T> resultData = new Result<>(SUCCESS_CODE);
        return JSON.toJSONString(resultData.data(data));
    }

    public static <T> String fail(int code, String msg) {
        Result<T> resultData = new Result<>(500);
        return JSON.toJSONString(resultData.code(code).message(msg));
    }


}
