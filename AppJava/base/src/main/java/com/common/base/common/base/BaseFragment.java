package com.common.base.common.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * fragment 上层基类
 */
public abstract class BaseFragment extends Fragment {
    public static final String DATA_KEY = "data_page";//页面page

    protected int page;
    protected boolean isMore;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        initHolder(view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    /**
     * 返回界面布局id
     */
    public abstract int getLayoutId();

    /**
     * 继承BaseFragment的碎片必须要实现的方法，在填充界面时，初始化控件
     */
    protected abstract void initHolder(View view);

    /**
     * 继承BaseFragment的碎片必须要实现的方法, 实现业务逻辑
     */
    protected abstract void initView();
}
