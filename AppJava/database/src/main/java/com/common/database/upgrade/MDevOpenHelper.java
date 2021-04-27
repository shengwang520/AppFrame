package com.common.database.upgrade;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.common.database.greendao.DaoMaster;

import org.greenrobot.greendao.database.Database;

/**
 * 数据库升级
 */
public class MDevOpenHelper extends DaoMaster.DevOpenHelper {
    public MDevOpenHelper(Context context, String name) {
        super(context, name);
    }

    public MDevOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
    }
}
