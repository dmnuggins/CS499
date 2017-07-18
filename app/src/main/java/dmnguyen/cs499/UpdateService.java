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
    Calendar currentTime = Calendar.getInstance();
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

        // TIME CONDITIONS
//        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY); //Current hour
//        return currentHour < 18 //False if after 6pm

        return START_STICKY;


    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        unregisterReceiver(mReceiver);
        Intent broadcastIntent = new Intent(".RestartService");
        sendBroadcast(broadcastIntent);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        Log.i("EXIT", "onDestroy!");

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
}
