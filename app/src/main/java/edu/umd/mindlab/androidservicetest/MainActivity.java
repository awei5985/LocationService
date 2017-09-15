package edu.umd.mindlab.androidservicetest;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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

    // wifi info persistence variables
    private WifiManager wifi;
    private List<ScanResult> mWifiResults;

    // broadcast receiver results from LocationService
    private Intent mGPSIntent;

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

        // check permissions
        if (notGranted()) {
            requestLocPermissions();
        }
        else {
            // intent for the locationService
            final Intent serviceIntent = new Intent(this, LocationService.class);

            // persist the switchButton between sessions
            switchButton = (Switch)findViewById(R.id.enableloc);
            SharedPreferences sharedPrefs = getSharedPreferences("edu.umd.mindlab.androidservicetest", MODE_PRIVATE);
            switchButton.setChecked(sharedPrefs.getBoolean(SHARE_LOC_STATUS, true));

            // listen for changes in the status of the switch button
            switchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(switchButton.isChecked()) {

                        // START LOCATION SERVICE
                        startService(serviceIntent);
                        Log.i(TAG, "Switch button clicked on -> start service");


                        SharedPreferences.Editor editor = getSharedPreferences("edu.umd.mindlab.androidservicetest", MODE_PRIVATE).edit();
                        editor.putBoolean(SHARE_LOC_STATUS, true);
                        editor.commit();
                    } else {

                        // STOP LOCATION SERVICE
                        // Note: Location service does not terminate unless this toggle is turned off.
                        stopService(serviceIntent);
                        Log.i(TAG, "Switch button clicked on -> stop service");

                        mWifiResults = null;
                        mGPSIntent = null;

                        SharedPreferences.Editor editor = getSharedPreferences("edu.umd.mindlab.androidservicetest", MODE_PRIVATE).edit();
                        editor.putBoolean(SHARE_LOC_STATUS, false);
                        editor.commit();
                    }
                }
            });
        }

    }

    public void requestLocPermissions() {
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

            Log.i(TAG, "need to show reasons for permissions");

        } else {

            ActivityCompat.requestPermissions(this,
                    PERMISSIONS_LOCATION,
                    REQUEST_LOC);

            Log.i(TAG, "requesting permissions");
        }
    }

    public void shouldSend() {
        if (mWifiResults != null && mGPSIntent != null) {
            sendLocation();
        }
    }

    public void sendLocation() {
        try {
            JSONObject obj = new JSONObject();
            JSONObject ap = new JSONObject();
            //String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Timestamp());
            String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
            for (ScanResult scan : mWifiResults) {
                ap.put("ssid", scan.SSID);
                ap.put("mac", scan.BSSID);
                ap.put("rssi", scan.level);
                ap.put("freq", scan.frequency);
            }

            obj.put("deviceID", mGPSIntent.getStringExtra("deviceID"));
            obj.put("timestamp", timeStamp);
            obj.put("accessPoints", ap);
            obj.put("latitude", mGPSIntent.getStringExtra("Latitude"));
            obj.put("longitude", mGPSIntent.getStringExtra("Longitude"));
            obj.put("atlitude", mGPSIntent.getStringExtra("Altitude"));
            obj.put("accuracy", mGPSIntent.getStringExtra("Accuracy"));
            String location = obj.toString();

            TextView tv = (TextView) findViewById(R.id.textLocation);
            tv.setText(location);

            //(new SendData()).execute(location);
        } catch(JSONException e) {
            //exception
            e.printStackTrace();
        }
    };

    // Broadcast receiver for Wifi. Changes in the Wifi get broadcast to this receiver
    private BroadcastReceiver wifiBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                // scan for all results
                mWifiResults = wifi.getScanResults();

                Log.i(TAG, "Wifi Broadcast Receiver onReceive fired");
                Log.i(TAG, mWifiResults.toString());

                // In case we haven't figured out the current location, let's figure it out
                // so we can send it over to the server.
                if (mGPSIntent == null) {

                    // if we don't have permissions
                    if (notGranted()) {
                        // ask for permissions
                        requestLocPermissions();

                    } else { // if we do have permissions

                        // Get the current GPS location
                        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

                        // even though we're checking for permissions, this segment of code still asks
                        // you to check, so we're suppressing the warning, we're getting the current
                        // location
                        @SuppressWarnings({"MissingPermission"})
                        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        mGPSIntent = new Intent(LOC_ACTION);
                        String deviceID =  Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

                        mGPSIntent.putExtra("deviceID", deviceID);
                        mGPSIntent.putExtra("Latitude", location.getLatitude());
                        mGPSIntent.putExtra("Longitude", location.getLongitude());
                        mGPSIntent.putExtra("Altitude", location.getAltitude());
                        mGPSIntent.putExtra("Accuracy", location.getAccuracy());

                        Log.e(TAG, mGPSIntent.toString());
                    }
                }
            }
            shouldSend();
        }
    };

    // GPS broadcast receiver
    private BroadcastReceiver gpsBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // save this intent for later
            mGPSIntent = intent;
            shouldSend();

            Log.i(TAG, "GPS Broadcast Receiver onReceive fired");
            Log.i(TAG, intent.toString());
        }

    };

    @Override
    protected void onResume()
    {
        Log.e(TAG, "OnResume");

        super.onResume();
        // register the GPS broadcast receiver so we can get info from LocationService
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(gpsBroadcastReceiver, new IntentFilter(LOC_ACTION));

        // register the WIFI broadcast receiver so we can get info from WIFI Service
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        setUp();
        registerReceiver(wifiBroadcastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "OnPause");

        try {
            mWifiResults = null;
            mGPSIntent = null;

            unregisterReceiver(gpsBroadcastReceiver);
            unregisterReceiver(wifiBroadcastReceiver);
        } catch(Exception e) {
            // not registered
        }
        super.onPause();
    }

    // start the wifi scan
    private void setUp() {
        Log.i(TAG, "setUp");

        if (!isConnected(getApplicationContext())) {
            finish();
        } else {
            wifi.startScan();
        }
    }

    // Do we have permissions access the location?
    private boolean notGranted() {
        Log.i(TAG, "notGranted");

        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED;
    }

    // are we connected to the internet at all??
    private static boolean isConnected(Context context) {
        Log.e(TAG, "isConnected");

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null);
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
