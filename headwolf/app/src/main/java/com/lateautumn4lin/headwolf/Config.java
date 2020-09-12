package com.lateautumn4lin.headwolf;
/*
 * Config
 *
 * @author lateautumn4lin
 * @github https://github.com/lateautumn4lin
 * @date 2020/9/10 13:40
 */

import com.lateautumn4lin.headwolf.commons.Logger;
import com.lateautumn4lin.headwolf.handlers.KuaishouHandler;
import com.lateautumn4lin.headwolf.utils.PropertiesAssistant;
import com.virjar.sekiro.api.SekiroRequestHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import static java.util.Arrays.asList;


/**
 * The type Config.
 */
public class Config {
    private static Properties propertiesAssistant = PropertiesAssistant.getProperties();
    private static List<SekiroRequestHandler> handlers = asList(
            (SekiroRequestHandler) new KuaishouHandler()
    );

    private static HashMap<String, SekiroRequestHandler> handlers_map = GenerateHandlersMap();

    private static HashMap<String, SekiroRequestHandler> GenerateHandlersMap() {
        HashMap<String, SekiroRequestHandler> map = new HashMap<String, SekiroRequestHandler>();
        for (SekiroRequestHandler obj : handlers) {
            map.put(obj.getClass().getName(), obj);
        }
        return map;
    }

    private static String client_id = null;
    /**
     * The Groups.
     */
    static private HashMap<String, String> groups = GetAllPackageGroups();

    /**
     * Get package name string.
     *
     * @return the string
     */
    public static String GetPackageName() {
        Properties propertiesAssistant = PropertiesAssistant.getProperties();
        return propertiesAssistant.getProperty("package_name");
    }


    /**
     * Gets remote server.
     *
     * @return the remote server
     */
    public static String getRemoteServer() {
        return propertiesAssistant.getProperty("remote_server");
    }

    /**
     * Gets client id.
     *
     * @return the client id
     */
    public static String getClientId() {
        if (client_id == null) {
            String s = UUID.randomUUID().toString();
            Config.client_id = s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18) + s.substring(19, 23) + s.substring(24);
        }
        return client_id;
    }

    /**
     * Get all package hash map.
     *
     * @return the hash map
     */
    public static HashMap<String, String> GetAllPackageGroups() {
        Properties propertiesAssistant = PropertiesAssistant.getProperties();
        HashMap<String, String> all_package_groups = new HashMap<String, String>();
        try {
            for (String name : propertiesAssistant.stringPropertyNames()) {
                if (name.contains("package_") ^ name.contains("name")) {
                    all_package_groups.put(propertiesAssistant.getProperty(name), name);
                }
            }
        } catch (Exception e) {
            Logger.loge(e.toString());
        }
        return all_package_groups;
    }


    /**
     * Gets group.
     *
     * @param type_name the type name
     * @return the group
     */
    public static String GetGroup(String type_name) {
        return groups.get(type_name);
    }

    /**
     * Get hook packages string.
     *
     * @return the string
     */
    public static List<String> GetHookPackages() {
        Properties propertiesAssistant = PropertiesAssistant.getProperties();
        List<String> inner_hook_packages = new ArrayList<String>();
        try {
            for (String name : propertiesAssistant.stringPropertyNames()) {
                if (name.contains("package_") ^ name.contains("name")) {
                    inner_hook_packages.add(propertiesAssistant.getProperty(name));
                }
            }
        } catch (Exception e) {
            Logger.loge(e.toString());
        }
        return inner_hook_packages;
    }


    /**
     * Get hanler instance list.
     *
     * @param class_name the class name
     * @return the list
     */
    public static SekiroRequestHandler GetHanlerInstance(String class_name) {
        return handlers_map.get(class_name);
    }
}
