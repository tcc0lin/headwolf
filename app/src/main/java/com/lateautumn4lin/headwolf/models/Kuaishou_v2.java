package com.lateautumn4lin.headwolf.models;
/*
 * Kuaishou
 *
 * @author lateautumn4lin
 * @github https://github.com/lateautumn4lin
 * @date 2020/9/21 14:07
 */

import com.lateautumn4lin.headwolf.commons.Logger;

/**
 * The type Kuaishou v 2.
 */
public class Kuaishou_v2 {
    /**
     * The constant classLoader.
     */
    public static ClassLoader classLoader = null;

    /**
     * Init.
     *
     * @param classloader the classloader
     */
    public static void init(ClassLoader classloader) {
        classLoader = classloader;
        Logger.logi(classloader.toString());
    }
}
