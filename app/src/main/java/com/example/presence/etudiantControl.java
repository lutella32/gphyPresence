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

    Personne personne;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        personne = new Personne();
        personne.print();
        processIntentData();

        setContentView(R.layout.activity_etudiant_control);
        if(personne.getConnexion().get(0)!=""){
            Switch switch1 = (Switch) findViewById(R.id.switch1);
            switch1.setChecked(true);
            TextView text1 = (TextView) findViewById(R.id.textView11);
            text1.setText(personne.getConnexion().get(0));
        }
    }
    public void finishing(View view) {

        finishAffinity();
        System.exit(0);
        // android.os.Process.killProcess(android.os.Process.myPid());
    }
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
