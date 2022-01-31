package com.example.presence;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.bluetooth.BluetoothAdapter;
import android.widget.Toast;
import android.bluetooth.BluetoothDevice;

import java.io.IOException;
import java.util.Set;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;

public class etudiantControl extends AppCompatActivity {
    // seconde page, où on voit ses connexions
    Personne personne;
    private Set<BluetoothDevice> devices;
    private IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    //Rachel
    //private String adresseMAC = "E4:70:B8:09:DF:ED";
    //Moi
    //private String adresseMAC = "E0:F8:47:12:A8:5F";
    //Rachel
    private String adresseMAC = "E4:5E:37:3A:C3:B0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // on charge page et personne transmis par processIntentData
        super.onCreate(savedInstanceState);
        personne = new Personne();
        processIntentData();
        setContentView(R.layout.activity_etudiant_control);
        // on regarde la liste des connexions et si une date en position 0 existe, on l'affiche
        //à faire
        // - afficher  les 10 dates
        // - ajouter la date du jour et de l'heure dans liste avec addConnexion quand connexion bluetooth établit
        // - mettre bouton check en vert si date du jour et heure ajoutées

        if(personne.getConnexion().get(0)!=""){
            // test du bouton switch
            Switch switch1 = (Switch) findViewById(R.id.switch1);
            switch1.setChecked(true);
            // écriture de la date en position 0 dans le texte
            TextView text1 = (TextView) findViewById(R.id.textView11);
            text1.setText(personne.getConnexion().get(0));
        }
        saveInformationFile(personne);
    }

    //bouton "valider ma présence" : lancement de la connexion bluetooth
    public void connexionBT(View view){
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
        Log.d("BluetoothConnect", device.getAddress());
        Log.d("BluetoothConnect", device.getName());

        //Connexion à l'appareil
        int numEtu = personne.getIdEtudiant();
        String numID = personne.getIdTel();
        Connexion connexion = new Connexion(device, numEtu, numID);
        personne.print();
        Toast.makeText(getApplicationContext(), "Connexion établie", Toast.LENGTH_SHORT).show();
    }

    //Bouton exit
    public void finishing(View view) {

        finishAffinity();
        System.exit(0);
       // à faire
        // stocké les nouvelles données de connexion dans le fichier avant de exit
    }
    // code pour récupérer les données de personne depuis  la page main
    private void processIntentData(){
        Intent intent = getIntent();
        if(intent!=null){
            Personne transferredPerson = intent.getParcelableExtra("FromNumToStarting");
            if (transferredPerson!=null){
                this.personne = transferredPerson;
                this.personne.print();
                Log.d("1", "Personne ok");
            }
            else{
                Log.d("2", "No person found after transfer from main");
            }
        }
        else{
            Log.d("3", "Error when transferring from main");
        }
    }
    // script pour sauver information personne
    //TODO rajouter condition "première connexion etudiant on sauvegarde

    public void saveInformationFile(Personne p){

        File file = new File(etudiantControl.this.getFilesDir(), "text");

        try {
            File gpxfile = new File(file, "sample");
            Log.d("gpxfile", gpxfile.getAbsolutePath());
            FileWriter writer = new FileWriter(gpxfile);
            String b= String.valueOf(p.getIdTel());
            String Id = String.valueOf(p.getIdEtudiant());
            writer.append(b);
            writer.append(",");
            writer.append(Id);

            writer.flush();
            writer.close();
            Log.d("--*---------*-------*-----","----*------------*----------*");
            Log.d("information sauvegardées","ok");
            Log.d("--*---------*-------*-----","----*------------*----------*");
            Toast.makeText(etudiantControl.this, "Saved your text", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.d("--*---------*-------*-----","----*------------*----------*");
            Log.d("problème de sauvegarde","data");
            Log.d("--*---------*-------*-----","----*------------*----------*");
        }


    }


}
