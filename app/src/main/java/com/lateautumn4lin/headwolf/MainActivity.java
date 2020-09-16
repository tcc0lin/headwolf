package com.lateautumn4lin.headwolf;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lateautumn4lin.headwolf.commons.Logger;
import com.lateautumn4lin.headwolf.utils.PropertiesAssistant;

import java.util.HashMap;
import java.util.List;


/**
 * The type Main activity.
 */
public class MainActivity extends AppCompatActivity {
    /**
     * The Hook package info.
     */
    HashMap<String, String> HookPackageInfo = new HashMap<String, String>();
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("HeadWolf", MODE_PRIVATE);
        Logger.logi("StartUp MainActivity");
        Switch btnUpdate = findViewById(R.id.btn_update);
        Boolean function_1_enable = sharedPreferences.getBoolean("function_1", false);
        btnUpdate.setChecked(function_1_enable);
        btnUpdate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor edt = sharedPreferences.edit();
                if (isChecked) {
                    edt.putBoolean("function_1", true);
                    Toast.makeText(MainActivity.this, "开启", Toast.LENGTH_SHORT).show();
                } else {
                    edt.putBoolean("function_1", false);
                    Toast.makeText(MainActivity.this, "关闭", Toast.LENGTH_SHORT).show();
                }
                edt.apply();
            }
        });
        if (function_1_enable) {
            GetPackages();
        }
    }

    /**
     * Get packages.
     */
    public void GetPackages() {
        HashMap propertiesAssistant = PropertiesAssistant.getJsonProperties();
        for (Object group : propertiesAssistant.keySet()) {
            if (((String) group).contains("group")) {
                JsonObject GroupInfo = (new Gson()).toJsonTree(propertiesAssistant.get(group)).getAsJsonObject();
                String name = GroupInfo.get("name").toString().replace("\"", "");
                String home = GroupInfo.get("home").toString().replace("\"", "");
                HookPackageInfo.put(name, home);
            }
        }
        PackageManager localPackageManager = getPackageManager();
        List localList = localPackageManager.getInstalledPackages(0);
        for (int i = 0; i < localList.size(); i++) {
            PackageInfo localPackageInfo1 = (PackageInfo) localList.get(i);
            String survive_app = localPackageInfo1.packageName.split(":")[0];
            if (((ApplicationInfo.FLAG_SYSTEM & localPackageInfo1.applicationInfo.flags) == 0) && ((ApplicationInfo.FLAG_UPDATED_SYSTEM_APP & localPackageInfo1.applicationInfo.flags) == 0) && ((ApplicationInfo.FLAG_STOPPED & localPackageInfo1.applicationInfo.flags) == 0)) {
                if (HookPackageInfo.keySet().contains(survive_app)) {
                    Logger.loge(survive_app);
                    RestartApp(survive_app);
                }
            }
        }
    }

    /**
     * Restart app.
     *
     * @param package_name the package name
     */
    public void RestartApp(String package_name) {
        try {
            ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
            am.killBackgroundProcesses(package_name);
            Thread.sleep(3000);
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            ComponentName cn = new ComponentName(package_name, HookPackageInfo.get(package_name));
            intent.setComponent(cn);
            startActivity(intent);
        } catch (Exception e) {
            Logger.loge(e.toString());
        }
    }

}