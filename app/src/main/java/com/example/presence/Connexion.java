package com.example.presence;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ViewDebug;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Connexion extends Thread{
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private String TAG = "Connexion";

    public Connexion(BluetoothDevice device, int numEtu, String numID, Personne personne) {
        // Use a temporary object that is later assigned to mmSocket
        // because mmSocket is final.
        BluetoothSocket tmp = null;

        mmDevice = device;
        try {
            tmp = (BluetoothSocket) mmDevice.getClass().getMethod("createRfcommSocket", new Class[] { int.class } ).invoke(device, 5);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        mmSocket = tmp;
        Log.d(TAG,"création socket OK");
        String messageRetour = run(numEtu, numID);
        Log.d(TAG, messageRetour);

        //Suppression des caractères supplémentaires
        String[] listeRetour = messageRetour.split(";");
        messageRetour = listeRetour[0];
        Log.d(TAG, messageRetour);

        //Recherche cas :
        // cas1 : la personne ne s'est jamais connectée => réception de son nom et prénom pour confirmer son identité
        // cas : personne déjà connecter => réception de son time_tag de connexion a enregistrer dans la liste des connexions

        boolean check = messageRetour.matches("^[0-9]+");

//        if (check){
//
//        }else{
//            //todo
//        }
        personne.addConnexion(messageRetour);
        personne.print();
        personne.print();

    }

    public String run(int numEtu, String numID) {

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mmSocket.connect();
            Log.d(TAG,"connecté");
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            Log.e(TAG,  "échec de connexion", connectException);
            try {
                mmSocket.close();
                Log.d(TAG,"socket fermée");
            } catch (IOException closeException) {
                Log.e(TAG, "Impossible de fermer la socket", closeException);
            }
            Log.d("MessageR", "echec");
            return ("echec");

        }

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
        Log.d(TAG,"envoi message");
        ConnectedThread connexionEnvoi = new ConnectedThread(mmSocket);

        String inputString = Integer.toString(numEtu)+";"+numID;
        Log.d("Message", inputString);
        byte[] byteArray = inputString.getBytes();

        connexionEnvoi.write(byteArray);
        String messageRetour = connexionEnvoi.reception();
        Log.d("messageRetour retourner", messageRetour);
        connexionEnvoi.cancel();

        return messageRetour;
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream
        //private Handler handler; // handler that gets info from Bluetooth service
        //private Message readMsg;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
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

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public String reception() {
            String result = null;
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    Log.d("Message retour", String.valueOf(numBytes));
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
