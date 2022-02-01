package com.example.presence;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    //private String adresseMAC = "E4:5E:37:3A:C3:B0";
    //Adaptateur
    private String adresseMAC = "04:42:1A:3F:5C:27";


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // on charge page et personne transmis par processIntentData
        super.onCreate(savedInstanceState);
        personne = new Personne();
        processIntentData();
        setContentView(R.layout.activity_etudiant_control);

        // Affichage des 10 dernières connexions
        afficheConnexion();

        saveInformationFile(personne);
    }

    //bouton "valider ma présence" : lancement de la connexion bluetooth
    @RequiresApi(api = Build.VERSION_CODES.O)
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
//        try {
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(adresseMAC);
//        }catch( e){
//
//        }

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
            afficheConnexion();

        }else{
            Log.d("BluetoothConnect", "echec de connexion : serveur introuvable");
            Toast.makeText(getApplicationContext(), "Connexion impossible", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void afficheConnexion(){

        Switch switch1 = (Switch) findViewById(R.id.switch1);
        int tailleListe = personne.getConnexion().size();
        if (personne.getConnexion().size() > 0) {
            String dateDerniereCo = personne.getConnexion().get(tailleListe-1).replace(" ", "T");
            Log.d("Test date", dateDerniereCo);
            LocalDateTime dateTime = LocalDateTime.parse(dateDerniereCo);
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime nowMorning1 = LocalDateTime.now().withHour(8).withMinute(0).withSecond(0);
            LocalDateTime nowMorning2 = LocalDateTime.now().withHour(12).withMinute(30).withSecond(0);
            LocalDateTime nowAfternoon1 = LocalDateTime.now().withHour(14).withMinute(0).withSecond(0);
            LocalDateTime nowAfternoon2 = LocalDateTime.now().withHour(19).withMinute(00).withSecond(0);

            boolean matin = now.isBefore(nowMorning2);
            boolean apresM = now.isAfter(nowAfternoon1);

            if (matin) {
                if (dateTime.isAfter(nowMorning1) && dateTime.isBefore(nowMorning2)) {
                    switch1.setChecked(true);
                }
            } else if (apresM) {
                if (dateTime.isAfter(nowAfternoon1) && dateTime.isBefore(nowAfternoon2)) {
                    switch1.setChecked(true);
                }
            } else {
                switch1.setChecked(false);
            }
        }

        if (10 <= personne.getConnexion().size()){
            if (personne.getConnexion().get(9) != null) {
                TextView text9 = (TextView) findViewById(R.id.txtDate0);
                text9.setText(personne.getConnexion().get(9));
            }
        } else {
            TextView text9 = (TextView) findViewById(R.id.txtDate0);
            text9.setText("");
        }

        if (9 <= personne.getConnexion().size()) {
            if (personne.getConnexion().get(8) != null) {
                TextView text8 = (TextView) findViewById(R.id.txtDate1);
                text8.setText(personne.getConnexion().get(8));
            }
        } else {
            TextView text8 = (TextView) findViewById(R.id.txtDate1);
            text8.setText(" ");
        }

        if (8 <= personne.getConnexion().size()) {
            if (personne.getConnexion().get(7) != null) {
                TextView text7 = (TextView) findViewById(R.id.txtDate2);
                text7.setText(personne.getConnexion().get(7));
            }
        } else {
            TextView text7 = (TextView) findViewById(R.id.txtDate2);
            text7.setText(" ");
        }

        if (7 <= personne.getConnexion().size()) {
            if (personne.getConnexion().get(6) != null) {
                TextView text6 = (TextView) findViewById(R.id.txtDate3);
                text6.setText(personne.getConnexion().get(6));
            }
        } else {
            TextView text6 = (TextView) findViewById(R.id.txtDate3);
            text6.setText(" ");
        }

        if (6 <= personne.getConnexion().size()) {
            if (personne.getConnexion().get(5) != null) {
                TextView text5 = (TextView) findViewById(R.id.txtDate4);
                text5.setText(personne.getConnexion().get(5));
            }
        } else {
            TextView text5 = (TextView) findViewById(R.id.txtDate4);
            text5.setText("");
        }

        if (5 <= personne.getConnexion().size()) {
            if (personne.getConnexion().get(4) != null) {
                TextView text4 = (TextView) findViewById(R.id.txtDate5);
                text4.setText(personne.getConnexion().get(4));
            }
        } else {
            TextView text4 = (TextView) findViewById(R.id.txtDate5);
            text4.setText("");
        }

        if (4 <= personne.getConnexion().size()) {
            if (personne.getConnexion().get(3) != null) {
                TextView text3 = (TextView) findViewById(R.id.txtDate6);
                text3.setText(personne.getConnexion().get(3));
            }
        } else {
            TextView text3 = (TextView) findViewById(R.id.txtDate6);
            text3.setText("");
        }

        if (3 <= personne.getConnexion().size()) {
            if (personne.getConnexion().get(2) != null) {
                TextView text2 = (TextView) findViewById(R.id.txtDate7);
                text2.setText(personne.getConnexion().get(2));
            }
        } else {
            TextView text2 = (TextView) findViewById(R.id.txtDate7);
            text2.setText("");
        }

        if (2 <= personne.getConnexion().size()) {
            if (personne.getConnexion().get(1) != null) {
                TextView text1 = (TextView) findViewById(R.id.txtDate8);
                text1.setText(personne.getConnexion().get(1));
            }
        } else {
            TextView text1 = (TextView) findViewById(R.id.txtDate8);
            text1.setText("");
        }

        if (1 <= personne.getConnexion().size()) {
            if (personne.getConnexion().get(0) != null) {
                TextView text0 = (TextView) findViewById(R.id.txtDate9);
                text0.setText(personne.getConnexion().get(0));
            }
        } else {
            TextView text0 = (TextView) findViewById(R.id.txtDate9);
            text0.setText("");
        }
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
