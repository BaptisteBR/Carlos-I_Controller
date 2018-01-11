package com.ceri.app.carlos_i;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * Created by Baptiste on 21/11/2017.
 * Activite representant l'unique ecran de l'application
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnTouchListener {

    // Variables definissant les evenements sur les boutons
    private static final String COMMAND_PRESS = "press";
    private static final String COMMAND_RELEASE = "release";

    // Variable definisant les directions
    private static final String DIRECTION_FORWARD = "forward";
    private static final String DIRECTION_BACKWARD = "backward";
    private static final String DIRECTION_LEFT = "left";
    private static final String DIRECTION_RIGHT = "right";

    // Objet Java representant un composant XML VideoView
    // Utilise pour afficher le flux de streaming video
    //private VideoView cameraView;

    // Objet Java representant un composant XML ImageView
    // Utilise pour afficher les frames d'images
    private ImageView cameraView;

    // Objet Java representant un composant XML Button
    // Utilise pour rafraichir le streaming
    private Button refreshButton;

    // Objet Java representant un composant XML Button
    // Utilise pour diriger le robot vers l'avant
    private Button forwardButton;

    // Objet Java representant un composant XML Button
    // Utilise pour diriger le robot vers l'arriere
    private Button backwardButton;

    // Objet Java representant un composant XML Button
    // Utilise pour diriger le robot vers la gauche
    private Button leftButton;

    // Objet Java representant un composant XML Button
    // Utilise pour diriger le robot vers la droite
    private Button rightButton;

    /**
     * Methode du cycle de vie d'une Activite
     * Appelee automatiquement par le systeme
     * @param savedInstanceState Etat de l'ectivite
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Associe la vue XML a l'activite
        setContentView(R.layout.activity_main);

        // On associe le composant XML VideoView avec sa representation Java
        //this.cameraView = (VideoView) findViewById(R.id.vv_camera);

        // On associe le composant XML ImageView avec sa representation Java
        this.cameraView = (ImageView) findViewById(R.id.iv_camera);

        // On initialise le streaming video
        initializeCamera();

        // On associe le composant XML Button avec sa representation Java
        this.refreshButton = (Button) findViewById(R.id.bt_refresh);
        // On definit un listener sur ce bouton, pour l'action Click
        this.refreshButton.setOnClickListener(this);

        // On associe le composant XML Button avec sa representation Java
        this.forwardButton = (Button) findViewById(R.id.bt_forward);
        // On definit un listener sur ce bouton, pour l'action Touch
        this.forwardButton.setOnTouchListener(this);

        // On associe le composant XML Button avec sa representation Java
        this.backwardButton = (Button) findViewById(R.id.bt_backward);
        // On definit un listener sur ce bouton, pour l'action Touch
        this.backwardButton.setOnTouchListener(this);

        // On associe le composant XML Button avec sa representation Java
        this.leftButton = (Button) findViewById(R.id.bt_left);
        // On definit un listener sur ce bouton, pour l'action Touch
        this.leftButton.setOnTouchListener(this);

        // On associe le composant XML Button avec sa representation Java
        this.rightButton = (Button) findViewById(R.id.bt_right);
        // On definit un listener sur ce bouton, pour l'action Touch
        this.rightButton.setOnTouchListener(this);

    }

    /**
     * Methode permettant l'initialisation de la video
     */
    private void initializeCamera() {

        // Demarre le streaming video, en utilisant un flux
        //launchVideoStreaming();

        // Demarre le streaming video, en utilisant des frames d'images
        launchImageFramesStreaming();

    }

    /**
     * Methode executant la recuperation des frames d'images
     */
    private void launchImageFramesStreaming() {

        // Creation d'une tache asynchrone
        StreamTask task = new StreamTask(this, this.cameraView);

        // Execution d'une tache asynchrone
        // On utilise 'executeOnExecutor' pour pouvoir effectuer deux tache en parallele
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    /**
     * Methode permettant la recuperation du flux video
     * Abandonnee car des problemes d'encodage sont rencontres
     */
    private void launchVideoStreaming() {
        /*
        try {

            String uriString = "http://10.122.1.67:9999/stream/test.webm";

            Uri uri = Uri.parse(uriString);

            MediaController controller = new MediaController(this);

            controller.setAnchorView(cameraView);

            this.cameraView.setMediaController(controller);

            Log.d("Carlos-I", "[MainActivity] setVideoURI: before");

            this.cameraView.setVideoURI(uri);

            Log.d("Carlos-I", "[MainActivity] setVideoURI: after");

            this.cameraView.requestFocus();

            this.cameraView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {

                    Log.d("Carlos-I", "[MainActivity] onPrepared");

                    this.cameraView.start();

                }

            });

        }
        catch (Exception e) {

            Log.d("Carlos-I", "[MainActivity] Exception: " + e.toString());

            e.printStackTrace();

        }
        */
    }

    /**
     * Methode capturant les evenements Touch
     * @param v Vue concernee par l'evenement
     * @param event Evenement
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        // On detecte l'action effectue sur le composant
        String command = detectAction(event);

        // On instancie la direction avec une valeur nulle
        String direction = null;

        // On determine le composant concerne par l'evenement
        switch (v.getId()) {

            // Bouton de direction FORWARD
            case R.id.bt_forward:

                // On valorise la direction
                direction = DIRECTION_FORWARD;

                break;

            // Bouton de direction BACKWARD
            case R.id.bt_backward:

                // On valorise la direction
                direction = DIRECTION_BACKWARD;

                break;

            // Bouton de direction LEFT
            case R.id.bt_left:

                // On valorise la direction
                direction = DIRECTION_LEFT;

                break;

            // Bouton de direction RIGHT
            case R.id.bt_right:

                // On valorise la direction
                direction = DIRECTION_RIGHT;

                break;

        }

        // Si l'on a pas reussi a determiner l'evenement ou la direction
        if (command == null || direction == null) {

            // On termine la methode
            return false;

        }

        // Creation d'une tache asynchrone
        CommandTask task = new CommandTask(this);

        // Execution d'une tache asynchrone
        // On utilise 'executeOnExecutor' pour pouvoir effectuer deux tache en parallele
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[] {command, direction});

        // On termine la methode
        return false;

    }

    /**
     * Methode permettant de definir l'evenement Touch
     * @param event Evenement
     * @return Nom de l'evenement
     */
    private String detectAction(MotionEvent event) {

        // On determine l'action correspondant a l'evenement
        switch (event.getAction()) {

            // Action d'appuie sur un composant
            case MotionEvent.ACTION_DOWN:

                // On retourne le nom de l'evenement
                return COMMAND_PRESS;

            // Action de relachement sur un composant
            case MotionEvent.ACTION_UP:

                // On retourne le nom de l'evenement
                return COMMAND_RELEASE;

        }

        // Dans le cas ou l'on arrive pas a determiner l'evenement
        // On retourne une valeur nulle
        return null;

    }

    /**
     * Methode capturant les evenement Click
     * @param v Vue concernee par l'evenement
     */
    @Override
    public void onClick(View v) {

        // On determine la vue concernee par l'evenement
        switch (v.getId()) {

            // Bouton de rafraichissement
            case R.id.bt_refresh:

                // Si le flux streaming est deja en cours d'execution
                if (!StreamTask.IS_RUNNING) {

                    // On initialise de nouveau le streaming
                    initializeCamera();

                }

                break;

        }

    }

}
