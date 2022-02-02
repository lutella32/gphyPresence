package com.example.presence;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Personne personne;
    private String adresseMAC = "04:42:1A:3F:5C:27";
    private Set<BluetoothDevice> devices;
    Button boutonAccepter;
    Button boutonOui;
    Button boutonNon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // instancier new Personne
        personne = new Personne();

        // on ouvre fichier
        File file = new File(MainActivity.this.getFilesDir(), "text");
        if (!file.exists()) {
            // si fichier existe pas on prend info au pif
            String newID;
            newID = GenerateUUID();
            personne.setIdTel(newID);
            Log.d("--*------*-----*-----","----*------------*----------*");
            Log.d("Le idtel généré auto:",personne.getIdTel());
            Log.d("--*------*-----*-----","----*------------*----------*");
        }
        else{
            // sinon on charge info
            personne = getInformationFile(personne);
        }

        // on affiche personne
        Log.d("test context","test");
        personne.print();
        // à faire


        // si la personne c'est déjà enregistrer et on va sur la page etudiantControl
        if (personne.getIdEtudiant() != 0) {
            super.onCreate(savedInstanceState);
//            setContentView(R.layout.activity_etudiant_control);
            Intent lecture = new Intent(this, etudiantControl.class);
            lecture.putExtra("FromNumToStarting", this.personne);
            startActivity(lecture);
        } else {
            // sinon on reste sur la page main
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            //bouton accepter non disponible pour l'instant
            boutonAccepter = (Button) findViewById(R.id.buttonAccepter);
            boutonAccepter.setEnabled(false);
            boutonOui = (Button) findViewById(R.id.buttonOui);
            boutonOui.setEnabled(false);
            boutonNon = (Button) findViewById(R.id.buttonNon);
            boutonNon.setEnabled(false);

        }

    }


        public void start(android.view.View v) {
        // si on clique sur accepter dans le main
        Log.d("start","you are in start");

        //Creation du fichier
            File file = new File(MainActivity.this.getFilesDir(), "text");
            createFile(personne,file);

            // on passe à la page etudiantControl et on transmet personne avec l'idetudiant enregistré
            Intent lecture = new Intent(this, etudiantControl.class);
            lecture.putExtra("FromNumToStarting", this.personne);
            startActivity(lecture);
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
            Log.d("--*------*-----*-----","----*------------*----------*");
            Log.d("fichier","créer");
            Log.d("--*----*------*-----","----*------------*----------*");
            Toast.makeText(MainActivity.this, "Saved your text", Toast.LENGTH_LONG).show();
        } catch (Exception e) { }


    }
    // on récupère info du fichier
    public Personne getInformationFile(Personne p){
        // on récup le fichier avec l'emplacement
        File fileEvents = new File(MainActivity.this.getFilesDir()+"/text/sample");
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
                //text.append('\n');
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
        Log.d("--*---------*-------*-----","----*------------*----------*");
        Log.d("Ce qu'il y a dans Sample.txt:",result);
        Log.d("--*---------*-------*-----","----*------------*----------*");
        String[] ListDesId = result.split(",", 2);

        Log.d("-*-**-**-**-**-**", "**-**-**-**-**-**-");
        for (String a : ListDesId) {
            Log.d("Liste de IdTel et idEtu extrait du sample avec split:", a); }
        Log.d("-*-**-**-**-**-**", "**-**-**-**-**-**-");
        Log.d("Liste0",ListDesId[0]);
        //Log.d("Liste1",ListDesId[1]);

        try {
            //Reaffectation de l'idTel et de l'idEtu dextrait du sample dans personne
            p.setIdTel(ListDesId[0]);
            p.setIdEtudiant(Integer.valueOf(ListDesId[1]));
            Log.d("--*---------*-------*-----", "----*------------*----------*");
            Log.d("Id tel de personne", String.valueOf(p.getIdTel()));
            Log.d("Id etu de personne", String.valueOf(p.getIdEtudiant()));
            Log.d("--*---------*-------*-----", "----*------------*----------*");
        }catch (Exception e){
            Log.d("--*---------*-------*-----", "----*------------*----------*");
            Log.d("ERREUR","Bien essayé boloss");
            Log.d("--*---------*-------*-----", "----*------------*----------*");
        }

        return p;
    }
    //public static class GenerateUUID {

    public String GenerateUUID(){
        //generate random UUIDs
        UUID idOne = UUID.randomUUID();
        String UniqId = String.valueOf(idOne);
        Log.d("UUID One: " , String.valueOf(idOne));
        return UniqId;
    }

    //bouton Valider
    public void verifIdentity(View view){

        // récupération de ce qu'à écrit l'étudiant
        EditText number = (EditText) findViewById(R.id.txtNumEtu);

        // création de ce qu'il faut pour le toast ( message erreur, si pas idetudiant remplis)
        Context context = getApplicationContext();
        CharSequence textToast = "You need to enter a student id";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, textToast, duration);

        if (!number.getText().toString().equals("")) {

            // convertir idEtudiant en int pour stocker dans personne
            Integer n = Integer.parseInt(number.getText().toString());
            personne.setIdEtudiant(n);
        } else {
            // message si idetudiant pas remplis
            toast.show();
            return;
        }

        //Vérification activation du bluetooth
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null)
        {
            Log.d("bluetoothTest:","bluetoothAdapter null");
            Toast.makeText(getApplicationContext(), "Bluetooth non activé !", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if (!bluetoothAdapter.isEnabled())
            {
                Log.d("bluetoothTest:","bluetoothAdapter not enable");
                Toast.makeText(getApplicationContext(), "Bluetooth non activé !", Toast.LENGTH_SHORT).show();
                bluetoothAdapter.enable();
            }
            else
            {
                Log.d("bluetoothTest:","bluetoothAdapter déjà enable");
                Toast.makeText(getApplicationContext(), "Bluetooth activé", Toast.LENGTH_SHORT).show();
            }
        }

        //Recherche de l'adaptateur dans la liste des périphériques liés
        devices = bluetoothAdapter.getBondedDevices();

        //Récupération de l'appareil auquel on veut se connecter
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(adresseMAC);


        //Information de l'appareil
        if (device.getName() != null){
            Log.d("BluetoothConnect", device.getAddress());
            Log.d("BluetoothConnect", device.getName());

            //Connexion à l'appareil
            int numEtu = personne.getIdEtudiant();
            String numID = personne.getIdTel();
            Connexion connexion = new Connexion(device, numEtu, numID, personne);
            Toast.makeText(getApplicationContext(), "Connexion établie", Toast.LENGTH_SHORT).show();
            afficheNom();
            boutonOui.setEnabled(true);
            boutonNon.setEnabled(true);

        }else{
            Log.d("BluetoothConnect", "echec de connexion : serveur introuvable");
            Toast.makeText(getApplicationContext(), "Connexion impossible", Toast.LENGTH_SHORT).show();
        }
        afficheNom();
    }

    public void afficheNom(){
        TextView textNomPrenom = (TextView) findViewById(R.id.txtNomPrenom);
        textNomPrenom.setText(personne.getNomPrenom());
    }

    public void buttonOui(View view){
        boutonAccepter.setEnabled(true);
    }

    public void buttonNon(View view){
        Toast.makeText(getApplicationContext(), "Rapprochez-vous de votre responsable de formation", Toast.LENGTH_SHORT).show();
    }
}
