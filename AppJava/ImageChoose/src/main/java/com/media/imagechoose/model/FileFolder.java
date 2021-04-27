package com.media.imagechoose.model;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 图片路径
 */
public class FileFolder {
    public List<FileBean> images;
    /**
     * 第一张图片的路径
     */
    private String firstImagePath;
    /**
     * 文件夹的名称
     */
    private String name;

    public FileFolder() {
        images = new ArrayList<>();
    }

    public String getFirstImagePath() {
        return firstImagePath;
    }

    public void setFirstImagePath(String firstImagePath) {
        this.firstImagePath = firstImagePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 设置已经选中的数据
     */
    public List<FileBean> getImages(List<FileBean> chooseData) {
        if (chooseData == null || chooseData.isEmpty()) return images;
        for (FileBean choose : chooseData) {
            for (FileBean img : images) {
                if (TextUtils.equals(choose.getImgpath(), img.getImgpath())) {
                    img.setIsChoose(true);
                }
            }
        }
        return images;
    }
}
