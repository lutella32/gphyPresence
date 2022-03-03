package com.example.presence;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

/**
 * Classe personne (correspond à un étudiant)
 */
public class Personne implements Parcelable {
    // Personne possède un idEtudiant,un nom/prénom, un idTel et une liste de connexion en String
    private int idEtudiant; // numéro etudiant
    private String nomPrenom; // nom et prénom de l'étudiant
    private String idTel; // identifiant du téléphone
    private ArrayList<String> connexion; // liste des 10 dernières connexions de l'étudiant

    /**
     * Constructeur de personne
     */
    public Personne(){
        // initialisation des valeurs par défaut
        idTel ="0";
        idEtudiant=0;
        nomPrenom = "";
        connexion = new ArrayList<String>();
        connexion.add("null");

    }
    // code généré automatiquement par android studio
    protected Personne(Parcel in) {
        idEtudiant = in.readInt();
        idTel = in.readString();
        connexion = in.createStringArrayList();
    }

    /**
     * surchage de la méthode toStrong avec les getter
     * @return string des informations de la personne
     */
    public String toString(){
        StringBuilder sBuilder = new StringBuilder("\t Numéros Etudiant: " + this.getIdEtudiant() + "\n");
        sBuilder.append("\t Id tel: ").append(this.getIdTel()).append("\n");
        sBuilder.append("\t Nom Prenom: ").append(this.getNomPrenom()).append("\n");
        sBuilder.append("\t Connexion :").append(this.getConnexion()).append("\n");

        return sBuilder.toString();
    }

    /**
     * surchage de la méthode print
     */
    public void print(){
        System.out.println("Person's attributes: ");
        System.out.print(this);
        System.out.println();
    }

    /**
     * Getter idEtudiant
     * @return idEtudiant
     */
    public int getIdEtudiant() { return idEtudiant; }

    /**
     * Setter idEtudiant
     * @param num nouvel idEtudiant
     */
    public void setIdEtudiant(int num) { this.idEtudiant = num; }

    /**
     * Getter nomPrenom
     * @return string contenant le nom et le prénom de la personne
     */
    public String getNomPrenom() { return nomPrenom; }

    /**
     * Setter nomPrenom
     * @param nom nouveau nomPrenom
     */
    public void setNomPrenom(String nom) { this.nomPrenom = nom; }

    /**
     * Getter idTel
     * @return idTel
     */
    public String getIdTel() { return idTel; }

    /**
     * Setter idTel
     * @param id nouveau idTel
     */
    public void setIdTel(String id) { this.idTel = id; }

    /**
     * Getter liste des connexions
     * @return liste des connexions
     */
    public ArrayList<String> getConnexion() { return connexion; }

    /**
     * Setter liste des connexions
     * @param connexion nouvelle liste de connexions
     */
    public void setConnexion(ArrayList<String> connexion) { this.connexion = connexion; }

    /**
     * Method d'ajout d'une connexion à la liste
     * @param dateCo string de la date de la nouvelle connexion à ajoutée
     */
    public void addConnexion(String dateCo){
        //Si la connexion n'a pas été un echec
        if (dateCo != "echec") {
            // Si le nombre de connexion est déjà égal à 10
            if (connexion.size() == 10) {
                //On supprime la connexion la plus ancienne
                this.connexion.remove(0);
                // Puis on ajoute la nouvelle connexion à la liste
                this.connexion.add(dateCo);
            } else {
                // Sinon on ajoute directement la nouvelle connexion à la liste
                this.connexion.add(dateCo);
            }
        }
    }

    /**
     * code généré par android studio pour transmettre personne d'une page à l'autre
     */
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

    /**
     * code généré par android studio pour transmettre personne d'une page à l'autre
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * code généré par android studio pour transmettre personne d'une page à l'autre
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(idEtudiant);
        parcel.writeString(idTel);
        parcel.writeStringList(connexion);
    }
}
