package com.common.base.common.base;

import androidx.multidex.MultiDexApplication;

/**
 * 基础Application  主app model 需要继承并实现相关逻辑，并在xml配置
 */
public class App extends MultiDexApplication {

    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static App getInstance() {
        return instance;
    }
}
