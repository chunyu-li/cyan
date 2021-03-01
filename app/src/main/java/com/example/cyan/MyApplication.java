package com.example.cyan;

import android.app.Application;
import android.content.Context;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.example.cyan.object.User;

import cn.bmob.v3.Bmob;

/**
 * @author Chunyu Li
 * @File: MyApplication.java
 * @Package com.example.cyan
 * @date 12/12/20 8:34 PM
 * @Description: Use MyApplication to initialize Bmob SDK, Baidu SDK and stores global variable
 */

public class MyApplication extends Application {

    private static Context context;
    private static MyApplication instance;
    private static User user;
    private static String shareUrl = "";

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        instance = this;
        Bmob.initialize(this, "c61e0362a80da62fd6fb1ddd9748d91f");
        SDKInitializer.initialize(this);
        SDKInitializer.setCoordType(CoordType.BD09LL);
    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        MyApplication.context = context;
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public static void setInstance(MyApplication instance) {
        MyApplication.instance = instance;
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        MyApplication.user = user;
    }

    public static String getShareUrl() {
        return shareUrl;
    }

    public static void setShareUrl(String shareUrl) {
        MyApplication.shareUrl = shareUrl;
    }
}
