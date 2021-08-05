package com.common.base.common.widget;

import android.util.AttributeSet;

/**
 * 自定义布局接口
 */
public interface ICustomView {

    /**
     * 初始化界面
     */
    void initView(AttributeSet attrs);

    /**
     * 显示
     */
    void show();

    /**
     * 隐藏
     */
    void hide();

    /**
     * 物理返回键处理,在activity的onBackPressed方法中拦截
     *
     * @return true 拦截返回键
     */
    boolean onBack();

    /**
     * 界面是否显示
     */
    boolean isShowing();

}
