package com.common.base.network.callback;

/**
 *
 */
public interface IDataCallback<T> {

    void success(T t);

    void failure(int error, String msg, T t);

    void finish();
}
