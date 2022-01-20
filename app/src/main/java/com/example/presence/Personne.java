package com.example.presence;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Personne implements Parcelable {
    // Personne possède un idEtudiant, un idBluetooth et une liste de connexion en String
    private int idEtudiant;
    private int idBluetooth;
    private ArrayList<String> connexion;
    // les valeurs à la création de personne sont 0,0," "
    public Personne(){
        idBluetooth=0;
        idEtudiant=0;
        connexion = new ArrayList<String>();

    }
    // code générre automatiquement par android studio
    protected Personne(Parcel in) {
        idEtudiant = in.readInt();
        idBluetooth = in.readInt();
        connexion = in.createStringArrayList();
    }
    // surchage de la méthode toStrong avec les getter
    public String toString(){
        StringBuilder sBuilder = new StringBuilder("\t Numéros Etudiant: " + this.getIdEtudiant() + "\n");
        sBuilder.append("\t Id Bluetooth: ").append(this.getIdBluetooth()).append("\n");
        sBuilder.append("\t Connexion :").append(this.getConnexion()).append("\n");

        return sBuilder.toString();
    }
    // surchage de la méthode print
    public void print(){
        System.out.println("Person's attributes: ");
        System.out.print(this);
        System.out.println();
    }


    //Getter / Setter IdEtudiant
    public int getIdEtudiant() { return idEtudiant; }
    public void setIdEtudiant(int num) { this.idEtudiant = num; }


    //Getter / Setter IdBluetooth
    public int getIdBluetooth() { return idBluetooth; }
    public void setIdBluetooth(int id) { this.idBluetooth = id; }

    //Getter / Setter
    public ArrayList<String> getConnexion() {
        return connexion;
    }

    public void setConnexion(ArrayList<String> connexion) {
        this.connexion = connexion;
    }
    // à faire pour la liste, une fonction addConnexion qui ajoute la dernire connexion en première position, décale tous d'un grand et supprime la connexion position 10

    // code généré par android studio pour transmettre personne d'une page à l'autre
    public static final Creator<Personne> CREATOR = new Creator<Personne>() {
        @Override
        public Personne createFromParcel(Parcel in) {
            return new Personne(in);
        }

        @Override
        public Personne[] newArray(int size) {
            return new Personne[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(idEtudiant);
        parcel.writeInt(idBluetooth);
        parcel.writeStringList(connexion);
    }
}
