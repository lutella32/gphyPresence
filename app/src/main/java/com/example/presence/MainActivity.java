package com.example.presence;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
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

import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.provider.Settings;
import android.provider.Settings.System;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.io.FileWriter;
import java.util.Scanner;


public class MainActivity extends AppCompatActivity {

    Personne personne;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // instancier new Personne
        personne = new Personne();
        // on ouvre fichier
        File file = new File(MainActivity.this.getFilesDir(), "text");
        if (!file.exists()) {
            // si fichier existe pas on prend info au pif
            personne.setIdBluetooth(12346);
            saveInformation(personne,file);

        }
        else{
            // sinon on charge info
            personne = getInformation(personne);

        }
        // on met des données au pif dans personne, et la date du jour en String / test get et set

        Date today = new Date();
        Log.d("date", String.valueOf(today));
        String date = String.valueOf(today);
        personne.getConnexion().add(date);

        // on affiche personne
        Log.d("test context","test");
        personne.print();
        // à faire


        // si la personne c'est déjà enregistrer et on va sur la page etudiantControl
        if (personne.getIdEtudiant() != 0) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_etudiant_control);
        } else {
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



    public void saveInformation(Personne p,File file){



        // si le dossier existe pas on le créer
        file.mkdir();
        try {
            // on créer fichier
            File gpxfile = new File(file, "sample");
            Log.d("gpxfile", gpxfile.getAbsolutePath());

            Log.d("fichier","créer");
            Toast.makeText(MainActivity.this, "Saved your text", Toast.LENGTH_LONG).show();
        } catch (Exception e) { }


    }
    // on récupère info du fichier
    public Personne getInformation(Personne p){
        // on récup le fichier avec l'emplacement
        File fileEvents = new File(MainActivity.this.getFilesDir()+"/text/sample");
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileEvents));
            String line;
            // on lit chaque ligne pour récup info
            while ((line = br.readLine()) != null) {
                text.append(line);
                Log.d("ligne",line);
                //TODO faire regex pour séparer idetudiant et idbluetooth et stocké dans Personne
               // p.setIdBluetooth(Integer.parseInt(line));
                text.append('\n');
            }
            br.close();
        } catch (IOException e) { }
        String result = text.toString();

       // p.setIdEtudiant(Integer.parseInt(result));
        personne.print();
        Log.d("resultat!!!!!!!!",result);
        return p;
    }
}
