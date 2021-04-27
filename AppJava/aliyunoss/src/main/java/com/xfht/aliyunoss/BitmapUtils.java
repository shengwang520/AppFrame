package com.xfht.aliyunoss;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import com.orhanobut.logger.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * 选择单张图片处理逻辑
 * Created by wang on 2016/2/15.
 */
public class BitmapUtils {
    private static final int minSize = 1224;//原图最小边大于1600,等比压缩到1600
    private static final String CACHE_IMG = "/CacheImg";//图片缓存目录

    /**
     * 删除图片缓存文件夹
     */
    public static void deleteFile(Context context) {
        String oPath = context.getFilesDir().getAbsolutePath() + CACHE_IMG;
        File file = new File(oPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }
    }

    /**
     * 获取图片缓存路径
     */
    private static String getCacheImageFile(Context context) {
        String path = context.getFilesDir().getAbsolutePath() + CACHE_IMG;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        return path;
    }

    /**
     * 开始压缩图片
     *
     * @param path 原图片路径
     */
    public static void startCompressImage(final Context context, final String path, final Callback callback) {
        Observable.fromArray(path)
                .map(new Function<String, Bitmap>() {
                    @Override
                    public Bitmap apply(String s) {
                        Bitmap bitmap;
                        try {
                            bitmap = BitmapFactory.decodeFile(s);
                        } catch (OutOfMemoryError e) {
                            e.printStackTrace();
                            // 提示系统，进行内存回收
                            System.gc();
                            BitmapFactory.Options opts = new BitmapFactory.Options();
                            opts.inSampleSize = 4;
                            bitmap = BitmapFactory.decodeFile(s, opts);
                            //压缩图片重新加载
                        }
                        return bitmap;
                    }
                })
                .map(new Function<Bitmap, Bitmap>() {
                    @Override
                    public Bitmap apply(Bitmap bitmap) {
                        int d = getBitmapDegree(path);
                        Logger.d("-degree->" + d);
                        if (d > 0) {
                            bitmap = rotateBitmapByDegree(bitmap, d);
                        }
                        return bitmap;
                    }
                })
                .map(new Function<Bitmap, Bitmap>() {
                    @Override
                    public Bitmap apply(Bitmap bitmap) {
                        if (isValid(bitmap.getWidth(), bitmap.getHeight())) {//按比例缩放
                            bitmap = zoomImg(bitmap);
                        }
                        return bitmap;
                    }
                })
                .map(new Function<Bitmap, ByteArrayOutputStream>() {
                    @Override
                    public ByteArrayOutputStream apply(Bitmap bitmap) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                        float size = out.toByteArray().length / 1024f;
                        Logger.d("压缩放缩后的图片大小-》" + size);

                        if (size > 1000) {
                            out.reset();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 99, out);
                            size = out.toByteArray().length / 1024f;
                            Logger.d("压缩99后的图片大小-》" + size);
                        }
                        bitmap.recycle();
                        return out;
                    }
                })
                .map(new Function<ByteArrayOutputStream, String>() {
                    @Override
                    public String apply(ByteArrayOutputStream out) throws Exception {
                        String imgPath = getCacheImageFile(context) + "/" + System.currentTimeMillis() + ".jpg";
                        File f = new File(imgPath);
                        FileOutputStream fOut = new FileOutputStream(f);
                        out.writeTo(fOut);
                        out.flush();

                        out.reset();
                        out.close();
                        fOut.close();
                        return imgPath;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        if (callback != null) callback.onComplete(s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        //图片压缩出现异常，返回原图路径
                        if (callback != null) callback.onComplete(path);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 缩放图片
     */
    private static Bitmap zoomImg(Bitmap bm) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();

        Logger.d("image height-w-->" + width + "-h-" + height);
        int newWidth;
        int newHeight;

        if (width > height) {
            newHeight = minSize;
            newWidth = (int) ((width / (float) height) * newHeight);
        } else if (width < height) {
            newWidth = minSize;
            newHeight = (int) ((height / (float) width) * newWidth);
        } else {
            newWidth = newHeight = minSize;
        }
        Logger.d("image height-w-->" + newWidth + "-h-" + newHeight);

        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap results = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        bm.recycle();
        return results;
    }

    /**
     * 判断图片的最小边是否大于minSize;
     */
    private static boolean isValid(float width, float height) {
        return Math.min(width, height) > minSize;
    }

    /**
     * 获取图片旋转角度
     */
    private static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;

    }

    /**
     * 还原旋转图片
     *
     * @param degree 愿旋转角度
     */
    private static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

    public interface Callback {
        /**
         * 图片处理界面
         */
        void onComplete(String path);
    }

}
