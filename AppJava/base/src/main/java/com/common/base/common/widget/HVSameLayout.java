package com.common.base.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * 宽高一样的布局，以宽为准,在xml中作为容器使用
 */
public class HVSameLayout extends FrameLayout {
    public HVSameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);//让宽高一样
    }
}
