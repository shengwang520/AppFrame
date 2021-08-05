package com.common.base.network.base;

import com.common.base.network.ApiError;
import com.common.base.network.callback.IDataCallback;
import com.orhanobut.logger.Logger;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 统一数据请求结果处理
 */
public class BaseObserver<T> implements Observer<BaseResponse<T>> {
    private IDataCallback<T> iDataCallback;

    public BaseObserver(IDataCallback<T> iDataCallback) {
        this.iDataCallback = iDataCallback;
    }

    @Override
    public void onSubscribe(@NotNull Disposable d) {
        Logger.d("api observer onSubscribe");
    }

    @Override
    public void onNext(@NotNull BaseResponse<T> tBaseResponse) {
        Logger.d("api observer onNext");
        switch (tBaseResponse.status) {
            case ApiError.API_SUCCESS:
                if (iDataCallback != null) iDataCallback.success(tBaseResponse.data);
                break;
            case ApiError.API_ERROR_2:
                onLoginLose();
                break;
            default:
                if (iDataCallback != null) {
                    iDataCallback.failure(tBaseResponse.status, tBaseResponse.msg, tBaseResponse.data);
                }
                break;
        }
    }

    @Override
    public void onError(@NotNull Throwable e) {
        Logger.d("api observer onError");
        if (iDataCallback != null) {
            iDataCallback.failure(ApiError.API_ERROR_404, "", null);
        }
    }

    @Override
    public void onComplete() {
        Logger.d("api observer onComplete");
        if (iDataCallback != null) {
            iDataCallback.finish();
        }
    }

    /**
     * 用户登录信息失效，需要重新登录
     */
    private void onLoginLose() {
        //todo 跳转至登录界面
    }
}
