package edu.umd.mindlab.androidservicetest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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

import java.io.File;
import java.net.URI;

public class SendEmail extends AppCompatActivity {

    private EditText email;
    private Button sendEmail;
    private static final String TERMS_ACCEPT = "Are_Terms_Accepted";
    private final String TAG = "SendEmailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_email);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Storing in shared preferences that the user has accepted terms
        SharedPreferences.Editor editor = getSharedPreferences("edu.umd.mindlab.androidservicetest", MODE_PRIVATE).edit();
        editor.putBoolean(TERMS_ACCEPT, true);
        editor.commit();

        Log.v(TAG, "Terms pushed in SendEmail");

        email = (EditText) findViewById(R.id.emailEdit);
        sendEmail = (Button) findViewById(R.id.sendConfirmation);

        sendEmail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent mainIntent = new Intent(SendEmail.this, MainActivity.class);
                startActivity(mainIntent);

                //file:///C:/Users/User/LocationService/app/src/main/assets/Consent_Smartphone_App.pdf
                // for now I just want to make sure the flow is good. I will work on sending the email later
               //Uri uri=Uri.parse("file:///LocationService/app/src/main/assets/Consent_Smartphone_App.pdf");

                /*File termsFile = new File("android.resource://edu.umd.mindlab.androidservicetest/assets/Consent_Smartphone_App.pdf");
                Uri uri = Uri.fromFile(termsFile);
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"mharding15@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Send Email Test 1");
                i.putExtra(Intent.EXTRA_TEXT   , "this is a test of sending an email in android. Did it work?");
                i.setType("application/pdf");
                i.putExtra(Intent.EXTRA_STREAM, uri);
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(v.getContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                } */
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

}
