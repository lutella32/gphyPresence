package com.example.presence;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;


public class MainActivity extends AppCompatActivity {

    Personne personne;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // instancier new Personne
        personne = new Personne();
        // on ouvre fichier
        File file = new File(MainActivity.this.getFilesDir(), "text4");
        if (!file.exists()) {
            // si fichier existe pas on prend info au pif
            personne.setIdTel(12346);
            createFile(personne,file);

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



    public void createFile(Personne p, File file){



        // si le dossier existe pas on le créer
        file.mkdir();
        try {
            // on créer fichier
            File gpxfile = new File(file, "sample");
            Log.d("gpxfile", gpxfile.getAbsolutePath());
            Log.d("--*---------*-------*-----","----*------------*----------*");
            Log.d("fichier","créer");
            Log.d("--*---------*-------*-----","----*------------*----------*");
            Toast.makeText(MainActivity.this, "Saved your text", Toast.LENGTH_LONG).show();
        } catch (Exception e) { }


    }
    // on récupère info du fichier
    public Personne getInformation(Personne p){
        // on récup le fichier avec l'emplacement
        File fileEvents = new File(MainActivity.this.getFilesDir()+"/text4/sample");
        Log.d("gpxfile", fileEvents.getAbsolutePath());
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileEvents));
            String line;
            // on lit chaque ligne pour récup info
            while ((line = br.readLine()) != null) {
                text.append(line);
                Log.d("--*---------*-------*-----","----*------------*----------*");
                Log.d("ligne",line);
                Log.d("--*---------*-------*-----","----*------------*----------*");
                //TODO faire regex pour séparer idetudiant et idbluetooth et stocké dans Personne
               // p.setIdBluetooth(Integer.parseInt(line));
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            Log.d("--*---------*-------*-----","----*------------*----------*");
            Log.d("problème de récupération de données","erreur");
            Log.d("--*---------*-------*-----","----*------------*----------*");
        }
        String result = text.toString();

       // p.setIdEtudiant(Integer.parseInt(result));
        personne.print();
        Log.d("resultat!!!!!!!!",result);
        return p;
    }
}
