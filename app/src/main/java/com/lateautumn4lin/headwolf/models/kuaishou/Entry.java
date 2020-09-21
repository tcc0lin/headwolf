package com.lateautumn4lin.headwolf.models.kuaishou;
/*
 * Entry
 *
 * @author lateautumn4lin
 * @github https://github.com/lateautumn4lin
 * @date 2020/9/21 15:01
 */

import com.lateautumn4lin.headwolf.commons.Logger;

public class Entry {
    public static ClassLoader classLoader = null;

    public static void init(ClassLoader classloader) {
        classLoader = classloader;
        Logger.logi(classloader.toString());
    }
}
