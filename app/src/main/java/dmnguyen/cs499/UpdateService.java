package dmnguyen.cs499;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class UpdateService extends Service {


    private static final String TRACKING_VALUES = "trackingValues";
    private static final String COUNT = "countKey";

    private boolean serviceStarted;

    SharedPreferences pref;
    BroadcastReceiver mReceiver;
    IntentFilter filter;

    public UpdateService(Context appContext) {
        super();
    }

    public UpdateService() {};



    @Override
    public void onCreate() {
        super.onCreate();
        // REGISTER RECEIVER THAT HANDLES SCREEN ON AND SCREEN OFF LOGIC
        filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);
        serviceStarted = true;
        pref = getApplication().getSharedPreferences(TRACKING_VALUES,Context.MODE_PRIVATE);

        // Background thread to reset counter once the day ends
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        resetCounter();
                    }
                } catch (InterruptedException e) {}
            }
        };
        t.start();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        ScreenReceiver sr = new ScreenReceiver();
        boolean screenOn = sr.getState();
        int updateCount = pref.getInt(COUNT,0);
        if(screenOn && serviceStarted) {
            updateCount++;
            pref.edit().putInt(COUNT, updateCount).apply();
            Log.i("Count", Integer.toString(updateCount));
        }
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
//        unregisterReceiver(mReceiver);
        Intent broadcastIntent = new Intent(".RestartService");
        sendBroadcast(broadcastIntent);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        Log.i("EXIT", "onDestroy!");
        unregisterReceiver(mReceiver);
        int updateCount = pref.getInt(COUNT,0);
        updateCount -= 3;
        pref.edit().putInt(COUNT, updateCount).apply();

        // restarts the service once it's destroyed
        Intent broadcastIntent = new Intent(".RestartService");
        sendBroadcast(broadcastIntent);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void resetCounter() {
        // TIME CONDITIONS
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY); //Current second
        int currentMinute = Calendar.getInstance().get(Calendar.MINUTE);
        int currentSecond = Calendar.getInstance().get(Calendar.SECOND);
        if(currentHour == 0 && currentMinute == 0 && currentSecond == 0) {
            pref.edit().putInt(COUNT,0).apply();
            Log.i("Timer","COUNT RESET FOR THE DAY");
            Log.i("Count",Integer.toString(pref.getInt(COUNT,0)));
        }
    }
}
