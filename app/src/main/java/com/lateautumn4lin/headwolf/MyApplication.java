package com.lateautumn4lin.headwolf;
/*
 * MyApplication
 *
 * @author lateautumn4lin
 * @github https://github.com/lateautumn4lin
 * @date 2020/9/12 3:12
 */


import android.app.Application;
import android.content.Context;

import com.lateautumn4lin.headwolf.commons.Logger;

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Logger.logi(String.format("StartUp MyApplication And Get Context %s", context));
    }

    public static Context getContext() {
        return context;
    }
}