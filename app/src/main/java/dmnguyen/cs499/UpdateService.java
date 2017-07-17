package dmnguyen.cs499;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class UpdateService extends Service {

    //PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

    public int counter = 0;
    public UpdateService(Context applicationContext) {
        super();
        Log.i("HERE", "here I am!");
    }

    public UpdateService() {

    }

//    @Override
//    public void onCreate() {
//        super.onCreate();
//        // REGISTER RECEIVER THAT HANDLES SCREEN ON AND SCREEN OFF LOGIC
//        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
//        filter.addAction(Intent.ACTION_SCREEN_OFF);
//        BroadcastReceiver mReceiver = new ScreenReceiver();
//        registerReceiver(mReceiver, filter);
//        //screenCheck();
//    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        boolean screenOn = intent.getBooleanExtra("screen_state", false);
//        if(!screenOn) {
//            // whatever
//        } else {
//            // whatever
//        }
        super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "onDestroy!");
        // restarts the service once it's destroyed
        Intent broadcastIntent = new Intent(".RestartSensor");
        sendBroadcast(broadcastIntent);
        stoptimertask();
    }

    //    @Override
//    public void onStart(Intent intent, int startId) {
//        boolean screenOn = intent.getBooleanExtra("screen_state", false);
//        if(!screenOn) {
//            // whatever
//        } else {
//            // whatever
//        }
//    }

//    public void screenCheck() {
//
//        if(!pm.isInteractive()) {
//            System.out.println("SCREEN TURNED OFF");
//        } else if(pm.isInteractive()){
//            System.out.println("SCREEN TURNED ON");
//        }
//    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime = 0;
    public void startTimer() {
        // set a new Timer
        timer = new Timer();

        // initialize the TimerTask's job
        initializeTimerTask();

        // schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000);
    }

    /**
     * sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  "+ (counter++));
            }
        };
    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
