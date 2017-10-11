package edu.umd.mindlab.androidservicetest;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class LoggedOutActivity extends AppCompatActivity {

    public Button loginButt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_out);
        getWindow().getDecorView().setBackgroundColor(Color.RED);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loginButt = (Button) findViewById(R.id.logInButton);

        loginButt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent mainIntent = new Intent(LoggedOutActivity.this, MainActivity.class);
                startActivity(mainIntent);

            }
        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }); */
        });
    }

}
