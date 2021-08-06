package com.common.base.common.utils;

import static android.view.View.MeasureSpec.UNSPECIFIED;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

/**
 * 屏幕测量工具
 */
public class MetricsUtils {

    /**
     * 获取控件的宽度
     *
     * @param offset 偏移量[dp]
     * @param count  每行个数
     */
    public static int getWidth(Context context, float offset, int count) {
        DisplayMetrics displayMetrics = getDisplayMetrics(context);
        return (int) ((displayMetrics.widthPixels - dp2px(displayMetrics, offset)) / count);
    }

    public static DisplayMetrics getDisplayMetrics(Context ctx) {
        return ctx.getResources().getDisplayMetrics();
    }

    /**
     * dp转px
     */
    public static int dp2px(Context context, float dp) {
        return (int) dp2px(context.getResources().getDisplayMetrics(), dp);
    }

    public static float dp2px(DisplayMetrics displayMetrics, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }

    /**
     * sp转px的方法。
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale);
    }

    /**
     * 获取控件的高度
     *
     * @param width 宽度
     * @param scale 宽高比
     */
    public static int getHeight(int width, float scale) {
        return (int) (width / scale);
    }

    public static void measureChildren(View parent) {
        parent.measure(UNSPECIFIED, UNSPECIFIED);
    }


    /**
     * 获取屏幕宽
     */
    public static int getWidth(Context context) {
        DisplayMetrics displayMetrics = getDisplayMetrics(context);
        return displayMetrics.widthPixels;
    }


    /**
     * 获取屏幕的高
     */
    public static int getHeight(Context context) {
        return getHeight(context, true);
    }

    /**
     * 获取屏幕的高
     */
    public static int getHeight(Context context, boolean real) {
        DisplayMetrics displayMetrics = getDisplayMetrics(context);
        if (real && context instanceof Activity) {
            //适配刘海屏高度获取
            ((Activity) context).getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        }
        return displayMetrics.heightPixels;
    }
}
