package com.ceri.app.carlos_i;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Baptiste on 22/11/2017.
 */

public class CommandTask extends AsyncTask<String, Void, Void> {

    private static final String PROTOCOL = "http";

    private String IP_SERVER;

    private static final String PORT = "8080";

    private static final String SCHEME = "command";

    public CommandTask(Context context) {

        this.IP_SERVER = ConnectionManager.getGatewayIPAddress(context);
        //this.IP_SERVER = "10.3.141.1";

    }

    @Override
    protected Void doInBackground(String... strings) {

        Log.d("Carlos-I", "[CommandTask] doInBackground");

        String command = strings[0];
        String direction = strings[1];

        String strUrl = PROTOCOL + "://" + this.IP_SERVER + ":" + PORT + "/" + SCHEME + "/"
                        + command + "/" + direction;

        Log.d("Carlos-I", "[CommandTask] URL: " + strUrl);

        try {

            //sendRequest(strUrl);

        }
        catch (Exception e) {

            e.printStackTrace();

        }
        finally {

            return null;

        }

    }

    private void sendRequest(String strUrl) throws Exception {

        int code;

        URL url = new URL(strUrl);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setAllowUserInteraction(false);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestMethod("GET");

        connection.connect();

        code = connection.getResponseCode();

        if (code == HttpURLConnection.HTTP_OK) {

            Log.d("Carlos-I", "[CommandTask] ResponseCode: HTTP_OK (" + code +")");

        }
        else {

            Log.d("Carlos-I", "[CommandTask] ResponseCode: " + code);

        }

    }

}
