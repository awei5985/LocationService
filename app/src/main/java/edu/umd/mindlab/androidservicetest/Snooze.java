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

        /*Log.v(TAG,"The time is " + minutes + " mins and " + seconds + " secs.");

        Intent i = getIntent();
        int hours = i.getIntExtra("hours", 1);
        int minutes = i.getIntExtra("mins", 0);

        if ((hours == -1) && (minutes == -1)){
            Log.v(TAG, "ok, so they were both -1");
            hours = 1;
            minutes = 0;
            Toast.makeText(Snooze.this, "Default snooze time is 1 hour", Toast.LENGTH_SHORT).show();
        } else if(hours == -1){
            Log.v(TAG, "ok, hours was -1");
            hours = 0;
        } else if(minutes == -1){
            Log.v(TAG, "ok, minutes was -1");
            minutes = 0;
        }

        final Intent startSnooze = new Intent(this, SnoozeService.class);
        startSnooze.putExtra("hours", hours);
        startSnooze.putExtra("minutes", minutes);
        startService(startSnooze);

        IntentFilter intentF = new IntentFilter(TIME_FILTER);
        registerReceiver(timeReceiver, intentF);

        IntentFilter intentTime = new IntentFilter(FINISH_FILTER);
        registerReceiver(finishedReceiver, intentTime);

        cancelButton = (Button) findViewById(R.id.cancelSnooze);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                stopService(startSnooze);
                Intent mainIntent = new Intent(v.getContext(), MainActivity.class);
                startActivity(mainIntent);

            }
        });

        /*
        SharedPreferences sharedPref = getSharedPreferences("edu.umd.mindlab.androidservicetest", MODE_PRIVATE);
        String LUID = sharedPref.getString(LUID_STORE, "doesn't exist");

        if (LUID.equals("doesn't exist")){
            Log.v(TAG, "why doesn't the LUID exist?");
        }

        JSONObject disconJ = new JSONObject();
        try{
            disconJ.put("LUID", LUID);
            disconJ.put("collecting","snoozed");
        }catch(JSONException e){
            Log.v(TAG, "JSON problem?");
        }

        (new SendInfo(Snooze.this)).execute(disconJ);

        Intent i = getIntent();
        int hours = i.getIntExtra("hours", 1);
        int minutes = i.getIntExtra("mins", 0);

        // if the user did not enter a number of either hours or minutes the default time is 1 hour
        if ((hours == -1) && (minutes == -1)){
            Log.v(TAG, "ok, so they were both -1");
            hours = 1;
            minutes = 0;
            Toast.makeText(Snooze.this, "Default snooze time is 1 hour", Toast.LENGTH_SHORT).show();
        } else if(hours == -1){
            Log.v(TAG, "ok, hours was -1");
            hours = 0;
        } else if(minutes == -1){
            Log.v(TAG, "ok, minutes was -1");
            minutes = 0;
        }

        int ms = ((hours * 3600) + minutes * 60) * 1000;

        counter = (TextView) findViewById(R.id.countDown);
        cancelButton = (Button) findViewById(R.id.cancelSnooze);

        // now send the info to the service

        final Counter timeCount = new Counter(ms,1000);

        timeCount.setCounterText(counter);

        timeCount.start();

        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                timeCount.cancel();
                Intent mainIntent = new Intent(v.getContext(), MainActivity.class);
                startActivity(mainIntent);

            }
        });

        /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }); */
    }
/*
    public class Counter extends CountDownTimer{

        TextView countD;

        public Counter(long millisInFuture, long countDownInterval) {
            super(millisInFuture,countDownInterval);
        }

        public void onTick(long millisUntilFinished) {
            String minsStr;
            String secsStr;

            // from millisUntilFinished figure out how many hours, minutes and seconds to show
            long secsLeft = millisUntilFinished/1000;
            long hours = secsLeft/3600;
            long minsLeft = (secsLeft - (hours * 3600))/60;
            secsLeft = ((secsLeft - (hours * 3600)) - (minsLeft * 60));

            // making sure it shows 05 instead of 5, not necessary for hours
            if (minsLeft < 10){
                minsStr = "0" + minsLeft;
            } else{
                minsStr = "" + minsLeft;
            }
            if (secsLeft < 10) {
                secsStr = "0" + secsLeft;
            } else{
                secsStr = "" + secsLeft;
            }

            countD.setText(hours + " : " + minsStr + " : " + secsStr);
        }

        public void setCounterText(TextView c){
            countD = c;
        }

        public void onFinish() {

            Intent mainIntent = new Intent(Snooze.this, MainActivity.class);
            startActivity(mainIntent);

        }

    } */

    @Override
    public void onResume(){
        super.onResume();

        Intent i = getIntent();
        int hours = i.getIntExtra("hours", 1);
        int minutes = i.getIntExtra("mins", 0);

        if ((hours == -1) && (minutes == -1)){
            Log.v(TAG, "ok, so they were both -1");
            hours = 1;
            minutes = 0;
            Toast.makeText(Snooze.this, "Default snooze time is 1 hour", Toast.LENGTH_SHORT).show();
        } else if(hours == -1){
            Log.v(TAG, "ok, hours was -1");
            hours = 0;
        } else if(minutes == -1){
            Log.v(TAG, "ok, minutes was -1");
            minutes = 0;
        }

        final Intent startSnooze = new Intent(this, SnoozeService.class);
        startSnooze.putExtra("hours", hours);
        startSnooze.putExtra("minutes", minutes);
        startService(startSnooze);

        IntentFilter intentF = new IntentFilter(TIME_FILTER);
        registerReceiver(timeReceiver, intentF);

        IntentFilter intentTime = new IntentFilter(FINISH_FILTER);
        registerReceiver(finishedReceiver, intentTime);

        cancelButton = (Button) findViewById(R.id.cancelSnooze);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                stopService(startSnooze);
                Intent mainIntent = new Intent(v.getContext(), MainActivity.class);
                startActivity(mainIntent);

            }
        });
    }

    public void onTaskCompleted(String result){}

    public BroadcastReceiver timeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.v(TAG, "Time Broadcast Received");
            int h = intent.getIntExtra("hours", 1);
            String m = intent.getStringExtra("minutes");
            String s = intent.getStringExtra("seconds");

            counter.setText(h + " : " + m + " : " + s);
        }
    };

    public BroadcastReceiver finishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //stopService(startSnooze);

            Log.v(TAG, "Finished Broadcast Received");

            Intent mainIntent = new Intent(Snooze.this, MainActivity.class);
            startActivity(mainIntent);
        }
    };
}
