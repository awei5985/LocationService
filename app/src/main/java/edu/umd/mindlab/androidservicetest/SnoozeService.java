package edu.umd.mindlab.androidservicetest;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.widget.TextView;

public class SnoozeService extends Service {
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



        return START_STICKY;
    }

    public class Counter extends CountDownTimer {

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

            // on finish I'll just senda broadcast to the snooze activity, telling it its done
            /*
            Intent mainIntent = new Intent(Snooze.this, MainActivity.class);
            startActivity(mainIntent); */

        }

    }
}
