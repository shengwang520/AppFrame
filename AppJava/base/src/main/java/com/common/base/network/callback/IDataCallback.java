package com.common.base.network.callback;

/**
 * 数据获取回调
 */
public interface IDataCallback<T> {

    /**
     * 数据获取成功
     */
    void success(T t);

    /**
     * 数据获取失败
     *
     * @param error 失败错误码
     * @param msg   原因
     */
    void failure(int error, String msg, T t);

    /**
     * 数据获取结束
     */
    void finish();
}
