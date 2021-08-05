package com.common.base.common.holder;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.common.base.R;

public abstract class ToolbarFinder extends BaseViewFinder {
    private AppCompatActivity appCompatActivity;
    public Toolbar toolbar;

    public ToolbarFinder(Activity activity) {
        super(activity);
        if (activity instanceof AppCompatActivity) {
            this.appCompatActivity = (AppCompatActivity) activity;
        }
        toolbar = findViewById(R.id.toolbar);
    }

    /**
     * 绑定toolbar 设置返回按钮图片
     *
     * @param resId 左上角返回按钮图片
     */
    private void initToolbar(AppCompatActivity activity, int resId) {
        activity.setSupportActionBar(toolbar);//toolbar与Activity绑定。
        ActionBar bar = activity.getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);//显示toolbar左上角的返回按钮。
            bar.setHomeAsUpIndicator(resId);//给左上角返回按钮设置图片。
            bar.setElevation(0);//设置阴影，高版本才能看见。
        }
    }

    /**
     * 初始化自定义布局
     *
     * @param gravity 显示的位置
     */
    protected void initTabView(View view, int gravity) {
        ActionBar actionBar = appCompatActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);//显示toolbar左上角的返回按钮。
        }
        Toolbar.LayoutParams params = new Toolbar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER | gravity);
        toolbar.addView(view, params);
    }
}
