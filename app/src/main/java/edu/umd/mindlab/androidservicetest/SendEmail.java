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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

                InputStream inputStream = null;
                ByteArrayOutputStream output = null;
                try {
                    inputStream = getAssets().open("Consent_Smartphone_App.pdf");
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    output = new ByteArrayOutputStream();
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }
                } catch(IOException e){
                    Log.e(TAG, "Problem getting pdf");
                }

                final byte[] file = output.toByteArray();

                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            EmailSender sender = new EmailSender("PrometheusLoc@gmail.com",
                                    "prometheus");
                            sender.sendMailAttach("Prometheus Terms and Conditions PDF", "Do not reply to this email",
                                    "PrometheusLoc@gmail.com", email.getText().toString(), file);
                        } catch (Exception e) {
                            Log.e("SendMail", "Sending didn't work?");
                        }
                    }

                }).start();

                Toast.makeText(v.getContext(), "Email Sent", Toast.LENGTH_SHORT).show();

                //Intent mainIntent = new Intent(v.getContext(), MainActivity.class);
               // startActivity(mainIntent);

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
