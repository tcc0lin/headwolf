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

import com.lateautumn4lin.headwolf.MyApplication;
import com.lateautumn4lin.headwolf.commons.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
    private static String modulePackage = MyApplication.class.getPackage().getName();
    private static List<String> hookPackages = null;
    private final String handleHookClass = RealEntry.class.getName();
    private final String handleHookMethod = "handleLoadPackage";
    private static BlockingQueue blockingQueue = new ArrayBlockingQueue<>(30);
    /**
     * The constant threadPoolExecutor.
     */
    public static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(50, 50, 1, TimeUnit.MINUTES, blockingQueue);

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        try {
//            根据配置文件设置需要hook的包名
            try {
                // 获取context对象
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
                setNeedHookPackage(context);
            } catch (Exception e) {
                Logger.loge(String.format("Set NeedHookPackage Accounding:%s Error", modulePackage));
            }
//            包含配置文件中配置的包名则进入真正Hook逻辑
            if ((hookPackages != null) && (hookPackages.contains(loadPackageParam.processName)) && (!loadPackageParam.processName.equals(modulePackage))) {
                try {
//                    得到目标app的classloader并替换原有classloader
                    XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                            Logger.logi(String.format("Get Needed Hook Package:%s", loadPackageParam.packageName));
                            Context context = (Context) param.args[0];
                            loadPackageParam.classLoader = context.getClassLoader();
//                            反射调用真正hook逻辑类的handleLoadPackage函数
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
//            同样的寻包过程
            File apkFile = findApkFile(context, modulePackageName);
            if (apkFile == null) {
                throw new RuntimeException("Find Apk File Error");
            }
            Logger.logi(String.format("Get Apk File:%s", apkFile.toString()));
//            新建pathclassloader得到真正hook逻辑类，并调用hook方法
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

    /**
     * Sets need hook package.
     *
     * @param context the context
     */
    private void setNeedHookPackage(Context context) {
        ArrayList<String> NeedHookPackage = new ArrayList<String>();
        try {
//            根据context对象获取当前apk路径
            String path = findApkFile(context, modulePackage).toString();
//            简单暴力使用zip来解包获取config文件，之前采用getSource发现加入免重启功能后导致获取原包路径失败，因此换用这种方案
            ZipFile zipFile = new ZipFile(path);
            ZipEntry zipEntry = zipFile.getEntry("assets/config");
//            读流数据转化成arraylist
            InputStream inputStream = zipFile.getInputStream(zipEntry);
            InputStreamReader in = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(in);
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
                if (line.contains(".name")) {
                    String[] new_line = line.split("=");
                    NeedHookPackage.add(new_line[1]);
                }
            }
            hookPackages = NeedHookPackage;
        } catch (Exception e) {
            Logger.loge(e.toString());
        }
    }
}
