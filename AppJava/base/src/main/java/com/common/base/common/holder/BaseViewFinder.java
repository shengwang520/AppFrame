package com.common.base.common.holder;

import android.app.Activity;
import android.view.View;

import androidx.annotation.IdRes;

/**
 * 页面布局持有基类
 */
public abstract class BaseViewFinder {
    private final Activity activity;

    public BaseViewFinder(Activity activity) {
        this.activity = activity;
    }

    protected <T extends View> T findViewById(@IdRes int id) {
        return activity.findViewById(id);
    }

}
