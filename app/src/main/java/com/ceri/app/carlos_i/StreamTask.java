package com.ceri.app.carlos_i;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Baptiste on 09/01/2018.
 * Cette classe definit la tache asynchrone qui permet la recuperation
 * Des images de maniere reguliere et continue
 */

public class StreamTask extends AsyncTask<Void, String, Void> {

    // Variable determinant si la tache est deja active
    public static boolean IS_RUNNING = false;

    // Constante definissant le protocole
    // Utilise pour la communication avec le Web Service
    private static final String PROTOCOL = "http";

    // Variable representant l'adresse IP du Web Service
    // Dans notre cas il s'agit du point d'acces WiFi
    private String IP_SERVER;

    // Port de communication
    private static final String PORT = "8080";

    // Schema de l'URL permettant l'envoi de commande
    private static final String SCHEME = "stream";

    // Constante definissant la vitesse de recuperation des images
    private static final long FRAME_SPEED = 100;

    // Methode de la requete HTTP
    private static final String REQUEST_METHOD = "GET";

    // Contexte d'execution de la tache asynchrone
    private Context context;

    // Representation Java du composant XML
    // Ayant pour tache d'afficher les images recuperer
    private ImageView cameraView;

    /**
     * Constructeur
     * @param context Contexte d'execution de la tache
     * @param cameraView Representation Java du composant XML permettant d'afficher les images
     */
    public StreamTask(Context context, ImageView cameraView) {

        // On valorise le contexte d'execution
        this.context = context;

        // On valorise le composant Java ImageView
        this.cameraView = cameraView;

        // On aurait aime utilise une recuperation d'IP dynamique
        // Mais on n'a pas reussi
        // Cette methode permet de recuperer l'adresse IP du point d'acces WiFi
        //this.IP_SERVER = ConnectionManager.getGatewayIPAddress(this.context);

        // Adresse IP valorisee en dur
        this.IP_SERVER = "10.3.141.1";

    }

    /**
     * Methode appellee automatiquement par les systeme
     * Lors de l'appel a la tache asynchrone
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        // On définit la variable indiquant que la tache est en cours d'execution
        IS_RUNNING = true;

    }

    /**
     * Methode appellee automatiquement par le systeme
     * Lors de l'execution d'une tache asynchrone
     * @param voids Aucun parametre requis
     * @return Retourne une valeur nulle
     */
    @Override
    protected Void doInBackground(Void... voids) {

        Log.d("Carlos-I", "[StreamTask] doInBackground");

        // On construit la chaine de caracteres de l'URL
        String strUrl = PROTOCOL + "://" + this.IP_SERVER + ":" + PORT + "/" + SCHEME;

        Log.d("Carlos-I", "[StreamTask] URL: " + strUrl);

        try {

            // On envoie la requete et on recupere la reponse du serveur
            String response = sendRequest(strUrl);

            // On boucle tant que l'on arrive a recuperer une image depuis le serveur
            while (response != null) {

                // On indique au systeme que la tache a progresse
                // Ce trick permet de sortir du thread cree par la tache
                // Et de communiquer l'image au composant qui permettra son affichage
                publishProgress(new String[] {response});

                // On patiente le temps definit avant la prochaine onterrogation
                Thread.sleep(FRAME_SPEED);

                // On envoie la requete et on recupere la reponse du serveur
                response = sendRequest(strUrl);

            }

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
     * Methode appellee automatiquement par le systeme
     * Lorque que l'on souhaite notifier au systeme l'avancement de la tache
     * @param values Contient l'image au format base64
     */
    @Override
    protected void onProgressUpdate(String... values) {

        // On genere un tableau d'octets a partir de l'image en base64
        byte[] bytes = Base64.decode(values[0], Base64.DEFAULT);

        // On decode ce tableau de facon a obtenir un bitmap de l'image
        Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        // On renseigne cette image au composant Java qui s'occupera de l'afficher
        this.cameraView.setImageBitmap(image);

    }

    /**
     * Methode appellee automatiquement par le systeme
     * Lorsque le traitement de la tache est termine
     * On retourne dans le thread precedent
     * Dans notre cas, cette fonction sera atteinte lorsque l'appareil n'arrivera pas
     * A recuperer une image depuis le serveur (perte de connexion ou autre soucis)
     * @param aVoid Aucun parametre
     */
    @Override
    protected void onPostExecute(Void aVoid) {

        // On indique que la tache est terminee
        IS_RUNNING = false;

        // On recupere l'image par defaut de l'ecran de streaming
        //Image d'erreur
        Drawable drawable  = this.context.getResources().getDrawable(R.drawable.intermission);

        // On renseigne cette image au composant Java qui s'occupera de l'afficher
        this.cameraView.setImageDrawable(drawable);

    }

    /**
     * Cette methode permet d'envoyer une requete au Web Service a partir d'une URL
     * @param strUrl Chaine de caracteres representant l'URL a interroger
     * @return Retourne l'image en base64, sinon une valeur nulle en cas d'erreur
     * @throws Exception On redirige une eventuelle exception
     */
    private String sendRequest(String strUrl) throws Exception {

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

            Log.d("Carlos-I", "[StreamTask] ResponseCode: HTTP_OK (" + code +")");

            // On convertit le flux recupere en chaine de caracteres
            return convertStreamToString(connection.getInputStream());

        }
        else {

            Log.d("Carlos-I", "[StreamTask] ResponseCode: " + code);

        }

        // En cas d'erreur on retourne une valeur nulle
        return null;

    }

    /**
     * Methode permettant de convertir un flux en chaine de caracteres
     * @param is Flux concerne par la conversion
     * @return Retourne la chaine de caracteres correspondante au flux d'entree
     */
    private static String convertStreamToString(InputStream is) {

        // On utilise un objet scanner, petit trick Java
        Scanner scanner = new Scanner(is).useDelimiter("\\A");

        // On lit l'ensemble du flux
        return scanner.hasNext() ? scanner.next() : "";

    }

}
