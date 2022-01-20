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
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Personne personne;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // instancier new Personne
       personne = new Personne();
       // on met des données au pif dans personne, et la date du jour en String / test get et set
        personne.setIdBluetooth(12346);
        Date today = new Date();
        Log.d("date", String.valueOf(today));
        String date = String.valueOf(today);
        personne.getConnexion().add(date);

        // on affiche personne
        personne.print();
        // à faire
       // code a rajouter pour le fichier, si il existe un fichier on récupère les données et si non, on crée fichier

        // si la personne c'est déjà enregistrer et on va sur la page etudiantControl
        if(personne.getIdEtudiant()!=0){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_etudiant_control);
        }
        else {
            // sinon on reste sur la page main
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);


        }


    }


    public void start(android.view.View v) {
        // si on clique sur accepter dans le main

        Log.d("start","you are in start");

        // récupération de ce qu'à écrit l'étudiant
        EditText number = (EditText) findViewById(R.id.editTextNumber);

        // création de ce qu'il faut pour le toast ( message erreur, si pas idetudiant remplis)
        Context context = getApplicationContext();
        CharSequence textToast = "You need to enter a student id";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, textToast, duration);

        if (!number.getText().toString().equals("")) {

            // convertir idEtudiant en int pour stocker dans personne
            Integer n = Integer.parseInt(number.getText().toString());
            personne.setIdEtudiant(n);

            // on passe à la page etudiantControl et on transmet personne avec l'idetudiant enregistré
            Intent lecture = new Intent(this, etudiantControl.class);
            lecture.putExtra("FromNumToStarting", this.personne);
            startActivity(lecture);

        } else {
            // message si idetudiant pas remplis
            toast.show();
        }

    }
    // code d'essais bluetooth et android id à supprimer à la fin
    //  final BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
    // manager.getAdapter().getAddress();
    //BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    // Log.d("bluetooth adaptateur",adapter.getAddress());
   // String androidID = System.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
    //String id = android.provider.Settings.System.getString(super.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);




}