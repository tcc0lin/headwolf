package com.lateautumn4lin.headwolf.handlers;
/*
 * KuaishouHandler
 *
 * @author lateautumn4lin
 * @github https://github.com/lateautumn4lin
 * @date 2020/9/10 15:39
 */

import com.google.gson.Gson;
import com.lateautumn4lin.headwolf.commons.Response;
import com.lateautumn4lin.headwolf.entry.BaseEntry;
import com.virjar.sekiro.api.SekiroRequest;
import com.virjar.sekiro.api.SekiroRequestHandler;
import com.virjar.sekiro.api.SekiroResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Kuaishou handler.
 */
public class KuaishouHandler extends BaseHandler implements SekiroRequestHandler {
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
    public void handleRequest(SekiroRequest sekiroRequest, final SekiroResponse sekiroResponse) {
        final Gson gson = new Gson();
        final Map<String, String> object = new HashMap<String, String>();
        String name = sekiroRequest.getString("name");
        object.put("name", name);
//        测试三种请求返回方式  测试方案 1000/s 总请求 50*1000
//        P1:阻塞返回
//        sekiroResponse.send(gson.toJson(object));
//        P2:新建线程返回 70/s，响应1分钟开外，延迟过高
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                sekiroResponse.send(gson.toJson(object));
//            }
//        });
//        P3:线程池返回 1000/s，响应1s左右
        Runnable task = new Response(gson.toJson(object), sekiroResponse);
        BaseEntry.threadPoolExecutor.submit(task);
    }
}
