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
 * et visualiser l'historique de ses 10 dernières connexions
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

            //Connexion à l'appareil (création d'une nouvelle connexion)
            Connexion connexion = new Connexion(device, personne);
            Toast.makeText(getApplicationContext(), "Connexion établie", Toast.LENGTH_SHORT).show();

            //ré-affichage des connexions avec la nouvelle connexion
            afficheConnexion();

        }else{
            // Sinon, on n'a pas trouvé le serveur donc la connexion est impossible
            Log.d("BluetoothConnect", "echec de connexion : serveur introuvable");
            Toast.makeText(getApplicationContext(), "Connexion impossible", Toast.LENGTH_SHORT).show();
        }
        try {
            //Fonction appelée: Essai de recupérer la liste des 10 dernieres connexions de l'objet personne
            //et insertion de la liste dans le fichier connexion.txt
            saveConnexionFile(personne);
            Log.d("Bonne Nouvelle:","LES CONNEXIONS SONT SAUVEGARDEES DANS LE CONNEXION");
        }catch (Exception e){
            Log.d("ATTENTION ERREUR:","LA SAUVEGARDE DU FICHIER FAILED");
        }
    }

    /**
     * Methode d'affichage des connexions sur l'activité
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void afficheConnexion(){

        // Gestion du switch
        Switch switch1 = (Switch) findViewById(R.id.switch1); // switch qui passe au vert si l'étudiant s'est connecté dans la demi-journée
        int tailleListe = personne.getConnexion().size(); // taille de la liste des connexions

        // Si la liste n'est pas vide, on regarde la dernière connexion
        if (personne.getConnexion().size() > 0) {
            //Si la dernière connexion n'est pas égale à "null"
            if (!personne.getConnexion().get(tailleListe-1).equals("null")) {
                // On remplace l'espace de la date par un 'T' pour pouvoir la convertir en LocalDateTime
                String dateDerniereCo = personne.getConnexion().get(tailleListe - 1).replace(" ", "T");
                Log.d("Test date", dateDerniereCo);
                LocalDateTime dateTime = LocalDateTime.parse(dateDerniereCo); // conversion de la date en LocalDateTime
                LocalDateTime now = LocalDateTime.now(); // date du moment
                LocalDateTime nowMorning1 = LocalDateTime.now().withHour(8).withMinute(0).withSecond(0); // date limite de check le matin
                LocalDateTime nowMorning2 = LocalDateTime.now().withHour(12).withMinute(30).withSecond(0); // date limite de check le matin
                LocalDateTime nowAfternoon1 = LocalDateTime.now().withHour(14).withMinute(0).withSecond(0); // date limite de check l'après-midi
                LocalDateTime nowAfternoon2 = LocalDateTime.now().withHour(19).withMinute(00).withSecond(0); // date limite de check l'après-midi

                // On regarde si on est le matin ou l'après midi
                boolean matin = now.isBefore(nowMorning2);
                boolean apresM = now.isAfter(nowAfternoon1);

                // Si c'est le matin
                if (matin) {
                    // Si la dernière signature est comprise dans les horaires de la matinée
                    if (dateTime.isAfter(nowMorning1) && dateTime.isBefore(nowMorning2)) {
                        // On passe le switch en vert
                        switch1.setChecked(true);
                    }
                // Si c'est l'après-midi
                } else if (apresM) {
                    // Si la dernière signature est comprise dans les horaires de l'après-midi
                    if (dateTime.isAfter(nowAfternoon1) && dateTime.isBefore(nowAfternoon2)) {
                        // On passe le switch en vert
                        switch1.setChecked(true);
                    }
                } else {
                    // Sinon il reste gris
                    switch1.setChecked(false);
                }
            }
        }

        // Affichage des 10 dernières connexions
        // Si la taille de la liste est supérieur ou égale à 1
        if (tailleListe >= 1){
            TextView text9 = (TextView) findViewById(R.id.txtDate0); // déclaration du text view
            // si la connexion est différente de null
            if (personne.getConnexion().get(tailleListe-1) != null && !personne.getConnexion().get(tailleListe-1).equals("null")) {
                // on l'affiche dans le textView
                text9.setText(personne.getConnexion().get(tailleListe-1));
            } else {
                // Sinon le textView est vide
                text9.setText("");
            }
        } else {
            // Sinon le textView est vide
            TextView text9 = (TextView) findViewById(R.id.txtDate0);
            text9.setText("");
        }

        // Même chose pour les 10 connexions suivantes

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


    /**
     * Action du bouton "exit"
     * @param view
     */
    public void finishing(View view) {
    //fonction android qui permet d'arreter toutes les activité qui pourrais encore être en cours
    finishAffinity();
    //fonction qui permet de fermer le programme en notifiant au sys d'exploitation que c'est normal '0'
    System.exit(0);
    }

    /**
     *
     * @param p
     * @param file
     */
    public void createConnexionFile(Personne p, File file){
        // Creation du dossier file entrer en paramètre
        file.mkdir();
        try {
            // on créer le fichier
            File gpxfile = new File(file, "Connexion");
            Log.d("gpxfile2", gpxfile.getAbsolutePath());
            Log.d("--*------*-----*-----","----*------------*----------*");
            Log.d("fichier Connexion","créer");
            Log.d("--*----*------*-----","----*------------*----------*");
        } catch (Exception e) {
            Log.d("ERREUR","le fichier de sauvegarde des checks --> ECHEC");
        }
    }

    /**
     * Fonction qui récupère les infos du fichier connexion.txt (les 10 derniers checks)
     * @param p
     * @return
     */
    public Personne getConnexionFile(Personne p){
        // on récupère le fichier avec l'emplacement
        File fileEvents = new File(etudiantControl.this.getFilesDir()+"/DossierCheck/Connexion");
        Log.d("gpxfile2", fileEvents.getAbsolutePath());
        //Creation d'un objet de type string
        StringBuilder text = new StringBuilder();
        try {
            //creation d'un tampon de lecture pour le fichier connexion.txt
            BufferedReader br = new BufferedReader(new FileReader(fileEvents));
            String line;
            // on lit chaque ligne du fichier pour récupérer les données tant que la ligne n'est pas vide
            while ((line = br.readLine()) != null) {
                //ajout du texte de la ligne dans la variable line
                text.append(line);
                Log.d("--*---------*-------*-----","----*------------*----------*");
                Log.d("ligne",line);
                Log.d("--*---------*-------*-----","----*------------*----------*");
            }
            //Fermeture de l'objet tampon
            br.close();
        } catch (IOException e) {
            Log.d("--*---------*-------*-----","----*------------*----------*");
            Log.d("problème de récupération de données","erreur");
            Log.d("--*---------*-------*-----","----*------------*----------*");
        }
        //affectation du contenu du tampn text à la variable String result
        String result = text.toString();
        Log.d("--*---------*-------*-----","----*------------*----------*");
        Log.d("Ce qu'il y a dans connexion.txt:",result);
        Log.d("--*---------*-------*-----","----*------------*----------*");

        try {
            Log.d("-*-**-**-**-**-**", "**-**-**-**-**-**-");
            // initialisation de nConnexion à vide
            String nConnexion = "";
            ;
            //parcours tout le string "result" extrait du fichier caractère par caractère
            for (int i = 0; i< result.length(); i++) {
                //creation d'une variable de type char qui va prendre pour chaque tour le char i
                char Xchar = result.charAt(i);
                //si le caractère est une virgule
                if (Xchar == ',') {
                    //alors on ajoute le string nConnexion à la liste personne.connexion[]
                    //qui contient alors tous les caractères d'une connexion
                    personne.addConnexion(nConnexion);
                    Log.d("Le nConnexion:", nConnexion);
                    //On réinitialise la var nConnexion à null pour qu'elle puisse accueillir la suivante
                    nConnexion = "";
                    // on passe l'indice i au suivant pour parcourir tout le string
                    i=i+1;
                }
                //Si le caractere est un crochet gauche
                else if (Xchar == '[') {
                    //alors on fait rien car c'est le début du string
                    Log.d("Début de la liste [", "...");
                }
                //Si le caractere est un crochet droit
                else if (Xchar == ']') {
                    Log.d("Fin de la liste ]", "...");
                    //alors on ajout nConnexion à la liste personne.connexion[]
                    personne.addConnexion(nConnexion);
                    Log.d("Le nConnexion:", nConnexion);
                // Sinon le caractere est ni , ni [ ni ] alors
                } else {
                // le caractère i est ajouté a la suite du string nConnexion
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
        //on retourne personne
        return p;
    }

    /**
     * Fonction qui sauvegarde les données de la liste personne.connexion[] dans un fichier
     * @param p
     */
    public void saveConnexionFile(Personne p){
        //creation du dossier
        File file = new File(etudiantControl.this.getFilesDir(), "DossierCheck");

        try {
            //creation du fichier connexion
            File gpxfile = new File(file, "Connexion");
            Log.d("gpxfile2", gpxfile.getAbsolutePath());
            //Insertion du fichier connexion dans le dossier DossierCheck
            FileWriter writer = new FileWriter(gpxfile);
            //création d'une var String qui prend la valeur de la liste connexion de personne
            String textInsert = String.valueOf(p.getConnexion());
            //écriture de la variable textInsert dans le fichier connexion.txt
            writer.append(textInsert);
            Log.d("C'est la liste des connexion recup de pers:", textInsert);
            // élimination des résidus restant de l'activité d'écriture
            writer.flush();
            //arret de l'écriture fermeture de l'action
            writer.close();
            Log.d("--*---------*-------*-----","----*------------*----------*");
            Log.d("Connexions sauvegardées","ok");
            Log.d("--*---------*-------*-----","----*------------*----------*");
            // génération d'un toast connexion sauvegardée sur l'application pour l'utilisateur
            Toast.makeText(etudiantControl.this, "Connexion sauvegardée", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.d("--*---------*-------*-----","----*------------*----------*");
            Log.d("problème de sauvegarde","data");
            Log.d("--*---------*-------*-----","----*------------*----------*");
        }
    }

    /**
     * fonction qui récupère les données de personne depuis  la page MainActivity
     */
    private void processIntentData(){
        //récupération des données de l'activité précédente
        Intent intent = getIntent();
        //Si on récupère des infos
        if(intent!=null){
            //alors je récupère les ancienne données de personne
            Personne transferredPerson = intent.getParcelableExtra("FromNumToStarting");
            //et si j'ai bien récupéré des données
            if (transferredPerson!=null){
                //alors je remet ce donnée dans mon objet personne de cette activité
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

    /**
     * fonction qui sauvegarde l'id unique du telephone et le numéro étudiant dans un fichier
     * @param p
     */
    public void saveInformationFile(Personne p){
        //creation du dossier
        File file = new File(etudiantControl.this.getFilesDir(), "text");

        try {
            //creation du fichier sample
            File gpxfile = new File(file, "sample");
            Log.d("gpxfile", gpxfile.getAbsolutePath());
            //creation d'un outil d'écriture du fichier sample
            FileWriter writer = new FileWriter(gpxfile);
            //on créer une variable b qui récupère l'id tel
            String b= p.getIdTel();
            //on créer une variable Id qui récupère l'id étudiant
            String Id = String.valueOf(p.getIdEtudiant());
            //Dans le fichier sample on écrit d'abord le l'id tel
            writer.append(b);
            Log.d("b c'est l'idtel:", b);
            Log.d("p.idtel c'est l'idtel:", p.getIdTel());
            //Dans le fichier sample on écrit a la suite une ","
            writer.append(",");
            //Dans le fichier sample on écrit l'id etudiant
            writer.append(Id);
            Log.d("Id c'est l'idetu:", Id);

            //On vide l'outil d'écriture
            writer.flush();
            // On ferme l'activité d'écriture
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
