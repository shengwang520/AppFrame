package com.common.base.network.callback;

import android.app.Dialog;
import android.content.Context;

public class SimpleDataCallback<T> implements IDataCallback<T> {

    private Context context;
    private Dialog dialog;

    public SimpleDataCallback() {
    }

    public SimpleDataCallback(Context context) {
        this.context = context;
    }

    public SimpleDataCallback(Context context, Dialog dialog) {
        this.context = context;
        this.dialog = dialog;
    }

    @Override
    public void success(T t) {

    }

    @Override
    public void failure(int error, String msg, T t) {

    }

    @Override
    public void finish() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
