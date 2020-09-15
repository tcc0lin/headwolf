package com.lateautumn4lin.headwolf.commons;
/*
 * Response
 *
 * @author lateautumn4lin
 * @github https://github.com/lateautumn4lin
 * @date 2020/9/14 18:16
 */

import com.virjar.sekiro.api.SekiroResponse;

public class Response implements Runnable {
    private String data;
    private SekiroResponse sekiroResponse;

    public Response(String Data, SekiroResponse sekiroResponse) {
        this.data = Data;
        this.sekiroResponse = sekiroResponse;
    }

    @Override
    public void run() {
        sekiroResponse.send(data);
    }
}
