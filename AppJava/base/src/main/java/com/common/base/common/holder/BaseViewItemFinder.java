package com.common.base.common.holder;

import android.content.Context;
import android.view.View;

/**
 * 顶层view布局
 */
public abstract class BaseViewItemFinder {
    private final View view;

    public BaseViewItemFinder(View view) {
        this.view = view;
    }

    public Context getContext() {
        return view.getContext();
    }

    public String getString(int resId) {
        return getContext().getString(resId);
    }
}
