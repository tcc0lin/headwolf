package com.lateautumn4lin.headwolf.utils;
/*
 * ClassesReaderAssistant
 *
 * @author lateautumn4lin
 * @github https://github.com/lateautumn4lin
 * @date 2020/9/11 11:03
 */


import android.content.Context;


import java.io.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dalvik.system.DexFile;

/**
 * 类读取器<br/>
 * 读取某个包下所有类<br/>
 */
public final class ClassesReaderAssistant {

    /**
     * 获取应用程序下的所有Dex文件
     *
     * @param context 上下文
     * @return Set<DexFile> set
     */
    public static Set<DexFile> applicationDexFile(Context context) {
        return applicationDexFile(context.getPackageCodePath());
    }

    /**
     * 获取应用程序下的所有Dex文件
     *
     * @param packageCodePath 包路径
     * @return Set<DexFile> set
     */
    public static Set<DexFile> applicationDexFile(String packageCodePath) {
        Set<DexFile> dexFiles = new HashSet<>();
        File dir = new File(packageCodePath).getParentFile();
        File[] files = dir.listFiles();
        for (File file : files) {
            try {
                String absolutePath = file.getAbsolutePath();
                if (!absolutePath.contains(".")) continue;
                String suffix = absolutePath.substring(absolutePath.lastIndexOf("."));
                if (!suffix.equals(".apk")) continue;
                DexFile dexFile = createDexFile(file.getAbsolutePath());
                if (dexFile == null) continue;
                dexFiles.add(dexFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dexFiles;
    }

    /**
     * 创建DexFile文件
     *
     * @param path 路径
     * @return DexFile dex file
     */
    public static DexFile createDexFile(String path) {
        try {
            return new DexFile(path);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 读取类路径下的所有类
     *
     * @param packageName 包名
     * @param context     包路径
     * @return List<Class> list
     */
    public static List<Class<?>> reader(String packageName, Context context) {
        return reader(packageName, context.getPackageCodePath());
    }

    /**
     * 读取类路径下的所有类
     *
     * @param packageName     包名
     * @param packageCodePath 上下文
     * @return List<Class> list
     */
    public static List<Class<?>> reader(String packageName, String packageCodePath) {
        List<Class<?>> classes = new ArrayList<>();
        Set<DexFile> dexFiles = applicationDexFile(packageCodePath);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        for (DexFile dexFile : dexFiles) {
            if (dexFile == null) continue;
            Enumeration<String> entries = dexFile.entries();
            while (entries.hasMoreElements()) {
                try {
                    String currentClassPath = entries.nextElement();
                    if (currentClassPath == null || currentClassPath.isEmpty() || currentClassPath.indexOf(packageName) != 0)
                        continue;
                    Class<?> entryClass = Class.forName(currentClassPath, true, classLoader);
                    if (entryClass == null) continue;
                    classes.add(entryClass);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return classes;
    }

}


