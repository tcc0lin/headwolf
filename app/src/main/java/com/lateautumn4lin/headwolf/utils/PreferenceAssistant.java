package com.lateautumn4lin.headwolf.utils;
/*
 * PreferenceAssistant
 *
 * @author lateautumn4lin
 * @github https://github.com/lateautumn4lin
 * @date 2020/9/17 15:24
 */


import java.io.File;

import de.robv.android.xposed.XSharedPreferences;

/**
 * The type Preference assistant.
 */
public class PreferenceAssistant {
    private static XSharedPreferences xSharedPreferences = null;

    /**
     * Init pref.
     */
    public static void initPref() {
        xSharedPreferences = new XSharedPreferences(new File("/data/user_de/0/com.lateautumn4lin.headwolf/shared_prefs/HeadWolf.xml"));
        xSharedPreferences.makeWorldReadable();
    }

    /**
     * Gets pref.
     *
     * @return the pref
     */
    public static XSharedPreferences getPref(String name) {
        return xSharedPreferences;
    }

    /**
     * Reload pref.
     */
    public static void reloadPref() {
        xSharedPreferences.reload();
        xSharedPreferences.makeWorldReadable();
    }
}
