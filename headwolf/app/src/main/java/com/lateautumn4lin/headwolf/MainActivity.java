package com.lateautumn4lin.headwolf;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.lateautumn4lin.headwolf.commons.Logger;


/**
 * The type Main activity.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Logger.logi("StartUp MainActivity");
    }
}