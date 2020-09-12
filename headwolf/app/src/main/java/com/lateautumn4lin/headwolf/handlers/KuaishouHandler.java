package com.lateautumn4lin.headwolf.handlers;
/*
 * KuaishouHandler
 *
 * @author lateautumn4lin
 * @github https://github.com/lateautumn4lin
 * @date 2020/9/10 15:39
 */

import com.google.gson.Gson;
import com.virjar.sekiro.api.SekiroRequest;
import com.virjar.sekiro.api.SekiroRequestHandler;
import com.virjar.sekiro.api.SekiroResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Kuaishou handler.
 */
public class KuaishouHandler implements SekiroRequestHandler {
    /**
     * Gets belong.
     *
     * @return the belong
     */
    public String getBelong() {
        return "com.smile.gifmaker";
    }

    /**
     * Gets action.
     *
     * @return the action
     */
    public String getAction() {
        return "sign";
    }


    @Override
    public void handleRequest(SekiroRequest sekiroRequest, SekiroResponse sekiroResponse) {
        Gson gson = new Gson();
        Map<String, String> object = new HashMap<String, String>();
        String name = sekiroRequest.getString("name");
        object.put("name", name);
        sekiroResponse.send(gson.toJson(object));
    }
}
