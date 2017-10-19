package edu.umd.mindlab.androidservicetest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.telephony.TelephonyManager;
import android.os.Build;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.json.JSONException;
import org.json.JSONObject;

public class GetPersonalInfo extends AppCompatActivity implements TaskCompleted {

    private EditText fname;
    private EditText lname;
    private EditText dob;
    private EditText birthCity;
    private EditText uid;
    private Button infoSubmit;

    private String luid;
    private String first_name;
    private String last_name;

    private int verifyAttempts = 0;

    private final String TAG = "GetPersonalInfo";

    private static final String LUID_STORE = "The_LUID_is_stored";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_personal_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fname = (EditText) findViewById(R.id.firstNameEdit);
        lname = (EditText) findViewById(R.id.lastNameEdit);
        dob = (EditText) findViewById(R.id.dobEdit);
        birthCity = (EditText) findViewById(R.id.birthCityEdit);
        uid = (EditText) findViewById(R.id.uidEdit);
        infoSubmit = (Button) findViewById(R.id.submitPersInfo);

        infoSubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                first_name = fname.getText().toString();
                last_name = lname.getText().toString();
                String birth_date = dob.getText().toString();
                String birth_city = birthCity.getText().toString();
                String UID = uid.getText().toString();

                LoggedIn log = LoggedIn.getLog();
                log.setName(first_name + " " + last_name);

                String infoToHash = last_name + birth_date + UID;
                infoToHash = infoToHash.replace(" ", "");

                // this method will use the hash function and return the LUID. Passing the info as a concatenated string
                luid = getLUID(infoToHash);

                Log.v(TAG, "Info to Hash: " + infoToHash);
                Log.v(TAG, "Hash: " + luid);

                // this method will gather the device about the phone and package it (with LUID) in a JSON
                JSONObject infoJSON = getDeviceInfo(luid);

                // if something is wrong, check that this is correct
                (new SendInfo(GetPersonalInfo.this)).execute(infoJSON);

            }
        });



        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }); */
    }

    // This is the method where the info is hashed and the LUID is retured.
    public String getLUID(String strToHash){

        String generatedHash = null;
        StringBuilder sb = null;

        try {

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            // I think maybe I should have the "UTF-8" argument as well, but for now, na
            byte[] bytes = md.digest(strToHash.getBytes());

            sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++){
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            generatedHash = sb.toString();

        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }

        return generatedHash;

    }

    public void storeLUID(String LUID){

        SharedPreferences.Editor editor = getSharedPreferences("edu.umd.mindlab.androidservicetest", MODE_PRIVATE).edit();
        editor.putString(LUID_STORE, LUID);
        editor.commit();

    }

    public JSONObject getDeviceInfo(String luid){

        String serviceName = Context.TELEPHONY_SERVICE;
        TelephonyManager m_telephonyManager = (TelephonyManager) getSystemService(serviceName);
        //String IMEI = m_telephonyManager.getDeviceId();
        String deviceID =  Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        int simState = m_telephonyManager.getSimState();
        //String serviceProvider = m_telephonyManager.getSimOperatorName();
        String serviceProvider = m_telephonyManager.getNetworkOperatorName();
        Integer API_Level = Build.VERSION.SDK_INT;
        // not sure if it's necessary to cast this to a string, but I'll do it for now.
        String apiLevel = API_Level.toString();
        String device = android.os.Build.DEVICE;
        String model = android.os.Build.MODEL;
        String product = android.os.Build.PRODUCT;

        JSONObject infoJSON = new JSONObject();
        try {
            infoJSON.put("LUID", luid);
            infoJSON.put("deviceID", deviceID);
            infoJSON.put("deviceInfo", "Make: " + device + ", Model: " + model + ", Network Provider: " + serviceProvider + ", API: " + apiLevel);
        } catch (JSONException e) {
            Log.e(TAG, "JSON problem");
        }

        return infoJSON;
    }

    @Override
    public void onBackPressed(){
        LoggedIn log = LoggedIn.getLog();
        log.setMain(true);

        super.onBackPressed();
    }

    @Override
    public void onTaskCompleted(String result) {

        if (result == null){

            Log.v(TAG, "The result was null. Something is wrong.");

        } else if(result.contains("LUID")){
            // if the LUID was valid go to the consent activity

            Log.v(TAG, "The result correct.");

            storeLUID(luid);
            luid = "";

            Intent consentIntent = new Intent(this, ConsentActivity.class);
            startActivity(consentIntent);

        } else{
            // if the result was not valid, ask them to try again

            if (verifyAttempts > 0) {
                // if after trying again, it still doesn't work. Ask them to see a project manager.

                Toast.makeText(this, "Please see project director for assistance", Toast.LENGTH_LONG).show();

            }  else{

                Toast.makeText(this, "Verification failed. Please check info and try again.", Toast.LENGTH_SHORT).show();
                verifyAttempts++;
            }

        }

    }

}
