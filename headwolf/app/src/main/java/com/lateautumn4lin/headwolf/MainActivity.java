package com.lateautumn4lin.headwolf;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.lateautumn4lin.headwolf.commons.Logger;
import com.tencent.mmkv.MMKV;


/**
 * The type Main activity.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Logger.logi("StartUp MainActivity");
//        try {
//            String rootDir = MMKV.initialize("/data/user/0/com.lateautumn4lin.headwolf/files/mmkv");
//            MMKV kv = MMKV.mmkvWithID("test", MMKV.MULTI_PROCESS_MODE);
//            Logger.logi("mmkv root: " + rootDir);
//            kv.encode("string", "Hello from mmkv");
//            String str = kv.decodeString("string");
//            Logger.logi("Get MMKV:" + str);
//        } catch (Exception e) {
//            Logger.loge(e.toString());
//        }
    }
}