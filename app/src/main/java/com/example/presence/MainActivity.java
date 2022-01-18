package com.example.presence;

import androidx.appcompat.app.AppCompatActivity;


import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import android.telephony.TelephonyManager;
import android.util.Log;
import android.provider.Settings;
import android.provider.Settings.System;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Personne personne;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String androidID = System.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
      //  final BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        //manager.getAdapter().getAddress();

        Log.d("startdeux", androidID);
       // Log.d("startTrois", manager.getAdapter().getAddress());
        //Log.d("startquatre", manager.getAdapter().getName());
       personne = new Personne();
       personne.setIdEtudiant(4567899);

       personne.setIdBluetooth(12346);
        personne.print();
        Date today = new Date();
        Log.d("date", String.valueOf(today));
        String date = String.valueOf(today);
        personne.getConnexion().add(date);


    }


    public void start(android.view.View v) {


        Log.d("start","you are in start");



        Intent lecture = new Intent(this, etudiantControl.class);
        lecture.putExtra("FromNumToStarting", this.personne);
        startActivity(lecture);
    }

}