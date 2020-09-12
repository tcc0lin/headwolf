package com.lateautumn4lin.headwolf.utils;
/*
 * ClassesReaderAssistant
 *
 * @author lateautumn4lin
 * @github https://github.com/lateautumn4lin
 * @date 2020/9/11 11:03
 */


import android.content.Context;


import com.lateautumn4lin.headwolf.Config;
import com.lateautumn4lin.headwolf.commons.Logger;
import com.virjar.sekiro.api.SekiroRequestHandler;

import java.io.File;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;
import de.robv.android.xposed.XposedBridge;


/**
 * The type Classes reader assistant.
 */
public final class ClassesReaderAssistant {


    /**
     * Application dex file set.
     *
     * @param packageCodePath the package code path
     * @return the set
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
     * Create dex file dex file.
     *
     * @param path the path
     * @return the dex file
     */
    public static DexFile createDexFile(String path) {
        try {
            return new DexFile(path);
        } catch (IOException e) {
            return null;
        }
    }


    /**
     * Reader hash map.
     *
     * @param context       the context
     * @param associateName the associate name
     * @return the hash map
     */
    public static HashMap<String, SekiroRequestHandler> reader(Context context, String associateName) {
        String packageName = String.format("%s.handlers", Config.GetPackageName());
        HashMap<String, SekiroRequestHandler> associate_handlers = new HashMap<String, SekiroRequestHandler>();
        try {
            Context moudleContext = context.createPackageContext(Config.GetPackageName(), Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
            List<Class<?>> associate_classes = reader(packageName, moudleContext.getPackageCodePath());
            for (Class<?> cls : associate_classes)
                try {
                    Logger.logi(String.format("%s,get", cls.getName()));
                    if (cls.getName().equals(String.format("%s.BaseHandler", packageName))) {
                        continue;
                    }
                    Constructor constructor = cls.getConstructor();
                    Object instance = constructor.newInstance();
                    Method getBelong = cls.getDeclaredMethod("getBelong");
                    Object belong_name = getBelong.invoke(instance);
                    Logger.logi(String.format("%s Compare %s", belong_name, associateName));
                    if (((String) belong_name).equals(associateName)) {
                        Method getAction = cls.getDeclaredMethod("getAction");
                        Object action = getAction.invoke(instance);
                        associate_handlers.put((String) action, Config.GetHanlerInstance(cls.getName()));
                    }
                } catch (NoSuchMethodException e) {
                    Logger.loge(String.format("Can't Find Method:getBelong For %s", packageName));
                } catch (Exception e) {
                    Logger.loge(e.toString());
                }
        } catch (Exception e) {
            Logger.loge(e.toString());
        }
        return associate_handlers;
    }


    /**
     * Reader list.
     *
     * @param packageName     the package name
     * @param packageCodePath the package code path
     * @return the list
     */
    public static List<Class<?>> reader(String packageName, String packageCodePath) {
        List<Class<?>> classes = new ArrayList<>();
        Set<DexFile> dexFiles = applicationDexFile(packageCodePath);
        for (DexFile dexFile : dexFiles) {
            Logger.logi(dexFile.toString());
            try {
                PathClassLoader pathClassLoader = new PathClassLoader(new File(packageCodePath).getAbsolutePath(), XposedBridge.BOOTCLASSLOADER);
                if (dexFile == null) continue;
                Enumeration<String> entries = dexFile.entries();
                while (entries.hasMoreElements()) {
                    try {
                        String currentClassPath = entries.nextElement();
                        if (currentClassPath == null || currentClassPath.isEmpty() || currentClassPath.indexOf(packageName) != 0)
                            continue;
                        Class<?> entryClass = Class.forName(currentClassPath, true, pathClassLoader);
                        if (entryClass == null) continue;
                        classes.add(entryClass);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                Logger.loge(e.toString());
            }
        }
        return classes;
    }

}


