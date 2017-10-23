package edu.umd.mindlab.androidservicetest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class Snooze extends AppCompatActivity implements TaskCompleted {

    private TextView counter;
    private Button cancelButton;
    private final String TAG = "Snooze Activity";
    private final String LUID_STORE = "The_LUID_is_stored";
    private final String TIME_FILTER = "TimeFilter";
    private final String FINISH_FILTER = "FinishFilter";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snooze);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        counter = (TextView) findViewById(R.id.countDown);
        cancelButton = (Button) findViewById(R.id.cancelSnooze);
        final Intent snoozeIntent = new Intent(this, SnoozeService.class);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                timeReceiver, new IntentFilter("timeSent"));

        LocalBroadcastManager.getInstance(this).registerReceiver(
                finishedReceiver, new IntentFilter("finished"));

        // check if the app is already snoozed
        LoggedIn log = LoggedIn.getLog();
        if (!log.getSnoozed()) {

            // set the status for the app as snoozed
            log.setSnoozed(true);

            Intent i = getIntent();
            int hours = i.getIntExtra("hours", 1);
            int minutes = i.getIntExtra("mins", 0);

            if ((hours == -1) && (minutes == -1)) {
                Log.v(TAG, "ok, so they were both -1");
                hours = 1;
                minutes = 0;
                Toast.makeText(Snooze.this, "Default snooze time is 1 hour", Toast.LENGTH_SHORT).show();
            } else if (hours == -1) {
                Log.v(TAG, "ok, hours was -1");
                hours = 0;
            } else if (minutes == -1) {
                Log.v(TAG, "ok, minutes was -1");
                minutes = 0;
            }

            Log.v(TAG, "in onCreate, the service has not been started");

            snoozeIntent.putExtra("hours", hours);
            snoozeIntent.putExtra("minutes", minutes);

            // will also have to tell the server that situation is "snoozed"
            startService(snoozeIntent);
        }

        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                stopService(snoozeIntent);

                // set the status for the app as not snoozed
                LoggedIn log = LoggedIn.getLog();
                log.setSnoozed(false);

                Intent mainIntent = new Intent(v.getContext(), MainActivity.class);
                startActivity(mainIntent);

            }
        });

    }

    private BroadcastReceiver timeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String time = intent.getStringExtra("time");

            counter.setText(time);

        }
    };

    private BroadcastReceiver finishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            Log.v(TAG, "The time has finished");

            final Intent serviceIntent = new Intent(Snooze.this, SnoozeService.class);
            stopService(serviceIntent);

            // set the status for the app as not snoozed
            LoggedIn log = LoggedIn.getLog();
            log.setSnoozed(false);

            Intent mainIntent = new Intent(Snooze.this, MainActivity.class);
            startActivity(mainIntent);

        }
    };

    @Override
    protected void onDestroy(){
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(timeReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(finishedReceiver);
    }

    public void onTaskCompleted(String result){}

    @Override
    public void onBackPressed(){}
}
