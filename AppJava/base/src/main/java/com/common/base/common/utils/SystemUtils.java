package com.common.base.common.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import com.common.base.common.base.App;

import java.util.Locale;
import java.util.UUID;

/**
 * 安卓系统参数信息
 */
public class SystemUtils {
    private static final String DATA_SYSTEM = "data_system";
    private static final String DATA_UUID = "data_uuid";

    private SharedPreferences getSharedPreferences() {
        return App.getInstance().getSharedPreferences(DATA_SYSTEM, Context.MODE_PRIVATE);
    }

    /**
     * 保存数据
     */
    private void saveData(String key, String value) {
        getSharedPreferences().edit().putString(key, value).apply();
    }

    /**
     * 获取数据
     */
    private String getData(String key) {
        return getSharedPreferences().getString(key, "");
    }

    /**
     * 获取版本名称
     */
    public String getVersionName() {
        PackageManager packageManager = App.getInstance().getPackageManager();
        try {
            PackageInfo info = packageManager.getPackageInfo(App.getInstance().getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "1.0.0";
    }

    /**
     * 获取版本号
     */
    public int getVersionCode() {
        PackageManager packageManager = App.getInstance().getPackageManager();
        try {
            PackageInfo info = packageManager.getPackageInfo(App.getInstance().getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 获取手机系统版本哈
     */
    public String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     */
    public String getSystemModel() {
        return Build.MODEL;
    }

    /**
     * 获取系统语言
     */
    public String getSystemLang() {
        Locale locale = Locale.getDefault();
        return locale.getLanguage() + "_" + locale.getCountry();
    }

    /**
     * 获取设备唯一标识
     */
    @SuppressLint("HardwareIds")
    public String getAndroidId() {
        String uuid = getData(DATA_UUID);
        if (TextUtils.isEmpty(uuid)) {
            String androidId = Settings.Secure.getString(App.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);
            if (TextUtils.isEmpty(androidId) || TextUtils.equals(androidId, "9774d56d682e549c")) {//部分手机可能生成同一androidId
                uuid = UUID.randomUUID().toString();
            } else {
                uuid = androidId;
            }
            saveData(DATA_UUID, uuid);
        }
        return uuid;
    }

}
