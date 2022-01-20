package com.example.presence;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

public class etudiantControl extends AppCompatActivity {
    // seconde page, où on voit ses connexions
    Personne personne;
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
        if(personne.getConnexion().get(0)!=""){
            // test du bouton switch
            Switch switch1 = (Switch) findViewById(R.id.switch1);
            switch1.setChecked(true);
            // écriture de la date en position 0 dans le texte
            TextView text1 = (TextView) findViewById(R.id.textView11);
            text1.setText(personne.getConnexion().get(0));
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


}
