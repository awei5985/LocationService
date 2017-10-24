package edu.umd.mindlab.androidservicetest;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONException;
import org.json.JSONObject;

// MainActivity which handles all services and broadcast receivers.
public class MainActivity extends AppCompatActivity implements TaskCompleted {

    // GLOBAL VARIABLES
    public static final String LOC_ACTION = "LOCATION";
    private static final String TERMS_ACCEPT = "Are_Terms_Accepted";

    // LOCAL VARIABLES

    // logging
    private static final String TAG = "MainActivity"; // for logging

    // location and network permissions
    private static final int REQUEST_LOC = 0;
    private static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CHANGE_WIFI_STATE
    };

    // switch button persistence variables
    //private Switch switchButton;
    private ToggleButton toggleLoc;
    private static final String SHARE_LOC_STATUS = "Sharing_Location_Status";
    private static final String LUID_STORE = "The_LUID_is_stored";

    // Used for making a note that the user has accepted terms if they have made it to this page

    private Button logOutButton;
    private Button snoozeButton;
    //private Button changeLogButton;
    private EditText hours;
    private EditText minutes;
    private boolean hasStarted;

    // Called when the activity is first created. This is where you should do all of your normal
    // static set up: create views, bind data to lists, etc. This method also provides you with a
    // Bundle containing the activity's previously frozen state, if there was one.
    // Always followed by onStart().
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // standard procedures DO NOT DELETE
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // if the user comes back to the app (and never logged out) and it tries to take them to the login activity it will redirect here.
        LoggedIn log = LoggedIn.getLog();
        log.setLoggedIn(true);

        Log.i(TAG, "OnCreate");

        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Intent serviceIntent = new Intent(this, LocationService.class);
        ///////started making changes//////////
        hasStarted = false;
        //started changes
        //switchButton = (Switch)findViewById(R.id.enableloc);
        toggleLoc = (ToggleButton) findViewById(R.id.enableToggle);

        toggleLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(toggleLoc.isChecked()) {

                    // START LOCATION SERVICE
                    startService(serviceIntent);
                    Log.i(TAG, "Switch button clicked on -> start service");

                    // tell server status is 'collecting: on'
                    sendStatus("on");
                    LoggedIn log = LoggedIn.getLog();

                    // record that the app is sending the location
                    log.setSending(true);

                    hasStarted = true;

                    SharedPreferences.Editor editor = getSharedPreferences("edu.umd.mindlab.androidservicetest", MODE_PRIVATE).edit();
                    editor.putBoolean(SHARE_LOC_STATUS, true);
                    editor.commit();

                    TextView tv = (TextView) findViewById(R.id.textLocation);
                    tv.setText("Currently sharing your location");
                } else {

                    // STOP LOCATION SERVICE
                    // Note: Location service does not terminate unless this toggle is turned off.
                    stopService(serviceIntent);
                    Log.i(TAG, "Switch button clicked on -> stop service");

                    // tell server that status is 'collecting: off'
                    sendStatus("off");

                    hasStarted = false;
                    LoggedIn log = LoggedIn.getLog();

                    // Let the app know that it is no longer sending location
                    log.setSending(false);

                    SharedPreferences.Editor editor = getSharedPreferences("edu.umd.mindlab.androidservicetest", MODE_PRIVATE).edit();
                    editor.putBoolean(SHARE_LOC_STATUS, false);
                    editor.commit();

                    TextView tv = (TextView) findViewById(R.id.textLocation);
                    tv.setText("Not sharing your location");
                }
            }
        });

        logOutButton = (Button) findViewById(R.id.logOutButton);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                stopService(serviceIntent);
                Log.i(TAG, "Logged out -> stop service");

                // tell server that status is 'collecting: off'
                sendStatus("off");

                LoggedIn log = LoggedIn.getLog();
                log.setLoggedIn(false);
                //starting changes
                log.setMain(true);

                // tell the app that we are no longer sending
                log.setSending(false);

                Intent logIntent = new Intent(v.getContext(), CASLoginActivity.class);
                startActivity(logIntent);

            }
        });

        // check permissions
        if (notGranted()) { // permission has not been granted
            requestLocPermissions();
        }

        hours = (EditText) findViewById(R.id.hourEdit);
        minutes = (EditText) findViewById(R.id.minutesEdit);
        snoozeButton = (Button) findViewById(R.id.snoozeButton);
        //changeLogButton = (Button) findViewById(R.id.testButton);

        snoozeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int hrs;
                // if the number was blank, use default hours
                try{
                    hrs = Integer.parseInt(hours.getText().toString());
                } catch(NumberFormatException e){
                    hrs = -1;
                }

                int mns;
                // if the number was blank, use default mins
                try{
                    mns = Integer.parseInt(minutes.getText().toString());
                } catch(NumberFormatException e){
                    mns = -1;
                }

                stopService(serviceIntent);
                Log.i(TAG, "Snoozed -> stop service");

                sendStatus("snoozed");

                // call the snooze activity and pass the time to snooze
                Intent snoozeIntent = new Intent(v.getContext(), Snooze.class);
                snoozeIntent.putExtra("hours", hrs);
                snoozeIntent.putExtra("mins", mns);
                startActivity(snoozeIntent);

            }
        });

        /*changeLogButton is a terrible name, this is for changing the Terms Accepted status (for testing purposes)
        changeLogButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                SharedPreferences.Editor editor = getSharedPreferences("edu.umd.mindlab.androidservicetest", MODE_PRIVATE).edit();
                editor.putBoolean(TERMS_ACCEPT, false);
                editor.commit();

                Log.v(TAG, "It will now act as no consent has been done on login.");
                Toast.makeText(MainActivity.this, "Now as if you have not consented.", Toast.LENGTH_SHORT).show();

            }
        }); */
    }

    public boolean requestLocPermissions() {
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

            Log.i(TAG, "need to show reasons for permissions");
            return false;

        } else {

            ActivityCompat.requestPermissions(this,
                    PERMISSIONS_LOCATION,
                    REQUEST_LOC);

            toggleLoc = (ToggleButton) findViewById(R.id.enableToggle);
            toggleLoc.setChecked(true);

            //switchButton = (Switch)findViewById(R.id.enableloc);
            //switchButton.setChecked(true);

            Log.i(TAG, "requesting permissions");

            return true;
        }
    }

    @Override
    protected void onResume()
    {
        Log.e(TAG, "OnResume");

        // persist the switchButton between sessions
        //switchButton = (Switch)findViewById(R.id.enableloc);

        toggleLoc = (ToggleButton) findViewById(R.id.enableToggle);
        SharedPreferences sharedPrefs = getSharedPreferences("edu.umd.mindlab.androidservicetest", MODE_PRIVATE);
        //switchButton.setChecked(sharedPrefs.getBoolean(SHARE_LOC_STATUS, true));
        toggleLoc.setChecked(sharedPrefs.getBoolean(SHARE_LOC_STATUS, true));

        TextView tv = (TextView) findViewById(R.id.textLocation);
        if(toggleLoc.isChecked() && !hasStarted) {

            LoggedIn log = LoggedIn.getLog();
            if (!log.getSending()) {

                // set background of toggle to blue
                //toggleLoc.setBackgroundColor(0xFF4f7cf6);

                final Intent serviceIntent = new Intent(this, LocationService.class);
                // restart service
                stopService(serviceIntent);
                startService(serviceIntent);

                // tell server that status is 'collecting: on'
                sendStatus("on");

                log.setSending(true);
            }

            tv.setText("Currently sharing your location");
        } else{
            tv.setText("Not sharing your location");
        }

        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "OnPause");
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        Log.i(TAG, "OnStop");
        super.onStop();
    }

    // Do we have permissions access the location?
    private boolean notGranted() {
        Log.i(TAG, "Check if permissions NotGranted");

        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){}

    // This method sends the collecting status to the server
    public void sendStatus(String status){

        // get the LUID from shared preferences
        SharedPreferences sharedPref = getSharedPreferences("edu.umd.mindlab.androidservicetest", MODE_PRIVATE);
        String LUID = sharedPref.getString(LUID_STORE, "doesn't exist");

        // create the JSON to send to the server
        JSONObject disconJ = new JSONObject();
        try{
            disconJ.put("LUID", LUID);
            disconJ.put("collecting",status);
        }catch(JSONException e){
            Log.v(TAG, "JSON problem?");
        }

        // send the JSON
        (new SendInfo(MainActivity.this)).execute(disconJ);

    }

    @Override
    public void onTaskCompleted(String result) {}
}
