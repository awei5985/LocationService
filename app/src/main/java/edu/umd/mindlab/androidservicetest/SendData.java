package edu.umd.mindlab.androidservicetest;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SendData extends AsyncTask<JSONObject, Void, String> {

    // public final String URI = "https://safe-scrubland-41744.herokuapp.com/";
    public final String URI = "http://rovermind.cs.umd.edu:8080/LocationServercont2/ContFindLocation/";
    public final String TAG = "SendData";

    @Override
    protected String doInBackground(JSONObject... jObjs) {

        String data = "";
        HttpURLConnection httpURLConnection = null;

        try {

            httpURLConnection = (HttpURLConnection) new URL(URI).openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            if (jObjs != null && jObjs.length > 0) {
                final JSONObject realjObject = jObjs[0];
                Log.i(TAG, realjObject.toString());
                wr.writeBytes(realjObject.toString());
            }
            wr.flush();
            wr.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (httpURLConnection.getInputStream())));

            String output;
            StringBuilder response = new StringBuilder();
            while ((output = br.readLine()) != null) {
                response.append(output);
                response.append('\r');
            }
            data = response.toString();
            httpURLConnection.disconnect();

            Log.i(TAG, "Data gotten: " + data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        } // end try-catch

        return data;

    } // end doInBackground

    @Override
    protected void onPostExecute(String result) {

        Log.i(TAG, result);

    } // end onPostExecute

}// end sendData