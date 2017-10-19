package edu.umd.mindlab.androidservicetest;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.TextView;

public class SnoozeService extends Service {

    public final String TAG = "Snooze Service";
    private Counter timeCount;
    private final String TIME_FILTER = "TimeFilter";
    private final String FINISH_FILTER = "FinishFilter";

    public SnoozeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        /* TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented"); */
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID){

        Log.v(TAG, "Snoozing Started");

        int hours = intent.getIntExtra("hours", 1);
        int minutes = intent.getIntExtra("minutes", 1);

        int ms = ((hours * 3600) + minutes * 60) * 1000;
        timeCount = new Counter(ms,1000);

        timeCount.start();

        return START_STICKY;
    }

    public class Counter extends CountDownTimer {

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

            //started making changes

            sendInfo(hours, minsStr, secsStr);
            //sendBroadcast(intent);
        }

        public void onFinish() {

            Intent intent = new Intent(FINISH_FILTER);

            Log.v(TAG, "Snoozing finished");

            LocalBroadcastManager.getInstance(SnoozeService.this).sendBroadcast(intent);

            Log.v(TAG, "Finished Broadcast sent");

            // on finish I'll just senda broadcast to the snooze activity, telling it its done
            /*
            Intent mainIntent = new Intent(Snooze.this, MainActivity.class);
            startActivity(mainIntent); */

        }

    }

    public void sendInfo(long hours, String mins, String secs){
        Intent intent = new Intent(TIME_FILTER);
        intent.putExtra("hours", hours);
        intent.putExtra("minutes", mins);
        intent.putExtra("seconds", secs);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        Log.v(TAG, "Time Broadcast Sent");
        Log.v(TAG, "Time was: " + hours + " : " + mins + " : " + secs);
    }

    @Override
    public void onDestroy(){
        timeCount.cancel();;
        stopSelf();
    }
}
