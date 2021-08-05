package com.common.base.common.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * toast工具
 */
public class ToastUtils {

    /**
     * 显示
     */
    public static void show(Context context, int resId) {
        if (context == null) return;
        Toast toast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
        toast.setText(resId);
        toast.show();
    }

    /**
     * 显示
     */
    public static void show(Context context, String msg) {
        if (context == null) return;
        if (TextUtils.isEmpty(msg)) return;
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.setText(msg);
        toast.show();
    }
}
