package com.xfht.aliyunoss;

/**
 * 阿里云配置信息
 * Created by wang on 2015/12/25.
 */
class OssKeyInfo {
    //必须配置
    static String OSS_ENDPOINT = "res.tmlvxing.cn";// Endpoint
    static String OSS_BUCKET = "tmlvxing";//Bucket
    static String OSS_STS_SERVER = "https://sts.tmlvxing.cn/sts.php";//STS应用服务器地址
    static String OSS_FILENAME;//上传图片的保存文件夹

    static {
        if (BuildConfig.DEBUG) {
            OSS_FILENAME = "development";//测试
        } else {
            OSS_FILENAME = "production";//正式
        }
    }

}
