package edu.umd.mindlab.androidservicetest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class Snooze extends AppCompatActivity implements TaskCompleted {

    private TextView counter;
    private Button cancelButton;
    private final String TAG = "Snooze Activity";
    private final String LUID_STORE = "The_LUID_is_stored";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snooze);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       //Log.v(TAG,"The time is " + minutes + " mins and " + seconds + " secs.");

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

        int ms = ((hours * 3600) + minutes * 60) * 1000;

        counter = (TextView) findViewById(R.id.countDown);
        cancelButton = (Button) findViewById(R.id.cancelSnooze);

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
            long minsLeft = (secsLeft % 3600)/60;
            secsLeft = (secsLeft % 3600) % 60;

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

    }

    public void onTaskCompleted(String result){}

}
