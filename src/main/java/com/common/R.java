package com.common;
import cn.hutool.http.HttpStatus;

import java.io.Serializable;

public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private static int SUCCESS_STATUS = HttpStatus.HTTP_OK;

    private static int FAIL_STATUS = HttpStatus.HTTP_INTERNAL_ERROR;
    /**
     * 返回代码
     */
    private int code;
    /**
     * 返回信息
     */
    private String msg;
    /**
     * 返回数据
     */
    private T data;

    private static <T> R<T> build(int code,String msg,T data){
        R<T> r = new R<>();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }

    public static <T> R<T> ok()
    {
        return build( SUCCESS_STATUS,null,null);
    }

    public static <T> R<T> ok(String msg)
    {
        return build( SUCCESS_STATUS,msg,null);
    }

    public static <T> R<T> ok(T data)
    {
        return build( SUCCESS_STATUS,null,data);
    }

    public static <T> R<T> ok(T data, String msg)
    {
        return build( SUCCESS_STATUS,msg,data);
    }

    public static <T> R<T> fail()
    {
        return build(FAIL_STATUS, null, null);
    }

    public static <T> R<T> fail(String msg)
    {
        return build(FAIL_STATUS, msg, null);
    }

    public static <T> R<T> fail(T data)
    {
        return build(FAIL_STATUS, null, data);
    }

    public static <T> R<T> fail(T data, String msg)
    {
        return build(FAIL_STATUS, msg, data);
    }

    public static <T> R<T> fail(int code ,String msg)
    {
        return build(code, msg, null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(final int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(final String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public R setData(final T data) {
        this.data = data;
        return null;
    }

    public String toString() {
        return "R(code=" + this.getCode() + ", data=" + this.getData() + ", msg=" + this.getMsg() + ")";
    }
}
