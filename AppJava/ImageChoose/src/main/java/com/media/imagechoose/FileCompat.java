package com.media.imagechoose;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.media.imagechoose.impl.CallBack;
import com.media.imagechoose.impl.IFile;
import com.media.imagechoose.impl.ILoadFile;
import com.media.imagechoose.model.FileBean;
import com.media.imagechoose.model.FileFolder;
import com.orhanobut.logger.Logger;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * 本地文件扫描
 */
public class FileCompat implements ILoadFile {
    private Context context;
    private List<FileFolder> listImages;
    private CallBack callBack;
    private HashMap<String, Integer> tmpDir = new HashMap<>();//临时的辅助类，用于防止同一个文件夹的多次扫描

    public FileCompat(Context context, CallBack callBack) {
        this.context = context;
        this.callBack = callBack;
        listImages = new ArrayList<>();
    }

    @Override
    public void loadImages() {
        final FileFolder all = new FileFolder();
        all.setName(context.getString(R.string.all_images));
        listImages.add(all);
        Observable
                .create(new ObservableOnSubscribe<FileBean>() {

                    @Override
                    public void subscribe(@NotNull ObservableEmitter<FileBean> e) {
                        Cursor mCursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                new String[]{MediaStore.Images.ImageColumns.DATA, MediaStore.Images.Media._ID, MediaStore.Images.Media.WIDTH, MediaStore.Images.Media.HEIGHT}, "", null,
                                MediaStore.MediaColumns.DATE_ADDED + " DESC");
                        if (mCursor != null && mCursor.moveToFirst()) {
                            do {
                                String path = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                                String id = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                                String uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id).toString();
                                int width = mCursor.getInt(mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH));
                                int height = mCursor.getInt(mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT));
                                FileBean fileBean = new FileBean(path);
                                fileBean.setFileUri(uri);
                                fileBean.setWidth(width);
                                fileBean.setHeight(height);
                                Logger.d("file start query:" + fileBean.toString());
                                e.onNext(fileBean);
                            } while (mCursor.moveToNext());
                        }
                        if (mCursor != null)
                            mCursor.close();

                        e.onComplete();
                    }
                })
                .filter(new Predicate<FileBean>() {
                    @Override
                    public boolean test(@NotNull FileBean fileBean) {
                        return fileBean.getWidth() >= 800
                                || fileBean.getHeight() >= 800;
                    }
                })
                .filter(new Predicate<FileBean>() {
                    @Override
                    public boolean test(@NonNull FileBean fileBean) throws Exception {
                        return fileBean.isExists();
                    }
                })
                .map(new Function<FileBean, FileFolder>() {
                    @Override
                    public FileFolder apply(@NotNull FileBean fileBean) {
                        all.images.add(fileBean);
                        all.setFirstImagePath(all.images.get(0).getImgpath());

                        // 获取该图片的父路径名
                        String fileParentName = fileBean.getFileParentName();

                        FileFolder imageFolder;
                        if (!tmpDir.containsKey(fileParentName)) {
                            // 初始化imageFolder
                            imageFolder = new FileFolder();
                            imageFolder.setName(fileParentName);
                            imageFolder.setFirstImagePath(fileBean.getImgpath());
                            listImages.add(imageFolder);
                            tmpDir.put(fileParentName, listImages.indexOf(imageFolder));
                        } else {
                            imageFolder = listImages.get(tmpDir.get(fileParentName));
                        }
                        imageFolder.images.add(new FileBean(fileBean.getImgpath()));
                        return imageFolder;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FileFolder>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull FileFolder imageFolder) {
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        if (callBack != null) {
                            callBack.onError();
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (callBack != null) {
                            callBack.onSuccess(listImages);
                        }
                    }
                });
    }

    @Override
    public void loadVideos() {
        final FileFolder all = new FileFolder();
        all.setName(context.getString(R.string.all_videos));
        listImages.add(all);
        Observable
                .create(new ObservableOnSubscribe<FileBean>() {

                    @Override
                    public void subscribe(@NotNull ObservableEmitter<FileBean> e) {
                        Cursor mCursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                new String[]{MediaStore.Video.VideoColumns.DATA, MediaStore.Video.Media.DURATION, MediaStore.Video.Media._ID}, "", null,
                                MediaStore.MediaColumns.DATE_ADDED + " DESC");
                        if (mCursor != null && mCursor.moveToFirst()) {
                            do {
                                String path = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)); // 路径
                                long duration = mCursor.getInt(mCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)); // 时长 （毫秒）
                                String id = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                                String uri = Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id).toString();
                                FileBean imageBean = new FileBean(path, IFile.Type.FILE_TYPE_VIDEO);
                                imageBean.setFileUri(uri);
                                imageBean.setVideoTime(duration);
                                e.onNext(imageBean);
                            } while (mCursor.moveToNext());
                        }
                        if (mCursor != null)
                            mCursor.close();

                        e.onComplete();
                    }
                })
                .filter(new Predicate<FileBean>() {
                    @Override
                    public boolean test(@NotNull FileBean imageBean) throws Exception {
                        //视频时长限制在10-30秒
                        return imageBean.getVideoTime() >= 3 * 1000 && imageBean.getVideoTime() <= 30 * 1000;
                    }
                })
                .filter(new Predicate<FileBean>() {
                    @Override
                    public boolean test(@NonNull FileBean fileBean) throws Exception {
                        return fileBean.isExists();
                    }
                })
                .map(new Function<FileBean, FileFolder>() {
                    @Override
                    public FileFolder apply(@NotNull FileBean imageBean) throws Exception {
                        all.images.add(imageBean);
                        all.setFirstImagePath(all.images.get(0).getImgpath());

                        // 获取该图片的父路径名
                        String fileParentName = imageBean.getFileParentName();

                        FileFolder imageFolder;
                        if (!tmpDir.containsKey(fileParentName)) {
                            // 初始化imageFloder
                            imageFolder = new FileFolder();
                            imageFolder.setName(fileParentName);
                            imageFolder.setFirstImagePath(imageBean.getImgpath());
                            listImages.add(imageFolder);
                            tmpDir.put(fileParentName, listImages.indexOf(imageFolder));
                        } else {
                            imageFolder = listImages.get(tmpDir.get(fileParentName));
                        }
                        imageFolder.images.add(imageBean);
                        return imageFolder;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FileFolder>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {
                        Logger.d("find video onSubscribe");

                    }

                    @Override
                    public void onNext(@NotNull FileFolder imageFolder) {
                        Logger.d("find video onNext");
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Logger.d("find video onError");
                        if (callBack != null) {
                            callBack.onError();
                        }
                    }

                    @Override
                    public void onComplete() {
                        Logger.d("find video complete");
                        if (callBack != null) {
                            callBack.onSuccess(listImages);
                        }
                    }
                });
    }

}
