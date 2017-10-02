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

import android.telephony.TelephonyManager;
import android.os.Build;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements TaskCompleted{

    private Button loginButton;
    private EditText directoryID;
    private EditText password;

    private String TAG = "Login Activity";

    private String tAccept;
    private static final String A_TERMS = "Terms_Accepted";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // this is strictly for testing purposes and I will remove it after done with testing
        if (true){
            SharedPreferences.Editor editor = getSharedPreferences("edu.umd.mindlab.androidservicetest", MODE_PRIVATE).edit();
            editor.putString(A_TERMS, "Terms do not exist");
            editor.commit();
        }

        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loginButton = (Button) findViewById(R.id.mainButton);
        directoryID = (EditText) findViewById(R.id.editID);
        password = (EditText) findViewById(R.id.editPassword);

        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                JSONObject logJSON = new JSONObject();
                try{
                    logJSON.put("id", directoryID.getText().toString());
                    logJSON.put("pass", password.getText().toString());
                }catch(JSONException e){
                    Log.e(TAG, "JSON is Wrong");
                }

                (new SendLogInfo(LoginActivity.this)).execute(logJSON);

                // testing the countDownTimer
                Intent emailIntent = new Intent(v.getContext(), SendEmail.class);
                startActivity(emailIntent);


            }
        });

   //     FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    //    fab.setOnClickListener(new View.OnClickListener() {
    //        @Override
    //        public void onClick(View view) {
    //            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
    //                    .setAction("Action", null).show();
    //        }
    //    });

    }

    @Override
    public void onTaskCompleted(String result) {

        Log.v(TAG, "The GUID has been received......." + result);

        // TO DO: store the GUID

        // If a GUID is not returned, will have to ask for login info again
        if (result == null || result == "Error"){

            Toast.makeText(this, "Login Failed.", Toast.LENGTH_SHORT).show();

        } else{

            // Checking to see if the user has accepted terms before
            SharedPreferences sharedPref = getSharedPreferences("edu.umd.mindlab.androidservicetest", MODE_PRIVATE);
            tAccept = sharedPref.getString(A_TERMS, "Terms do not exist");

            // I think using shared preferences might cause an issue with testing because after
            // you set it, it is set on the device whether you reload the app or not
            // I could just while testing it set the main activity to set it to something else, or not set it at all.

            Log.v(TAG, tAccept);

            if (tAccept.equals("Terms do not exist")){
                // if the user has not acctepted terms, they will be directed to the Consent Page

                Intent getInfoIntent = new Intent(this, GetPersonalInfo.class);
                startActivity(getInfoIntent);

            } else{
                // if the user has accepted terms, then they can go right to Main

                Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);

            } // end if

        } // end if

    } // end onTaskCompleted

} // end class
