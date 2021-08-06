package com.common.base.common.compat;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;
import android.view.DisplayCutout;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.common.base.R;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.orhanobut.logger.Logger;

import java.lang.reflect.Method;


/**
 * ui一体化状态栏
 * Created by wangshengqiang on 2016/12/16.
 */

public class UiCompat {

    /**
     * 图片全屏透明状态栏（图片位于状态栏下面）
     */
    public static void setImageTransparent(Activity activity) {
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, R.color.transparent));
    }

    /**
     * 设置状态栏的显示隐藏
     */
    public static void fullscreen(Activity activity, boolean enable) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        if (enable) { //显示状态栏
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            activity.getWindow().setAttributes(lp);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else { //隐藏状态栏
            lp.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.getWindow().setAttributes(lp);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    /**
     * 设置view的距上的顶部的距离
     */
    public static void setMarginTopViewParams(Activity activity, View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params instanceof CollapsingToolbarLayout.LayoutParams) {
            ((CollapsingToolbarLayout.LayoutParams) params).topMargin = getStatusBarHeight(activity);
        } else if (params instanceof RelativeLayout.LayoutParams) {
            ((RelativeLayout.LayoutParams) params).topMargin = getStatusBarHeight(activity);
        } else if (params instanceof LinearLayout.LayoutParams) {
            ((LinearLayout.LayoutParams) params).topMargin = getStatusBarHeight(activity);
        }
        view.setLayoutParams(params);
    }


    /**
     * 获取状态栏的高度
     */
    public static int getStatusBarHeight(Context activity) {
        int result = 0;
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = activity.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * Desc: 获取虚拟按键高度 放到工具类里面直接调用即可
     */
    public static int getNavigationBarHeight(Context context) {
        int result = 0;
        if (isNavigationBarExist(context)) {
            Resources res = context.getResources();
            int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId);
            }
        }
        Logger.d("虚拟键盘高度" + result);
        return result;
    }

    /**
     * 判断是否存在导航栏
     * 需要再activity onOpenCamera 中添加一下代码判断才生效
     * if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
     * window.navigationBarColor = ContextCompat.getColor(this, R.color.color_white)
     * }
     */
    private static boolean isNavigationBarExist(@NonNull Context context) {
        String NAVIGATION = "navigationBarBackground";
        if (context instanceof Activity) {
            ViewGroup vp = (ViewGroup) ((Activity) context).getWindow().getDecorView();
            for (int i = 0; i < vp.getChildCount(); i++) {
                vp.getChildAt(i).getContext().getPackageName();

                if (vp.getChildAt(i).getId() != -1 && NAVIGATION.equals(context.getResources().getResourceEntryName(vp.getChildAt(i).getId()))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 设置状态栏文案颜色
     */
    public static void setAndroidNativeLightStatusBar(Activity activity, boolean dark) {
        View decor = activity.getWindow().getDecorView();
        if (dark) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        } else {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }


    public static final int VIVO_NOTCH = 0x00000020;//是否有刘海

    /**
     * 全屏适配刘海方案
     */
    public static void setActivityFullLiuHai(Activity activity) {
        // 设置页面全屏显示 P适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Window window = activity.getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            // 设置页面延伸到刘海区显示
            window.setAttributes(lp);
        } else {
            if (hasNotchInScreen(activity)) {
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                setImageTransparent(activity);
            }
        }

    }

    /**
     * 是否有刘海屏
     */
    public static boolean hasNotchInScreen(Activity activity) {
        // android  P 以上有标准 API 来判断是否有刘海屏
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                DisplayCutout displayCutout = activity.getWindow().getDecorView().getRootWindowInsets().getDisplayCutout();
                // 说明有刘海屏
                return displayCutout != null;
            } else {
                // 通过其他方式判断是否有刘海屏  目前官方提供有开发文档的就 小米，vivo，华为（荣耀），oppo
                String manufacturer = Build.MANUFACTURER;
                if (TextUtils.isEmpty(manufacturer)) {
                    return false;
                } else if (manufacturer.equalsIgnoreCase("HUAWEI")) {
                    return hasNotchAtHuawei(activity);
                } else if (manufacturer.equalsIgnoreCase("xiaomi")) {
                    return hasNotchAtMiui(activity);
                } else if (manufacturer.equalsIgnoreCase("oppo")) {
                    return hasNotchAtOPPO(activity);
                } else if (manufacturer.equalsIgnoreCase("vivo")) {
                    return hasNotchAtVivo(activity);
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断华为是否有刘海屏
     */
    public static boolean hasNotchAtHuawei(Context context) {
        boolean ret = false;
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class HwNotchSizeUtil = classLoader.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            ret = (boolean) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            Logger.e("Notch", "hasNotchAtHuawei ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Logger.e("Notch", "hasNotchAtHuawei NoSuchMethodException");
        } catch (Exception e) {
            Logger.e("Notch", "hasNotchAtHuawei Exception");
        }
        Logger.d("liu hai Huawei:" + ret);
        return ret;
    }

    /**
     * 判断vivo是否有刘海屏
     */
    public static boolean hasNotchAtVivo(Context context) {
        boolean ret = false;
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class FtFeature = classLoader.loadClass("android.util.FtFeature");
            Method method = FtFeature.getMethod("isFeatureSupport", int.class);
            ret = (boolean) method.invoke(FtFeature, VIVO_NOTCH);
        } catch (ClassNotFoundException e) {
            Logger.e("Notch", "hasNotchAtVivo ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Logger.e("Notch", "hasNotchAtVivo NoSuchMethodException");
        } catch (Exception e) {
            Logger.e("Notch", "hasNotchAtVivo Exception");
        }
        Logger.d("liu hai Vivo:" + ret);
        return ret;
    }

    /**
     * 判断oppo是否有刘海屏
     */
    public static boolean hasNotchAtOPPO(Context context) {
        boolean ret = context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
        Logger.d("liu hai OPPO:" + ret);
        return ret;
    }


    /**
     * 判断miui是否有刘海屏
     */
    public static boolean hasNotchAtMiui(Context context) {
        boolean ret = false;
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class FtFeature = classLoader.loadClass("android.os.SystemProperties");
            Method get = FtFeature.getMethod("getInt", String.class, int.class);
            ret = (int) (get.invoke(FtFeature, "ro.miui.notch", 0)) == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Logger.d("liu hai miui:" + ret);
        return ret;
    }
}
