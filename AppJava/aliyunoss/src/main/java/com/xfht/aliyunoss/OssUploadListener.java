package com.xfht.aliyunoss;

import java.util.LinkedList;

/**
 * 文件上传回调
 * Created by wang on 2015/12/25.
 */
public interface OssUploadListener {
    /**
     * 上传成功
     *
     * @param osspaths 文件路径
     */
    void onSuccess(LinkedList<String> osspaths);

    /**
     * 上传中的进度
     *
     * @param allsize  总大小
     * @param progress 当前的进度
     */
    void onProgress(long allsize, long progress);

    /**
     * 上传失败
     */
    void onFailure();
}
