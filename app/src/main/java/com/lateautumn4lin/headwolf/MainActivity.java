package com.lateautumn4lin.headwolf;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lateautumn4lin.headwolf.commons.Logger;
import com.lateautumn4lin.headwolf.utils.PropertiesAssistant;

import java.io.File;
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getNormalSharedPreferences();
        setWorldReadable();
        Logger.logi("StartUp MainActivity");
        Switch btnUpdate = findViewById(R.id.btn_update);
        EditText contentChange = findViewById(R.id.content_change);
        Boolean function_1_enable = sharedPreferences.getBoolean("function_1", false);
        btnUpdate.setChecked(function_1_enable);
        btnUpdate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor edt = sharedPreferences.edit();
                if (isChecked) {
                    edt.putBoolean("function_1", true).apply();
                    setWorldReadable();
                    Toast.makeText(MainActivity.this, "开启", Toast.LENGTH_SHORT).show();
                } else {
                    edt.putBoolean("function_1", false).apply();
                    setWorldReadable();
                    Toast.makeText(MainActivity.this, "关闭", Toast.LENGTH_SHORT).show();
                }
                edt.apply();
            }
        });
        contentChange.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SharedPreferences.Editor edt = sharedPreferences.edit();
                edt.putString("function_name", contentChange.getText().toString()).apply();
                setWorldReadable();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        if (function_1_enable) {
            GetPackages();
        }
    }


    @SuppressLint({"SetWorldReadable", "SetWorldWritable"})
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setWorldReadable() {
        try {
            File f = new File("/data/user_de/0/com.lateautumn4lin.headwolf/shared_prefs/HeadWolf.xml");
            f.setReadable(true, false);
            f.setExecutable(true, false);
            f.setWritable(true, false);
        } catch (Exception e) {
            Logger.loge(e.toString());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public SharedPreferences getNormalSharedPreferences() {
        return this.createDeviceProtectedStorageContext().getSharedPreferences("HeadWolf", MODE_PRIVATE);
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