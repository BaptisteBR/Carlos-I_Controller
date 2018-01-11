package com.ceri.app.carlos_i;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Baptiste on 22/11/2017.
 * Cette classe definit la tache asynchrone qui permet l'envoi
 * De commandes de direction au Web Service
 */

public class CommandTask extends AsyncTask<String, Void, Void> {

    // Constante definissant le protocole
    // Utilise pour la communication avec le Web Service
    private static final String PROTOCOL = "http";

    // Variable representant l'adresse IP du Web Service
    // Dans notre cas il s'agit du point d'acces WiFi
    private String IP_SERVER;

    // Port de communication
    private static final String PORT = "8080";

    // Schema de l'URL permettant l'envoi de commande
    private static final String SCHEME = "command";

    // Methode de la requete HTTP
    private static final String REQUEST_METHOD = "GET";

    /**
     * Constructeur
     * @param context Contexte d'execution de la tache
     */
    public CommandTask(Context context) {

        // On aurait aime utilise une recuperation d'IP dynamique
        // Mais on n'a pas reussi
        // Cette methode permet de recuperer l'adresse IP du point d'acces WiFi
        //this.IP_SERVER = ConnectionManager.getGatewayIPAddress(context);

        // Adresse IP valorisee en dur
        this.IP_SERVER = "10.3.141.1";

    }

    /**
     * Methode appellee automatiquement par le systeme
     * Lors de l'execution d'une tache asynchrone
     * @param strings Representent les deux parametres de la commande, evenement et direction
     * @return Retourne une valeur nulle
     */
    @Override
    protected Void doInBackground(String... strings) {

        Log.d("Carlos-I", "[CommandTask] doInBackground");

        // On recupere l'evenement (PRESS ou RELEASE)
        String command = strings[0];
        // On recupere la direction
        String direction = strings[1];

        // On construit la chaine de caracteres de l'URL
        String strUrl = PROTOCOL + "://" + this.IP_SERVER + ":" + PORT + "/" + SCHEME + "/"
                        + command + "/" + direction;

        Log.d("Carlos-I", "[CommandTask] URL: " + strUrl);

        try {

            // On envoie la requete au Web Service
            sendRequest(strUrl);

        }
        // On capture une eventuelle exception
        catch (Exception e) {

            // On affiche cette exception dans le logcat
            e.printStackTrace();

        }
        finally {

            // Finalement on retourne une valeur nulle
            return null;

        }

    }

    /**
     * Cette methode permet d'envoyer une requete au Web Service a partir d'une URL
     * @param strUrl Chaine de caracteres representant l'URL a interroger
     * @throws Exception On redirige une eventuelle exception
     */
    private void sendRequest(String strUrl) throws Exception {

        // Code de retour HTTP
        int code;

        // Creation de l'URL a partir de la chaine de caracteres
        URL url = new URL(strUrl);

        // Ouverture d'une connexion HTTP a partir de l'URL
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // On renseigne differents parametres de configuration
        connection.setAllowUserInteraction(false);
        connection.setInstanceFollowRedirects(true);

        // On definit le mode d'envoi de la requete
        connection.setRequestMethod(REQUEST_METHOD);

        // On envoie la requete au serveur
        connection.connect();

        // On recupere le code de retour de la connexion
        code = connection.getResponseCode();

        // Si la requete a bien ete envoyee
        if (code == HttpURLConnection.HTTP_OK) {

            Log.d("Carlos-I", "[CommandTask] ResponseCode: HTTP_OK (" + code +")");

        }
        else {

            Log.d("Carlos-I", "[CommandTask] ResponseCode: " + code);

        }

    }

}
