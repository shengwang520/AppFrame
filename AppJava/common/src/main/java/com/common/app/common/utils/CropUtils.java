package com.common.app.common.utils;

import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.common.app.R;
import com.sheng.wang.media.utils.FileUtils;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;

/**
 * 图片裁切
 */
public class CropUtils {

    /**
     * 图片裁切
     */
    public static void crop(AppCompatActivity context, String path) {
        File photoFile = new File(path);
        File outFile;
        try {
            outFile = FileUtils.createFile(context, Environment.DIRECTORY_PICTURES, ".jpg");

            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && path.startsWith("content://")) {
                uri = Uri.parse(path);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                uri = FileProvider.getUriForFile(context, context.getPackageName() + ".FileProvider", photoFile);
            } else {
                uri = Uri.fromFile(photoFile);
            }

            UCrop.Options options = new UCrop.Options();
            //设置裁剪图片可操作的手势
            options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);
            //设置隐藏底部容器，默认显示
//        options.setHideBottomControls(true);
            //设置toolbar颜色
            options.setToolbarColor(ActivityCompat.getColor(context, R.color.colorPrimary));
            //设置状态栏颜色
            options.setStatusBarColor(ActivityCompat.getColor(context, R.color.colorPrimary));
            options.setToolbarWidgetColor(ActivityCompat.getColor(context, R.color.color_222222));
//            options.setToolbarCancelDrawable(R.drawable.return_icon);

            UCrop.of(uri, Uri.fromFile(outFile))
                    .withAspectRatio(1, 1)
                    .withMaxResultSize(800, 800)
                    .withOptions(options)
                    .start(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
