package com.common.base.common.compat;

import android.content.Context;
import android.content.Intent;

import androidx.core.app.ActivityCompat;

/**
 * 页面跳转
 */
public class AppCompat {
    /**
     * 跳转界面
     */
    public static void startActivity(Context context, Intent intent) {
        ActivityCompat.startActivity(context, intent, null);
    }

    /**
     * 跳转界面
     */
    public static void startActivities(Context context, Intent... intent) {
        ActivityCompat.startActivities(context, intent, null);
    }
}
