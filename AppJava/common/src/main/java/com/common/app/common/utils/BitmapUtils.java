package com.common.app.common.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

import com.common.app.R;
import com.common.app.common.widget.CustomProgressDialog;
import com.orhanobut.logger.Logger;
import com.sheng.wang.media.utils.FileUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * 选择单张图片处理逻辑
 */
public class BitmapUtils {

    /**
     * 保存图片到本地
     */
    public static void saveImage2Local(final Context context, String url) {
        final CustomProgressDialog dialog = DialogUtils.create(context);
        Observable.fromArray(url)
                .map(new Function<String, Bitmap>() {
                    @Override
                    public Bitmap apply(@NotNull String s) throws Exception {
                        URL pictureUrl = new URL(s);
                        InputStream in = pictureUrl.openStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(in);
                        in.close();
                        return bitmap;
                    }
                })
                .map(new Function<Bitmap, String>() {
                    @Override
                    public String apply(@NotNull Bitmap bitmap) throws Exception {
                        File file = FileUtils.createFile(context, Environment.DIRECTORY_PICTURES, ".jpg");
                        FileOutputStream out = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.flush();
                        out.close();
                        return file.getAbsolutePath();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull String s) {
                        dialog.dismiss();
                        Logger.d("image save success path:" + s);
                        //刷新系统相册
                        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + s)));
                        ToastUtils.show(context, context.getString(R.string.image_save_success));
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        dialog.dismiss();
                        e.printStackTrace();
                        ToastUtils.show(context, context.getString(R.string.image_save_fail));
                    }

                    @Override
                    public void onComplete() {
                        dialog.dismiss();
                    }
                });
    }

}
