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

import java.util.Set;

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
    //Lucie
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


            // test du bouton switch
            Switch switch1 = (Switch) findViewById(R.id.switch1);
            switch1.setChecked(true);
            // écriture de la date en position 0 dans le texte


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
        if (device.getName() != null){
            Log.d("BluetoothConnect", device.getAddress());
            Log.d("BluetoothConnect", device.getName());

            //Connexion à l'appareil
            int numEtu = personne.getIdEtudiant();
            String numID = personne.getIdTel();
            Connexion connexion = new Connexion(device, numEtu, numID, personne);
            //personne.print();
            Toast.makeText(getApplicationContext(), "Connexion établie", Toast.LENGTH_SHORT).show();

            if (10 <= personne.getConnexion().size()){
                if (personne.getConnexion().get(9) != null) {
                    TextView text9 = (TextView) findViewById(R.id.txtDate9);
                    text9.setText(personne.getConnexion().get(9));
                } else {
                    TextView text9 = (TextView) findViewById(R.id.txtDate9);
                    text9.setText("");
                }
            }
            if (9 <= personne.getConnexion().size()) {
                if (personne.getConnexion().get(8) != null) {
                    TextView text8 = (TextView) findViewById(R.id.txtDate8);
                    text8.setText(personne.getConnexion().get(8));
                } else {
                    TextView text8 = (TextView) findViewById(R.id.txtDate8);
                    text8.setText("");
                }
            }
            if (8 <= personne.getConnexion().size()) {
                if (personne.getConnexion().get(7) != null) {
                    TextView text7 = (TextView) findViewById(R.id.txtDate7);
                    text7.setText(personne.getConnexion().get(7));
                } else {
                    TextView text7 = (TextView) findViewById(R.id.txtDate7);
                    text7.setText("");
                }
            }
            if (7 <= personne.getConnexion().size()) {
                if (personne.getConnexion().get(6) != null) {
                    TextView text6 = (TextView) findViewById(R.id.txtDate6);
                    text6.setText(personne.getConnexion().get(6));
                } else {
                    TextView text6 = (TextView) findViewById(R.id.txtDate6);
                    text6.setText("");
                }
            }
            if (6 <= personne.getConnexion().size()) {
                if (personne.getConnexion().get(5) != null) {
                    TextView text5 = (TextView) findViewById(R.id.txtDate5);
                    text5.setText(personne.getConnexion().get(5));
                } else {
                    TextView text5 = (TextView) findViewById(R.id.txtDate5);
                    text5.setText("");
                }
            }
            if (5 <= personne.getConnexion().size()) {
                if (personne.getConnexion().get(4) != null) {
                    TextView text4 = (TextView) findViewById(R.id.txtDate4);
                    text4.setText(personne.getConnexion().get(4));
                } else {
                    TextView text4 = (TextView) findViewById(R.id.txtDate4);
                    text4.setText("");
                }
            }
            if (4 <= personne.getConnexion().size()) {
                if (personne.getConnexion().get(3) != null) {
                    TextView text3 = (TextView) findViewById(R.id.txtDate3);
                    text3.setText(personne.getConnexion().get(3));
                } else {
                    TextView text3 = (TextView) findViewById(R.id.txtDate3);
                    text3.setText("");
                }
            }
            if (3 <= personne.getConnexion().size()) {
                if (personne.getConnexion().get(2) != null) {
                    TextView text2 = (TextView) findViewById(R.id.txtDate2);
                    text2.setText(personne.getConnexion().get(2));
                } else {
                    TextView text2 = (TextView) findViewById(R.id.txtDate2);
                    text2.setText("");
                }
            }
            if (2 <= personne.getConnexion().size()) {
                if (personne.getConnexion().get(1) != null) {
                    TextView text1 = (TextView) findViewById(R.id.txtDate1);
                    text1.setText(personne.getConnexion().get(1));
                } else {
                    TextView text1 = (TextView) findViewById(R.id.txtDate1);
                    text1.setText("");
                }
            }
            if (1 <= personne.getConnexion().size()) {
                //if (personne.getConnexion().get(0) != null) {
                TextView text0 = (TextView) findViewById(R.id.txtDate0);
                text0.setText(personne.getConnexion().get(0));
                //} else {
                //    TextView text0 = (TextView) findViewById(R.id.txtDate0);
                //    text0.setText("");
                //}
            }

        }else{
            Log.d("BluetoothConnect", "echec de connexion : serveur introuvable");
            Toast.makeText(getApplicationContext(), "Connexion impossible", Toast.LENGTH_SHORT).show();
        }
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
            String b= p.getIdTel();
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
