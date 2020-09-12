package com.lateautumn4lin.headwolf.entry;
/*
 * RealEntry
 *
 * @author lateautumn4lin
 * @github https://github.com/lateautumn4lin
 * @date 2020/9/10 13:45
 */

import android.content.Context;

import com.lateautumn4lin.headwolf.commons.Logger;
import com.lateautumn4lin.headwolf.initialization.Register;
import com.lateautumn4lin.headwolf.utils.ClassesReaderAssistant;
import com.virjar.sekiro.api.SekiroRequestHandler;

import java.util.HashMap;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * The type Real entry.
 */
public class RealEntry implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        try {
            Logger.logi(String.format("Begin Real Hook Logic About:%s", loadPackageParam.packageName));
//            step1:获取context
            Context context = (Context) XposedHelpers.callMethod(
                    XposedHelpers.callStaticMethod(
                            XposedHelpers.findClass(
                                    "android.app.ActivityThread",
                                    loadPackageParam.classLoader
                            ),
                            "currentActivityThread"
                    ),
                    "getSystemContext"
            );
//            step2:获取包名对应的需要注册的handler以及home_class
            Class HomeClass = loadPackageParam.classLoader.loadClass("com.yxcorp.gifshow.HomeActivity");
            HashMap<String, SekiroRequestHandler> associate_handlers = ClassesReaderAssistant.reader(context, loadPackageParam.packageName);
//            step3:由注册类进行handler注册
            Register.GroupRegister(loadPackageParam, HomeClass, associate_handlers);
        } catch (Exception e) {
            Logger.loge(e.toString());
        }
    }
}
