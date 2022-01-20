package com.example.presence;

import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.IOException;


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
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Personne personne;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
       personne = new Personne();

        if(personne.getIdEtudiant()!=0){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_etudiant_control);
        }
        else {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            String androidID = System.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
           //  final BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
           // manager.getAdapter().getAddress();
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            Log.d("bluetooth adaptateur",adapter.getAddress());

            String id = android.provider.Settings.System.getString(super.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            Log.d("UDID-------------------------",id);
            Log.d("startdeux", androidID);
           //  Log.d("startTrois", manager.getAdapter().getAddress());
            //Log.d("startquatre", manager.getAdapter().getName());
            // personne = new Personne();
            //personne.setIdEtudiant(4567899);

            personne.setIdBluetooth(12346);
            Date today = new Date();
            Log.d("date", String.valueOf(today));
            String date = String.valueOf(today);
            personne.getConnexion().add(date);
            personne.print();

        }


    }


    public void start(View v) {


        Log.d("start","you are in start");

        EditText number = (EditText) findViewById(R.id.editTextNumber);

        Context context = getApplicationContext();
        CharSequence textToast = "You need to enter a student id";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, textToast, duration);
        if (!number.getText().toString().equals("")) {
            Integer n = Integer.parseInt(number.getText().toString());
            personne.setIdEtudiant(n);
            Intent lecture = new Intent(this, etudiantControl.class);
            lecture.putExtra("FromNumToStarting", this.personne);
            startActivity(lecture);

            // Creation du fichier certificat aliant num etu et id unique tel.

            try {
                File ObjCertif = new File("gphyPresence/app/build/generated/" +
                        "ap_generated_sources/debug/outapp/build/generated/source/buildConfig/" +
                        "debugdebugEtuCertificat.txt");
                if (ObjCertif.createNewFile()) {
                    Log.d("File created: " , ObjCertif.getName());
                } else {
                    Log.d("le fichier existe deja","File already exists.");
                }
            } catch (IOException e) {
                Log.d("Un probleme est survenu","An error occurred.");
                e.printStackTrace();
            }



        } else {
            toast.show();
        }

    }

}