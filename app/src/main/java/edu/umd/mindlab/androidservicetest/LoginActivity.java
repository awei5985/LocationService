package edu.umd.mindlab.androidservicetest;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity {

    private Button mainButton;
    private boolean loggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // I'm not sure this is the best way to see if the user has already logged in, but it works
        //  for now. From what I've seen so far from CAS login info we should probably use cookies
        Intent i = getIntent();
        loggedIn = i.getBooleanExtra("loggedOut", false);

        if (loggedIn){
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
        }

        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainButton = (Button) findViewById(R.id.mainButton);

        mainButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent mainIntent = new Intent(v.getContext(), MainActivity.class);
                startActivity(mainIntent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

}
