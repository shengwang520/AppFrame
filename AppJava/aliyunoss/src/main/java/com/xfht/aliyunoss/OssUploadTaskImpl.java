package com.xfht.aliyunoss;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSAuthCredentialsProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.utils.BinaryUtil;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.ObjectMetadata;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

import static com.xfht.aliyunoss.OssKeyInfo.OSS_BUCKET;
import static com.xfht.aliyunoss.OssKeyInfo.OSS_ENDPOINT;
import static com.xfht.aliyunoss.OssKeyInfo.OSS_FILENAME;
import static com.xfht.aliyunoss.OssKeyInfo.OSS_STS_SERVER;

/**
 * 阿里云文件上传
 * Created by wang on 2015/12/25.
 * 如果T是实体，则需重写toString()方法，返回需要上传的图片路径
 */
public class OssUploadTaskImpl {
    private Context context;
    private OSS oss;
    private PutObjectRequest put;
    private OSSAsyncTask task;

    private String userId = "00000";

    private LinkedList<String> filepaths;//需要上传的文件
    private OssUploadListener ossUploadListener;
    private LinkedList<String> imgurls;//上传成功后的结果路径
    private boolean isProgress;//是否需要计算进度
    private long allSize;//文件总大小
    private long uploadSize, uploadSize2;//当前进度
    private boolean isUploading;//是否正在上传
    private boolean isCompressImage;//是否需要压缩图片，默认不压缩

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0://失败
                    if (isUploading)
                        if (ossUploadListener != null) ossUploadListener.onFailure();
                    break;
                case 1://成功
                    if (ossUploadListener != null) ossUploadListener.onSuccess(imgurls);
                    break;
                case 2://进度
                    long progress = uploadSize + uploadSize2;
                    Logger.d(allSize + "<-当前进度->" + progress);
                    if (ossUploadListener != null)
                        ossUploadListener.onProgress(allSize, Math.min(allSize, progress));
                    break;
            }
            return false;
        }
    });

    /**
     * 多文件上传
     *
     * @param filepaths 要上传的文件集
     */
    public OssUploadTaskImpl(Context context, LinkedList<String> filepaths) {
        this.context = context;
        this.filepaths = filepaths;
        init();
    }

    /**
     * 单文件上传
     *
     * @param filepath 上传文件
     */
    public OssUploadTaskImpl(Context context, String filepath) {
        this.context = context;
        this.filepaths = new LinkedList<>();
        filepaths.add(filepath);
        init();
    }

    /**
     * 设置上传的文件名字为当前的上传时间
     */
    private static String generateFileName(String path) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.CHINA);
        String fileName = sdf.format(new Date()) + path.substring(path.lastIndexOf("."));
        Log.d("oss_image_name", fileName);
        return fileName;
    }

    /**
     * 设置用户id,创建专属文件夹
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setCompressImage(boolean compressImage) {
        isCompressImage = compressImage;
    }

    /**
     * 初始化
     */
    private void init() {
        imgurls = new LinkedList<>();
        //初始化OSSClient
        OSSCredentialProvider credentialProvider = new OSSAuthCredentialsProvider(OSS_STS_SERVER);
        oss = new OSSClient(context, OSS_ENDPOINT, credentialProvider);
    }

    /**
     * 初始化总大小
     */
    private void initAllSize() {
        for (String t : filepaths) {
            allSize += getImgFileSize(t);
        }
    }

    /**
     * 获取文件大小
     */
    private int getImgFileSize(String path) {
        File f = new File(path);
        if (f.exists()) {
            FileInputStream fis;
            try {
                fis = new FileInputStream(f);
                return fis.available();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * 设置上传回调
     */
    public void setOssUploadListener(OssUploadListener ossUploadListener) {
        this.ossUploadListener = ossUploadListener;
    }

    /**
     * 设置是否开启进度回调
     */
    public void setProgress(boolean progress) {
        isProgress = progress;
        initAllSize();
    }

    /**
     * 开始上传
     */
    public void onStart() {
        isUploading = true;
        if (isCompressImage) {
            BitmapUtils.startCompressImage(context, filepaths.getFirst(), new BitmapUtils.Callback() {
                @Override
                public void onComplete(String path) {
                    onUpload(path);
                }
            });
        } else {
            onUpload(filepaths.getFirst());
        }
    }

    /**
     * 取消上传
     */
    public void onCancel() {
        if (task != null) task.cancel();
    }

    /**
     * 暂停上传
     */
    public void onStop() {
        isUploading = false;
        if (task != null) task.cancel();
    }

    /**
     * 上传图片
     */
    private void onUpload(String filepath) {
        Logger.d("-上传的路径->" + filepath);
        put = new PutObjectRequest(OSS_BUCKET, OSS_FILENAME + "/" + userId + "/" + generateFileName(filepath), filepath);
        // 文件元信息的设置是可选的
        ObjectMetadata metadata = new ObjectMetadata();
//        metadata.setContentType("application/octet-stream"); // 设置content-type
        try {
            metadata.setContentMD5(BinaryUtil.calculateBase64Md5(filepath)); // 校验MD5
        } catch (IOException e) {
            e.printStackTrace();
        }
        put.setMetadata(metadata);

        if (isProgress) {
            //设置上传进度
            put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
                @Override
                public void onProgress(PutObjectRequest putObjectRequest, long progress, long allsize) {
                    if (progress == allsize) {
                        uploadSize += allsize;
                        uploadSize2 = 0;
                    } else
                        uploadSize2 = progress;

                    setUploadState(State.PROGRESS);
                }
            });
        }

        task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {
                Logger.d("oss upload success url:" + putObjectRequest.getObjectKey());
                imgurls.add(putObjectRequest.getObjectKey());
                setUploadState(State.SUCCESS);
            }

            @Override
            public void onFailure(PutObjectRequest putObjectRequest, ClientException e, ServiceException serviceException) {
                setUploadState(State.FAILURE);
                Log.e("ErrorCode", serviceException.getErrorCode());
                Log.e("RequestId", serviceException.getRequestId());
                Log.e("HostId", serviceException.getHostId());
                Log.e("RawMessage", serviceException.getRawMessage());
            }

        });
    }

    /**
     * 循环上次图片
     *
     * @param state 状态
     */
    private void setUploadState(State state) {
        Logger.d("数据大小--》" + filepaths.size());
        switch (state) {
            case CHECK_WAIT_LIST:
                if (!filepaths.isEmpty()) {
                    //从待上传列表中取出一个去上传
                    onUpload(filepaths.getFirst());
                } else {
                    //上传成功，给后台传数据
                    Logger.d("-上传到阿里云的图片个数-->" + imgurls.size());
                    handler.sendEmptyMessage(1);
                    BitmapUtils.deleteFile(context);
                }
                break;
            case PROGRESS:
                //上传进度(单文件)
                handler.sendEmptyMessage(2);
                break;
            case SUCCESS:
                //移出待上传列表
                filepaths.removeFirst();
                //重新检查待上传列表
                setUploadState(State.CHECK_WAIT_LIST);
                break;
            case FAILURE:
                handler.sendEmptyMessage(0);
                break;
            case STOP:
                break;
        }
    }

    private enum State {CHECK_WAIT_LIST, PROGRESS, SUCCESS, FAILURE, STOP}

}
