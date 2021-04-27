package com.common.database.manage;

import android.content.Context;

import com.common.database.entry.Student;
import com.common.database.greendao.DaoMaster;
import com.common.database.greendao.DaoSession;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


/**
 * 数据库处理
 */
public class DaoManage {
    private static String DB_NAME = "db_user";
    private static DaoManage mInstance;
    private static DaoMaster mDaoMaster;
    private static DaoSession mDaoSession;

    private DaoManage(Context context) {
        if (mInstance == null) {
            DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
            mDaoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
            mDaoSession = mDaoMaster.newSession();
        }
    }

    public static DaoManage getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DaoManage.class) {
                if (mInstance == null) {
                    mInstance = new DaoManage(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 数据查询示例
     */
    public void getData() {
        mDaoSession.getStudentDao().queryBuilder().rx()
                .list()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Student>>() {
                    @Override
                    public void call(List<Student> students) {

                    }
                });
    }
}
