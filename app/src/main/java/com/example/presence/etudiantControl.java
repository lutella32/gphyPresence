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
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;




import java.io.File;
import java.io.FileWriter;

public class etudiantControl extends AppCompatActivity {
    // seconde page, où on voit ses connexions
    Personne personne;
    private Set<BluetoothDevice> devices;
    private BluetoothAdapter adaptateurBluetooth;
    //private ReceiverBluetooth receiverBluetooth = new ReceiverBluetooth();
    private IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    //private final static int REQUEST_CODE_ENABLE_BLUETOOTH = 0;
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
        // - établir une connexion bluetooth
        // - envoyer les données bluetooth
        // - ajouter la date du jour et de l'heure dans liste avec addConnexion quand connexion bluetooth établit
        // - mettre bouton check en vert si date du jour et heure ajoutées

        //Vérification connexion du bluetooth
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
                // Possibilité 1 :
                //Intent activeBlueTooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //startActivityForResult(activeBlueTooth, REQUEST_CODE_ENABLE_BLUETOOTH);
                // ou Possibilité 2:
                bluetoothAdapter.enable();
            }
            else
            {
                Log.d("bluetoothTest:","bluetoothAdapter déjà enable");
                Toast.makeText(getApplicationContext(), "Bluetooth activé", Toast.LENGTH_SHORT).show();
            }
        }

        //Liste des périphériques
        devices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice blueDevice : devices)
        {
            Log.d("bluetoothList:",blueDevice.getName());
            Toast.makeText(getApplicationContext(), "Device = " + blueDevice.getName(), Toast.LENGTH_SHORT).show();
        }

        // Filtre sur BroadcastReceiver
        //registerReceiver(receiverBluetooth, filter);

        //Recherche de nouveaux périphériques
        adaptateurBluetooth = BluetoothAdapter.getDefaultAdapter();
        adaptateurBluetooth.startDiscovery();

        //Rachel
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice("E4:70:B8:09:DF:ED");
        //Coline
        //BluetoothDevice device = bluetoothAdapter.getRemoteDevice("E4:42:A6:4C:CA:29");
        //Lucie
        //BluetoothDevice device = bluetoothAdapter.getRemoteDevice("E4:42:A6:4C:CA:29");
        //Lucie téléphone
        //BluetoothDevice device = bluetoothAdapter.getRemoteDevice("0c:60:46:44:41:fc");
        //Moi
        //BluetoothDevice device = bluetoothAdapter.getRemoteDevice("E0:F8:47:12:A8:5F");


        Log.d("BluetoothConnect", device.getAddress());
        Log.d("BluetoothConnect", device.getName());
        Connexion connexion = new Connexion(device);

        // Rends le périphérique visible
//        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
//        Log.d("Test", "pb");
//        startActivity(discoverableIntent);

        //Peripherique.connecter();

        if(personne.getConnexion().get(0)!=""){
            // test du bouton switch
            Switch switch1 = (Switch) findViewById(R.id.switch1);
            switch1.setChecked(true);
            // écriture de la date en position 0 dans le texte
            TextView text1 = (TextView) findViewById(R.id.textView11);
            text1.setText(personne.getConnexion().get(0));
        }
        saveInformation(personne);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (adaptateurBluetooth != null)
        {
            adaptateurBluetooth.cancelDiscovery();
            //unregisterReceiver(receiverBluetooth);
        }
    }


    //bouton exit
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
            Log.d("3", "Error when transferrinf from main");
        }
    }
    // script pour sauver information personne
    //TODO rajouter condition "première connexion etudiant on sauvegarde

    public void saveInformation(Personne p){

        File file = new File(etudiantControl.this.getFilesDir(), "text");

        try {
            File gpxfile = new File(file, "sample");
            Log.d("gpxfile", gpxfile.getAbsolutePath());
            FileWriter writer = new FileWriter(gpxfile);
            String b= String.valueOf(p.getIdBluetooth());
            String Id = String.valueOf(p.getIdEtudiant());
            writer.append(b);

            writer.append(Id);

            writer.flush();
            writer.close();

            Toast.makeText(etudiantControl.this, "Saved your text", Toast.LENGTH_LONG).show();
        } catch (Exception e) { }


    }


}
