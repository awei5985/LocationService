package edu.umd.mindlab.androidservicetest;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SendLogInfo extends AsyncTask<JSONObject, Void, String> {

    // public final String URI = "https://safe-scrubland-41744.herokuapp.com/";
    public final String URI = "https://obscure-ridge-13374.herokuapp.com/login";
    public final String TAG = "SendLogInfo";

    private Context mContext;
    private TaskCompleted mCallback;

    public SendLogInfo(Context context) {
        this.mContext = context;
        this.mCallback = (TaskCompleted) context;
    }

        @Override
    protected String doInBackground(JSONObject... json) {

        String data = "";
        HttpURLConnection httpURLConnection = null;

        try {

            httpURLConnection = (HttpURLConnection) new URL(URI).openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            if (json != null && json.length > 0) {
                Log.i(TAG, "ID is " + json[0].toString());
                wr.writeBytes(json[0].toString());
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

            Log.i(TAG, "Data gotten from Login Server: " + data);
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
        mCallback.onTaskCompleted(result);

    } // end onPostExecute

}// end sendData