package edu.umd.mindlab.androidservicetest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import org.json.JSONException;
import org.json.JSONObject;

public class GetPersonalInfo extends AppCompatActivity implements TaskCompleted {

    private EditText fname;
    private EditText lname;
    private EditText dob;
    private EditText birthCity;
    private EditText uid;
    private Button infoSubmit;

    private String first_name;
    private String last_name;
    private String birth_date;
    private String birth_city;
    private String UID;

    private String luid;

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
                birth_date = dob.getText().toString();
                birth_city = birthCity.getText().toString();
                UID = uid.getText().toString();

                // this method will use the hash function and return the LUID
                luid = getLUID(first_name, last_name, birth_date, birth_city, UID);

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
    public String getLUID(String first, String last, String dob, String birthD, String birthC){

        return "ThisIsAFakeLUID";

    }

    public void storeLUID(String LUID){

        SharedPreferences.Editor editor = getSharedPreferences("edu.umd.mindlab.androidservicetest", MODE_PRIVATE).edit();
        editor.putString(LUID_STORE, LUID);
        editor.commit();

    }

    public JSONObject getDeviceInfo(String luid){

        String serviceName = Context.TELEPHONY_SERVICE;
        TelephonyManager m_telephonyManager = (TelephonyManager) getSystemService(serviceName);
        String IMEI = m_telephonyManager.getDeviceId();
        int simState = m_telephonyManager.getSimState();
        String serviceProvider = m_telephonyManager.getSimOperatorName();
        int API_Level = Build.VERSION.SDK_INT;
        String device = android.os.Build.DEVICE;
        String model = android.os.Build.MODEL;
        String product = android.os.Build.PRODUCT;

        JSONObject infoJSON = new JSONObject();
        try {
            infoJSON.put("LUID", luid);
            infoJSON.put("deviceID", IMEI.toString());
            infoJSON.put("deviceInfo", "Make: " + device + ", Model: " + model + ", Network Provider: " + serviceProvider + ", API: " + IMEI.toString());
        } catch (JSONException e) {
            Log.e(TAG, "JSON problem");
        }

        return infoJSON;
    }

    @Override
    public void onTaskCompleted(String result) {

        Intent consentIntent = new Intent(this, ConsentActivity.class);
        startActivity(consentIntent);

       /* if (result == "good"){

            storeLUID(luid);

            // actually I think if I just ceate these variables in this method, they should not exist later
            first_name = "";
            last_name = "";
            birth_date = "";
            birth_city = "";
            UID = "";



        } else{

            if (verifyAttempts > 0) {

                Toast.makeText(this, "Please see project director for assistance", Toast.LENGTH_LONG).show();

            }  else{

                Toast.makeText(this, "Verification failed. Please check info and try again.", Toast.LENGTH_SHORT).show();

            }

        } */

    }

}
