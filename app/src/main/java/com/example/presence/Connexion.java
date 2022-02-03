// Imports
package com.example.presence;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;

/**
 * Classe connexion : classe permettant la création d'une socket bluetooth entre
 * le smartphone (client) et l'ordinateur avec script de connexion (serveur).
 * La socket bluetooth permet l'échange de message entre le client et le serveur
 */
public class Connexion extends Thread{

    private final BluetoothSocket mmSocket; // la socket est le canal de communication entre le client et le serveur
    private final BluetoothDevice mmDevice; // le device est l'appareil bluetooth auquel on souhaite se connecté (l'adaptateur bluetooth lié au serveur)
    private String TAG = "Classe connexion"; // TAG pour les logs de la classe

    /**
     * Constructeur de la classe connexion : création d'une socket entre le client et le serveur
     * @param device apprareil auquel on se connecte (ordinateur serveur via adaptateur bluetooth)
     * @param personne objet personne contenant les informations de l'étudiant qui seront transmises par le message
     */
    public Connexion(BluetoothDevice device, Personne personne) {
        // Utilisation d'un objet socket temporaire qui sera plus tard assigné à mmSocket
        BluetoothSocket tmp = null;
        //Appareil
        mmDevice = device;

        //Creation de la socket sur le port n°5 (les ports bluetooth sont les ports des numéros 1 à 30)
        try {
            tmp = (BluetoothSocket) mmDevice.getClass().getMethod("createRfcommSocket", new Class[] { int.class } ).invoke(device, 5);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        // on assigne la socket à la variable mmSocket
        mmSocket = tmp;
        Log.d(TAG,"création socket OK");

        //Récupération du message retour retourner par la fonction run
        String messageRetour = run(personne.getIdEtudiant(), personne.getIdTel());
        Log.d(TAG, messageRetour);

        //Suppression des caractères supplémentaires du message retour
        // Tous les messages retours sont envoyés avec un point virgule à la fin
        // car des caractères non souhaitées s'ajoutent à la suite du message
        // on fait donc un split sur le ';' pour supprimer les caractères non souahités
        String[] listeRetour = messageRetour.split(";");
        messageRetour = listeRetour[0];
        Log.d(TAG, messageRetour);

        //Recherche cas de message retour (2 types de messages peuvent être reçu selon la situation):
        // cas1 : la personne ne s'est jamais connectée => réception de son nom et prénom pour confirmer son identité
        // cas2 : personne déjà connectée => réception de son horaire de connexion à enregistrer dans la liste des connexions

        //Si message retour différent de "echec"
        if (messageRetour!="echec") {
            // On regarde avec une regex si le message est une date et heure de connexion
            boolean check = messageRetour.matches("^[0-9]+.*");
            Log.d("Message Date", String.valueOf(check));

            // Si le message est une date et heure
            if (check) {
                //On ajoute le message dans la liste des connexions de l'étudiant
                personne.addConnexion(messageRetour);
                personne.print();
            } else { // Sinon le message est le nom et le prenom
                //donc on met à jour le nom et le prénom de la personne
                personne.setNomPrenom(messageRetour);
                personne.print();
            }
        }else{ // Si message retour est "echec" c'est que l'échange de message n'a pas fonctionné
            Log.d("Message retour", "echec donc aucune action sur l'application");
        }
    }

    /**
     * Methode run : permet la connexion de la socket et l'échange de message
     * @param numEtu numero de l'etudiant
     * @param numID numero d'identification du telephone
     * @return message retour reçu envoyé par le serveur
     */
    public String run(int numEtu, String numID) {

        try {
            // Connexion de l'appareil grace à la socket
            mmSocket.connect();
            Log.d(TAG,"connecté");
        } catch (IOException connectException) {
            // Connexion à la socket impossible, on ferme la socket
            Log.e(TAG,  "échec de connexion", connectException);
            try {
                mmSocket.close();
                Log.d(TAG,"socket fermée");
            } catch (IOException closeException) {
                Log.e(TAG, "impossible de fermer la socket", closeException);
            }
            //Si la connexion n'a pas fonctionnée on retourne le message "echec"
            Log.d("MessageRetour:", "echec");
            return ("echec");
        }

        // La connexion a fonctionné, on peut échanger des messages
        Log.d(TAG,"envoi message");

        //Création d'un thread dans la socket pour envoi et réception de message
        ConnectedThread connexionEnvoi = new ConnectedThread(mmSocket);

        //Ecriture du message et conversion en byte (format d'envoi des messages par bluetooth)
        String inputString = Integer.toString(numEtu)+";"+numID;
        byte[] byteArray = inputString.getBytes();
        Log.d("Message envoyé", inputString);
        // Envoi du message en byte
        connexionEnvoi.write(byteArray);

        //Réception du message réponse envoyé par le serveur
        String messageRetour = connexionEnvoi.reception();
        Log.d("Message reçu", messageRetour);

        //Fermeture de la socket
        connexionEnvoi.cancel();

        // On retourne le message reçu
        return messageRetour;
    }

    /**
     * Classe ConnectedThread : classe privée permettant la création d'un thread sur la socket
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket; // socket
        private final InputStream mmInStream; // flux de message reçu
        private final OutputStream mmOutStream; // flux de message envoyé
        private byte[] mmBuffer; // mmBuffer stocke les données du flux

        /**
         * Constructeur de la classe ConnectedThread : création d'un thread
         * @param socket //socket
         */
        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket; //socket

            //Utilisation d'objet temporaire avant assignation sur mmInStream et mmOutStream
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Récupération des fluxs entrant et sortant (InputStream, OutputStream)
            try {
                tmpIn = socket.getInputStream();
                Log.d(TAG, "Input stream OK");
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
                Log.d(TAG, "Output stream OK");
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            //Assignation des fluxs
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        /**
         * Method reception : permet la reception d'un message
         * @return retourne le message reçu
         */
        public String reception() {
            String result = null;
            mmBuffer = new byte[1024]; //format des bytes dans le buffer
            int numBytes; // bytes retournés à la lecture du message

            // Ecoute constante de l'input stream jusqu'à ce qu'on recoive un message ou qu'une exception se produise
            while (true) {
                try {
                    // Lecture du flux entrant
                    numBytes = mmInStream.read(mmBuffer);
                    //Conversion du message reçu (de bytes en string)
                    result = new String(mmBuffer, StandardCharsets.UTF_8);
                    Log.d("Message retour", result);
                    break;
                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    result = "echec";
                    break;
                }
            }
            if (result == null) {
                result = "echec";
            }
            return result;
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
                Log.d(TAG, "Message envoyé");

            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);

            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
                Log.d(TAG, "Socket close");
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }
}
