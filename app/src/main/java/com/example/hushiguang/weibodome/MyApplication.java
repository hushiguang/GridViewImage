package com.example.hushiguang.weibodome;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;
import com.socks.library.KLog;

import retrofit.Retrofit;

/**
 * Created by HuShiGuang on 2015/12/28.
 */
public class MyApplication extends Application {


    public static MyApplication application;
    public static Context mContext;
    Retrofit retrofit;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        mContext = this;

        KLog.init(BuildConfig.LOG_DEBUG);
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }



    /***
     * 获取实例
     *
     * @return
     */
    public synchronized static MyApplication getInstance() {
        if (application == null)
            return new MyApplication();
        else
            return application;

    }



}
