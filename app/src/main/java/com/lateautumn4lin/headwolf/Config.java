package com.lateautumn4lin.headwolf;
/*
 * Config
 *
 * @author lateautumn4lin
 * @github https://github.com/lateautumn4lin
 * @date 2020/9/10 13:40
 */

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lateautumn4lin.headwolf.commons.Logger;
import com.lateautumn4lin.headwolf.handlers.KuaishouHandler;
import com.lateautumn4lin.headwolf.utils.PropertiesAssistant;
import com.virjar.sekiro.api.SekiroRequestHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;


/**
 * The type Config.
 */
public class Config {
    private static HashMap propertiesAssistant = PropertiesAssistant.getJsonProperties();
    private static String client_id = null;
    /**
     * The Groups.
     */
    static private HashMap<String, HashMap<String, String>> groups = GetAllPackageGroups();

    /**
     * Get package name string.
     *
     * @return the string
     */
    public static String GetPackageName() {
        return propertiesAssistant.get("package_name").toString();
    }


    /**
     * Gets remote server.
     *
     * @return the remote server
     */
    public static String getRemoteServer() {
        return propertiesAssistant.get("remote_server").toString();
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
    public static HashMap<String, HashMap<String, String>> GetAllPackageGroups() {
        HashMap<String, HashMap<String, String>> all_package_groups = new HashMap<String, HashMap<String, String>>();
        try {
            for (Object group : propertiesAssistant.keySet()) {
                if (((String) group).contains("group")) {
                    HashMap<String, String> same_group = new HashMap<String, String>();
                    JsonObject GroupInfo = (new Gson()).toJsonTree(propertiesAssistant.get(group)).getAsJsonObject();
                    String name = GroupInfo.get("name").toString().replace("\"", "");
                    String home = GroupInfo.get("home").toString().replace("\"", "");
                    same_group.put("home", home);
                    same_group.put("group", group.toString());
                    all_package_groups.put(name, same_group);
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
        return groups.get(type_name).get("group");
    }

    /**
     * Get home string.
     *
     * @param type_name the type name
     * @return the string
     */
    public static String GetHome(String type_name) {
        return groups.get(type_name).get("home");
    }

    /**
     * Get hook packages string.
     *
     * @return the string
     */
    public static List<String> GetHookPackages() {
        List<String> inner_hook_packages = new ArrayList<String>();
        try {
            for (String name : groups.keySet()) {
                inner_hook_packages.add(name);
            }
        } catch (Exception e) {
            Logger.loge(e.toString());
        }
        return inner_hook_packages;
    }
}
