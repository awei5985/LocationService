package edu.umd.mindlab.androidservicetest;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.widget.Switch;
import android.widget.TextView;

import edu.umd.mindlab.androidservicetest.LoginActivity;

// MainActivity which handles all services and broadcast receivers.
public class MainActivity extends AppCompatActivity {

    // GLOBAL VARIABLES
    public static final String LOC_ACTION = "LOCATION";

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
    private Switch switchButton;
    private static final String SHARE_LOC_STATUS = "Sharing_Location_Status";

    private Button logOutButton;

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

        Log.i(TAG, "OnCreate");

        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Intent serviceIntent = new Intent(this, LocationService.class);

        hasStarted = false;

        switchButton = (Switch)findViewById(R.id.enableloc);
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(switchButton.isChecked()) {

                // START LOCATION SERVICE
                startService(serviceIntent);
                Log.i(TAG, "Switch button clicked on -> start service");

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

                hasStarted = false;

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
                Intent logIntent = new Intent(v.getContext(), LoginActivity.class);
                logIntent.putExtra("loggedOut", false);
                startActivity(logIntent);
            }
        });

        // check permissions
        if (notGranted()) { // permission has not been granted
            requestLocPermissions();
        }
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

            switchButton = (Switch)findViewById(R.id.enableloc);
            switchButton.setChecked(true);

            Log.i(TAG, "requesting permissions");

            return true;
        }
    }

    @Override
    protected void onResume()
    {
        Log.e(TAG, "OnResume");

        // persist the switchButton between sessions
        switchButton = (Switch)findViewById(R.id.enableloc);
        SharedPreferences sharedPrefs = getSharedPreferences("edu.umd.mindlab.androidservicetest", MODE_PRIVATE);
        switchButton.setChecked(sharedPrefs.getBoolean(SHARE_LOC_STATUS, true));

        if(switchButton.isChecked() && !hasStarted) {
            final Intent serviceIntent = new Intent(this, LocationService.class);
            // restart service
            stopService(serviceIntent);
            startService(serviceIntent);

            TextView tv = (TextView) findViewById(R.id.textLocation);
            tv.setText("Currently sharing your location");
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
}
