package com.common.base.common.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * 适配5.0+ webView 问题
 */
public class BWebView extends WebView {

    public BWebView(Context context) {
        this(context, null);
    }

    public BWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(getFixedContext(context), attrs, defStyleAttr);
    }

    public static Context getFixedContext(Context context) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            // Android Lollipop 5.0 & 5.1
            return context.createConfigurationContext(new Configuration());
        }
        return context;
    }

}
