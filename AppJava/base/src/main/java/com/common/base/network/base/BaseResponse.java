package com.common.base.network.base;

/**
 * api 接口返回统一实体格式
 */
public class BaseResponse<T> {
    /**
     * 状态码
     */
    public int status;
    /**
     * 说明
     */
    public String msg;
    /**
     * 数据
     */
    public T data;
}
