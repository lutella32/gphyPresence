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

import java.io.BufferedReader;
import java.io.FileReader;
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
        //File fileEvents = new File(etudiantControl.this.getFilesDir()+"/DossierCheck/Connexion");
        // on ouvre fichier
      /*  File file = new File(etudiantControl.this.getFilesDir(), "DossierCheck");
        if (!file.exists()) {
            createConnexionFile(personne,file);
        }
        // sinon on charge info
        personne = getConnexionFile(personne);
*/
    finishAffinity();
    System.exit(0);
   // à faire
    // stocké les nouvelles données de connexion dans le fichier avant de exit
    }
/*
    public void createConnexionFile(Personne p, File file){
        // si le dossier existe pas on le créer
        file.mkdir();
        try {
            // on créer fichier
            File gpxfile = new File(file, "Connexion");
            Log.d("gpxfile2", gpxfile.getAbsolutePath());
            Log.d("--*------*-----*-----","----*------------*----------*");
            Log.d("fichier Connexion","créer");
            Log.d("--*----*------*-----","----*------------*----------*");
        } catch (Exception e) {
            Log.d("ERREUR","le fichier de sauvegarde des checks --> ECHEC");
        }
    }

    // on récupère info du fichier
    public Personne getConnexionFile(Personne p){
        // on récup le fichier avec l'emplacement
        File fileEvents = new File(etudiantControl.this.getFilesDir()+"/DossierCheck/Connexion");
        Log.d("gpxfile2", fileEvents.getAbsolutePath());
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
        Log.d("--*---------*-------*-----","----*------------*----------*");
        Log.d("Ce qu'il y a dans connexion.txt:",result);
        Log.d("--*---------*-------*-----","----*------------*----------*");

        Log.d("-*-**-**-**-**-**", "**-**-**-**-**-**-");
*/
       /* for (char i : result.toCharArray()) {
            String nConnexion = "";
            char Xchar = result.charAt(i);
            if (Xchar.Equals",") {

            }else{
                nConnexion = nConnexion + Xchar;
            }

            Log.d("Liste de IdTel et idEtu extrait du sample avec split:", nConnexion); }
        Log.d("-*-**-**-**-**-**", "**-**-**-**-**-**-");


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
    }*/

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
            Log.d("b c'est l'idtel:", b);
            Log.d("p.idtel c'est l'idtel:", p.getIdTel());
            writer.append(",");
            writer.append(Id);
            Log.d("Id c'est l'idetu:", Id);

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
