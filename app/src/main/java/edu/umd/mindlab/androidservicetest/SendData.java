package edu.umd.mindlab.androidservicetest;

import android.os.AsyncTask;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SendData extends AsyncTask<String, Void, String> {

    public final String URI = "https://40514f3c-a40f-454b-ab9f-fa1000e7ef01.mock.pstmn.io/post";

    @Override
    protected String doInBackground(String... params) {

        String data = "";
        HttpURLConnection httpURLConnection = null;

        try {

            httpURLConnection = (HttpURLConnection) new URL(URI).openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            wr.writeBytes(params[0]);
            wr.flush();
            wr.close();

            InputStream in = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(in);

            int inputStreamData = inputStreamReader.read();
            while (inputStreamData != -1) {
                char current = (char) inputStreamData;
                inputStreamData = inputStreamReader.read();
                data += current;
            }
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

    } // end onPostExecute

}// end sendData