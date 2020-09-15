package com.lateautumn4lin.headwolf.entry;
/*
 * BaseEntry
 *
 * @author lateautumn4lin
 * @github https://github.com/lateautumn4lin
 * @date 2020/9/10 13:45
 */

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;

import com.lateautumn4lin.headwolf.commons.Logger;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import dalvik.system.PathClassLoader;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * The type Base entry. 第一层Hook入口
 */
public class BaseEntry implements IXposedHookLoadPackage {
    private String modulePackage = "com.lateautumn4lin.headwolf";
    private static List<String> hookPackages = new ArrayList<String>();
    private final String handleHookClass = RealEntry.class.getName();
    private final String handleHookMethod = "handleLoadPackage";
    private static BlockingQueue blockingQueue = new ArrayBlockingQueue<>(30);
    public static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(50, 50, 1, TimeUnit.MINUTES, blockingQueue);

    static {
        hookPackages.add("com.smile.gifmaker");
    }

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        try {
//            包含配置文件中配置的包名则进入真正Hook逻辑
            if (hookPackages.contains(loadPackageParam.packageName)) {
                try {
                    XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                            Logger.logi(String.format("Get Needed Hook Package:%s", loadPackageParam.packageName));
                            Context context = (Context) param.args[0];
                            loadPackageParam.classLoader = context.getClassLoader();
                            invokeHandleHookMethod(context, modulePackage, handleHookClass, handleHookMethod, loadPackageParam);
                        }
                    });
                } catch (Exception e) {
                    Logger.loge(String.format("Invoke Hook Method Error, Package:%s", loadPackageParam.packageName));
                }
            }
        } catch (Exception e) {
            Logger.loge(e.toString());
        }
    }

    /**
     * Invoke handle hook method.
     *
     * @param context           the context
     * @param modulePackageName the module package name
     * @param handleHookClass   the handle hook class
     * @param handleHookMethod  the handle hook method
     * @param loadPackageParam  the load package param
     * @throws Throwable the throwable
     */
    private void invokeHandleHookMethod(Context context, String modulePackageName, String handleHookClass, String handleHookMethod, XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        try {
            File apkFile = findApkFile(context, modulePackageName);
            if (apkFile == null) {
                throw new RuntimeException("Find Apk File Error");
            }
            Logger.logi(String.format("Get Apk File:%s", apkFile.toString()));
            PathClassLoader pathClassLoader = new PathClassLoader(apkFile.getAbsolutePath(), XposedBridge.BOOTCLASSLOADER);
            Class<?> cls = Class.forName(handleHookClass, true, pathClassLoader);
            Logger.logi(String.format("Get ClassLoader:%s", apkFile.toString()));
            Object instance = cls.newInstance();
            Method method = cls.getDeclaredMethod(handleHookMethod, XC_LoadPackage.LoadPackageParam.class);
            Logger.logi(String.format("Get Needed Hook Method:%s", apkFile.toString()));
            method.invoke(instance, loadPackageParam);
        } catch (Exception e) {
            Logger.loge(e.toString());
        }
    }

    /**
     * Find apk file file. 获取需要Hook Apk文件地址
     *
     * @param context           the context
     * @param modulePackageName the module package name
     * @return the file
     */
    private File findApkFile(Context context, String modulePackageName) {
        if (context == null) {
            throw new RuntimeException("Can't Get Context");
        }
        try {
            Context moudleContext = context.createPackageContext(modulePackageName, Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
            String apkPath = moudleContext.getPackageCodePath();
            return new File(apkPath);
        } catch (PackageManager.NameNotFoundException e) {
            Logger.loge(String.format("Find File Error，Package:%s", modulePackageName));
            return null;
        }
    }
}
