package com.lateautumn4lin.headwolf.initialization;
/*
 * Register
 *
 * @author lateautumn4lin
 * @github https://github.com/lateautumn4lin
 * @date 2020/9/10 13:49
 */

import android.os.Bundle;

import com.lateautumn4lin.headwolf.Config;
import com.lateautumn4lin.headwolf.commons.Logger;
import com.virjar.sekiro.api.SekiroClient;
import com.virjar.sekiro.api.SekiroRequestHandler;

import java.util.HashMap;
import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * The type Register.
 */
public class Register {
    /**
     * Base register boolean.
     *
     * @param loadPackageParam   the load package param
     * @param associate_handlers the associate handlers
     * @return the boolean
     */
    public static Boolean GroupRegister(final XC_LoadPackage.LoadPackageParam loadPackageParam, final HashMap<String, SekiroRequestHandler> associate_handlers) {
        try {
            Class HomeClass = loadPackageParam.classLoader.loadClass(Config.GetHome(loadPackageParam.packageName));
            final String group_name = Config.GetGroup(loadPackageParam.packageName);
            XposedHelpers.findAndHookMethod(HomeClass, "onCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Logger.logi(String.format("Group Register:%s Begin ClientId:%s", group_name, Config.getClientId()));
                    final SekiroClient sekiroClient = SekiroClient.start(
                            Config.getRemoteServer(),
                            Config.getClientId(),
                            group_name
                    );
                    try {
                        for (Map.Entry<String, SekiroRequestHandler> entry : associate_handlers.entrySet()) {
                            try {
                                SekiroRequestHandler handler = entry.getValue();
                                sekiroClient.registerHandler(entry.getKey(), handler);
                                Logger.logi(String.format("Group Register Group:%s Action:%s Handler:%s Success ClientId:%s", group_name, entry.getKey(), handler, Config.getClientId()));
                            } catch (Exception e) {
                                Logger.loge(e.toString());
                            }

                        }
                    } catch (Exception e) {
                        Logger.loge(e.toString());
                    }
                    super.afterHookedMethod(param);
                }
            });
        } catch (Exception e) {
            Logger.loge(String.format("Group Register Error ClientId:%s", Config.getClientId()));
            return false;
        }
        return true;
    }
}
