package com.media.imagechoose.model;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.media.imagechoose.impl.IFile;
import com.media.imagechoose.utils.FileUtils;

import java.io.File;

/**
 * 手机上的图片实体
 */
public class FileBean implements Parcelable, IFile {
    private String imagePath;
    private String fileUri;//android 10以上使用
    private long videoTime;//视频时长

    private int width;
    private int height;

    private String type = Type.FILE_TYPE_IMAGE;//文件类型
    private String des = Des.FILE_DES_PHONE_VIDEO;//描述

    private boolean isChoose;
    private boolean isCamera;//是否是相机

    public FileBean(boolean isCamera) {
        this.isCamera = isCamera;
    }

    /**
     * 默认为图片类型
     */
    public FileBean(String imgpath) {
        this(imgpath, Type.FILE_TYPE_IMAGE);
    }

    public FileBean() {
    }

    public FileBean(String imgpath, String type) {
        this.imagePath = imgpath;
        this.type = type;
    }

    /**
     * 获取父类文件名称
     */
    public String getFileParentName() {
        String[] str = imagePath.split("/");
        if (str.length > 1) {
            return str[str.length - 2];
        } else {
            return "";
        }
    }


    protected FileBean(Parcel in) {
        imagePath = in.readString();
        fileUri = in.readString();
        videoTime = in.readLong();
        width = in.readInt();
        height = in.readInt();
        type = in.readString();
        des = in.readString();
        isChoose = in.readByte() != 0;
        isCamera = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imagePath);
        dest.writeString(fileUri);
        dest.writeLong(videoTime);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeString(type);
        dest.writeString(des);
        dest.writeByte((byte) (isChoose ? 1 : 0));
        dest.writeByte((byte) (isCamera ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FileBean> CREATOR = new Creator<FileBean>() {
        @Override
        public FileBean createFromParcel(Parcel in) {
            return new FileBean(in);
        }

        @Override
        public FileBean[] newArray(int size) {
            return new FileBean[size];
        }
    };

    public boolean isCamera() {
        return isCamera;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isChoose() {
        return isChoose;
    }

    public void setIsChoose(boolean isChoose) {
        this.isChoose = isChoose;
    }

    public String getImgpath() {
        return getFilePathQ();
    }

    public String getFileUri() {
        return fileUri;
    }

    /**
     * 获取安卓Q路径
     */
    public String getFilePathQ() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Environment.isExternalStorageLegacy() &&
                !TextUtils.isEmpty(fileUri)) {
            return fileUri;
        } else {
            return imagePath;
        }
    }

    /**
     * 获取要上传的文件路径
     */
    public String getUploadFilePath(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Environment.isExternalStorageLegacy() && !TextUtils.isEmpty(fileUri)) {
            return FileUtils.getUri2CachePath(context, imagePath, fileUri);
        } else {
            return imagePath;
        }
    }

    /**
     * 判断文件是否存在
     */
    public boolean isExists() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Environment.isExternalStorageLegacy() && !TextUtils.isEmpty(fileUri)) {
            return true;
        } else {
            File file = new File(imagePath);
            return file.exists();
        }
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    public FileBean(String imgpath, String type, String des) {
        this.imagePath = imgpath;
        this.type = type;
        this.des = des;
    }

    @Override
    public boolean equals(Object o) {//重写判断是否是同一个数据的标准
        String oPath = "";
        if (o instanceof String) {
            oPath = (String) o;
        } else if (o instanceof FileBean) {
            FileBean oPicture = (FileBean) o;
            oPath = oPicture.imagePath;
        }
        return oPath.equals(imagePath);
    }

    public long getVideoTime() {
        return videoTime;
    }

    public void setVideoTime(long videoTime) {
        this.videoTime = videoTime;
    }

    public String getDes() {
        return des;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "FileBean{" +
                "imagePath='" + imagePath + '\'' +
                ", fileUri='" + fileUri + '\'' +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
