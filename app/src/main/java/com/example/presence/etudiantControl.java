package com.example.presence;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class etudiantControl extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etudiant_control);
    } public void finishing(View view) {

        finishAffinity();
        System.exit(0);
        // android.os.Process.killProcess(android.os.Process.myPid());
    }

}