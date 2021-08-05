package com.common.base.network.user;

import android.content.Context;
import android.content.SharedPreferences;

public class User {
    private static final String DATA = "data_user";

    private volatile static User instance;
    private SharedPreferences sharedPreferences;

    public User(Context context) {
        sharedPreferences = context.getSharedPreferences(DATA, Context.MODE_PRIVATE);
    }

    public static User getInstance(Context context) {
        if (instance == null) {
            synchronized (User.class) {
                if (null == instance) {
                    instance = new User(context);
                }
            }
        }
        return instance;
    }


    /**
     *
     */
    public void logout() {
        sharedPreferences.edit().clear().apply();
    }
}
