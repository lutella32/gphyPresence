package com.example.presence;

import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.telephony.TelephonyManager;
import android.util.Log;
import android.provider.Settings;
import android.provider.Settings.System;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String androidID = System.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        final BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        manager.getAdapter().getAddress();
        Log.d("startdeux", androidID);
        Log.d("startTrois", manager.getAdapter().getAddress());

    }

    public void start(android.view.View v) {


        Log.d("start","you are in start");



        Intent lecture = new Intent(this, etudiantControl.class);

        startActivity(lecture);
    }

}