package com.common.base.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 基础自定义组件
 */
public class BaseCustomView extends FrameLayout implements ICustomView {
    public BaseCustomView(@NonNull Context context) {
        this(context, null);
    }

    public BaseCustomView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseCustomView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    @Override
    public void initView(AttributeSet attrs) {

    }

    @Override
    public void show() {
        setVisibility(VISIBLE);
    }

    @Override
    public void hide() {
        setVisibility(GONE);
    }

    @Override
    public boolean onBack() {
        return false;
    }

    @Override
    public boolean isShowing() {
        return getVisibility() == VISIBLE;
    }
}
