package com.ceri.app.carlos_i;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity implements OnTouchListener {

    private static final String COMMAND_PRESS = "press";
    private static final String COMMAND_RELEASE = "release";

    private static final String DIRECTION_FORWARD = "forward";
    private static final String DIRECTION_BACKWARD = "backward";
    private static final String DIRECTION_LEFT = "left";
    private static final String DIRECTION_RIGHT = "right";

    private VideoView cameraView;

    private Button forwardButton;

    private Button backwardButton;

    private Button leftButton;

    private Button rightButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraView = (VideoView) findViewById(R.id.vv_camera);
        //initializeCamera();

        forwardButton = (Button) findViewById(R.id.bt_forward);
        forwardButton.setOnTouchListener(this);

        backwardButton = (Button) findViewById(R.id.bt_backward);
        backwardButton.setOnTouchListener(this);

        leftButton = (Button) findViewById(R.id.bt_left);
        leftButton.setOnTouchListener(this);

        rightButton = (Button) findViewById(R.id.bt_right);
        rightButton.setOnTouchListener(this);

    }

    private void initializeCamera() {

        try {

            String uriString = "http://10.122.1.67:9999/stream/test.webm";

            Uri uri = Uri.parse(uriString);

            MediaController controller = new MediaController(this);

            controller.setAnchorView(cameraView);

            cameraView.setMediaController(controller);

            Log.d("Carlos-I", "[MainActivity] setVideoURI: before");

            cameraView.setVideoURI(uri);

            Log.d("Carlos-I", "[MainActivity] setVideoURI: after");

            cameraView.requestFocus();

            cameraView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {

                    Log.d("Carlos-I", "[MainActivity] onPrepared");

                    cameraView.start();

                }

            });

        }
        catch (Exception e) {

            Log.d("Carlos-I", "[MainActivity] Exception: " + e.toString());

            e.printStackTrace();

        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        String command = detectAction(event);
        String direction = null;

        switch (v.getId()) {

            case R.id.bt_forward:

                direction = DIRECTION_FORWARD;

                break;

            case R.id.bt_backward:

                direction = DIRECTION_BACKWARD;

                break;

            case R.id.bt_left:

                direction = DIRECTION_LEFT;

                break;

            case R.id.bt_right:

                direction = DIRECTION_RIGHT;

                break;

        }

        if (command == null || direction == null) {

            return false;

        }

        CommandTask task = new CommandTask(this);

        task.execute(new String[] {command, direction});

        return false;

    }

    private String detectAction(MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:

                return COMMAND_PRESS;

            case MotionEvent.ACTION_UP:

                return COMMAND_RELEASE;

        }

        return null;

    }
}
