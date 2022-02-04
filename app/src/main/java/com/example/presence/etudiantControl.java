package com.example.presence;
//imports
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

/**
 * classe etudiantControl : classe liée à l'activité activity_etudiant_control
 * qui est la page de connexions, où l'étudiant peut valider sa présence du jour
 * et visualiser l'historique de ses 10 ernières connexions
 */
public class etudiantControl extends AppCompatActivity {
    Personne personne; // objet personne contenant les informations de l'étudiant
    private Set<BluetoothDevice> devices; // liste des appareils bluetooth captés aux alentours
    private IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    private String adresseMAC = "04:42:1A:3F:5C:27"; // adresse MAC bluetooth de l'adaptateur bluetooth lié au serveur
    private String TAG = "classe etudiantControl";

    /**
     * method onCreate qui est lancée à la création de l'activité
     * @param savedInstanceState // pour le bon fonctionnement de l'activité
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        personne = new Personne(); // Création d'une nouvelle personne
        processIntentData(); // Chargement des infos de personne depuis l'activité précédente
        setContentView(R.layout.activity_etudiant_control); // affichage de l'activité

        // Ouverture du fichier permettant la sauvegarde des 10 dernières connexions de l'étudiant
        File file = new File(etudiantControl.this.getFilesDir(), "DossierCheck");
        if (!file.exists()) {
            createConnexionFile(personne,file); // si le dossier n'existe pas, on le crée
        }else {
            personne = getConnexionFile(personne); // sinon on charge les informations de personne stockées dans le ficheir
        }

        // Affichage des 10 dernières connexions
        afficheConnexion();

        // Sauvegarde des données de personne dans les fichiers
        saveInformationFile(personne); // sauvegarde des données de l'étudiant (numEtu, numId)
        saveConnexionFile(personne); // sauvegarde des connexions de l'étudiant (historique des 10 dernières connexions)
    }

    /**
     * Methode liée au bouton "Valider ma présence" : lance la connexion bluetooth et l'échange de message
     * @param view bouton "Valider ma présence"
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void connexionBT(View view){
        //Vérification activation du bluetooth
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null)
        {
            Log.d(TAG+" verif bluetooth","bluetoothAdapter null");
            Toast.makeText(getApplicationContext(), "Bluetooth non activé !", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if (!bluetoothAdapter.isEnabled())
            {
                Log.d(TAG+" verif bluetooth","bluetoothAdapter non disponible");
                Toast.makeText(getApplicationContext(), "Bluetooth non activé !", Toast.LENGTH_SHORT).show();
                bluetoothAdapter.enable();
            }
            else
            {
                Log.d(TAG+" verif bluetooth","bluetoothAdapter disponible");
                Toast.makeText(getApplicationContext(), "Bluetooth activé", Toast.LENGTH_SHORT).show();
            }
        }

        //Recherche de l'adaptateur dans la liste des périphériques liés
        devices = bluetoothAdapter.getBondedDevices();

        //Récupération de l'appareil : avec adresse MAC de l'adaptateur bluetooth
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(adresseMAC);

        //Si on trouve le nom de l'appareil, il existe
        if (device.getName() != null){
            //Affichage des logs des informations de l'apparareil
            Log.d("BluetoothConnect", device.getAddress());
            Log.d("BluetoothConnect", device.getName());

            //Connexion à l'appareil
            Connexion connexion = new Connexion(device, personne);
            Toast.makeText(getApplicationContext(), "Connexion établie", Toast.LENGTH_SHORT).show();

            //ré-affichage des connexions avec la nouvelle connexion
            afficheConnexion();

        }else{
            // Sinon, on n'a pas trouvé le serveur donc la connexion est impossible
            Log.d("BluetoothConnect", "echec de connexion : serveur introuvable");
            Toast.makeText(getApplicationContext(), "Connexion impossible", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Methode d'affichage des connexions sur l'ativité
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void afficheConnexion(){

        Switch switch1 = (Switch) findViewById(R.id.switch1);
        int tailleListe = personne.getConnexion().size();
        if (personne.getConnexion().size() > 0) {
            if (!personne.getConnexion().get(tailleListe-1).equals("null")) {
                String dateDerniereCo = personne.getConnexion().get(tailleListe - 1).replace(" ", "T");
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
        }

        if (tailleListe >= 1){
            TextView text9 = (TextView) findViewById(R.id.txtDate0);
            if (personne.getConnexion().get(tailleListe-1) != null && !personne.getConnexion().get(tailleListe-1).equals("null")) {
                text9.setText(personne.getConnexion().get(tailleListe-1));
            } else {
                text9.setText("");
            }
        } else {
            TextView text9 = (TextView) findViewById(R.id.txtDate0);
            text9.setText("");
        }

        if (tailleListe >=2) {
            TextView text8 = (TextView) findViewById(R.id.txtDate1);
            if (personne.getConnexion().get(tailleListe-2) != null && !personne.getConnexion().get(tailleListe-2).equals("null")) {
                text8.setText(personne.getConnexion().get(tailleListe-2));
            } else {
                text8.setText(" ");
            }
        } else {
            TextView text8 = (TextView) findViewById(R.id.txtDate1);
            text8.setText(" ");
        }

        if (tailleListe >=3) {
            TextView text7 = (TextView) findViewById(R.id.txtDate2);
            if (personne.getConnexion().get(tailleListe-3) != null && !personne.getConnexion().get(tailleListe-3).equals("null")) {
                text7.setText(personne.getConnexion().get(tailleListe-3));
            } else {
                text7.setText(" ");
            }
        } else {
            TextView text7 = (TextView) findViewById(R.id.txtDate2);
            text7.setText(" ");
        }

        if (tailleListe >=4) {
            TextView text6 = (TextView) findViewById(R.id.txtDate3);
            if (personne.getConnexion().get(tailleListe-4) != null && !personne.getConnexion().get(tailleListe-4).equals("null")) {
                text6.setText(personne.getConnexion().get(tailleListe-4));
            } else {
                text6.setText(" ");
            }
        } else {
            TextView text6 = (TextView) findViewById(R.id.txtDate3);
            text6.setText(" ");
        }

        if (tailleListe >=5) {
            TextView text5 = (TextView) findViewById(R.id.txtDate4);
            if (personne.getConnexion().get(tailleListe-5) != null && !personne.getConnexion().get(tailleListe-5).equals("null")) {
                text5.setText(personne.getConnexion().get(tailleListe-5));
            } else {
                text5.setText(" ");
            }
        } else {
            TextView text5 = (TextView) findViewById(R.id.txtDate4);
            text5.setText("");
        }

        if (tailleListe >=6) {
            TextView text4 = (TextView) findViewById(R.id.txtDate5);
            if (personne.getConnexion().get(tailleListe-6) != null && !personne.getConnexion().get(tailleListe-6).equals("null")) {
                text4.setText(personne.getConnexion().get(tailleListe-6));
            } else {
                text4.setText(" ");
            }
        } else {
            TextView text4 = (TextView) findViewById(R.id.txtDate5);
            text4.setText("");
        }

        if (tailleListe >=7) {
            TextView text3 = (TextView) findViewById(R.id.txtDate6);
            if (personne.getConnexion().get(tailleListe-7) != null && !personne.getConnexion().get(tailleListe-7).equals("null")) {
                text3.setText(personne.getConnexion().get(tailleListe-7));
            } else {
                text3.setText(" ");
            }
        } else {
            TextView text3 = (TextView) findViewById(R.id.txtDate6);
            text3.setText("");
        }

        if (tailleListe >=8) {
            TextView text2 = (TextView) findViewById(R.id.txtDate7);
            if (personne.getConnexion().get(tailleListe-8) != null && !personne.getConnexion().get(tailleListe-8).equals("null")) {
                text2.setText(personne.getConnexion().get(tailleListe-8));
            } else {
                text2.setText(" ");
            }
        } else {
            TextView text2 = (TextView) findViewById(R.id.txtDate7);
            text2.setText("");
        }

        if (tailleListe >=9) {
            TextView text1 = (TextView) findViewById(R.id.txtDate8);
            if (personne.getConnexion().get(tailleListe-9) != null && !personne.getConnexion().get(tailleListe-9).equals("null")) {
                text1.setText(personne.getConnexion().get(tailleListe-9));
            } else {
                text1.setText(" ");
            }
        } else {
            TextView text1 = (TextView) findViewById(R.id.txtDate8);
            text1.setText("");
        }

        if (tailleListe >=10) {
            TextView text0 = (TextView) findViewById(R.id.txtDate9);
            if (personne.getConnexion().get(tailleListe-10) != null && !personne.getConnexion().get(tailleListe-10).equals("null")) {
                text0.setText(personne.getConnexion().get(tailleListe-10));
            } else {
                text0.setText(" ");
            }
        } else {
            TextView text0 = (TextView) findViewById(R.id.txtDate9);
            text0.setText("");
        }
    }

    //Bouton exit
    public void finishing(View view) {
    try {
        saveConnexionFile(personne);
        Log.d("HEYHEYY","LES CONNEXIONS SONT SAUVEGARDEES DANS LE CONNEXION");
    }catch (Exception e){
        Log.d("ATTENTION ERREUR","LA SAUVEGARDE DU FICHIER FAILED");
    }

    finishAffinity();
    System.exit(0);
   // à faire
    // stocké les nouvelles données de connexion dans le fichier avant de exit
    }

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

        try {
            Log.d("-*-**-**-**-**-**", "**-**-**-**-**-**-");
            String nConnexion = "";
            ;
            for (int i = 0; i< result.length(); i++) {
                char Xchar = result.charAt(i);
                if (Xchar == ',') {
                    personne.addConnexion(nConnexion);
                    Log.d("Le nConnexion:", nConnexion);
                    nConnexion = "";
                    i=i+1;
                }
                else if (Xchar == '[') {
                    Log.d("Début de la liste [", "...");
                }
                else if (Xchar == ']') {
                    Log.d("Fin de la liste ]", "...");
                    personne.addConnexion(nConnexion);
                    Log.d("Le nConnexion:", nConnexion);
               // }else if (Xchar == ' ') {
                //    nConnexion = nConnexion + '_';
                } else {
                    nConnexion = nConnexion + Xchar;
                }
            }
            personne.print();
            Log.d("-*-**-**-**-**-**", "**-**-**-**-**-**-");
        }catch (Exception e){
            Log.d("-*-**-**-**-**-**", "**-**-**-**-**-**-");
            Log.d("Probleme :", "pas de set personne.connexion");
            Log.d("-*-**-**-**-**-**", "**-**-**-**-**-**-");
        }
        return p;
    }

    public void saveConnexionFile(Personne p){

        File file = new File(etudiantControl.this.getFilesDir(), "DossierCheck");

        try {
            File gpxfile = new File(file, "Connexion");
            Log.d("gpxfile2", gpxfile.getAbsolutePath());
            FileWriter writer = new FileWriter(gpxfile);
            String textInsert = String.valueOf(p.getConnexion());
            writer.append(textInsert);
            Log.d("C'est la liste des connexion recup de pers:", textInsert);
            writer.flush();
            writer.close();
            Log.d("--*---------*-------*-----","----*------------*----------*");
            Log.d("Connexions sauvegardées","ok");

            Log.d("--*---------*-------*-----","----*------------*----------*");
            Toast.makeText(etudiantControl.this, "Connexion sauvegardée", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.d("--*---------*-------*-----","----*------------*----------*");
            Log.d("problème de sauvegarde","data");
            Log.d("--*---------*-------*-----","----*------------*----------*");
        }
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
