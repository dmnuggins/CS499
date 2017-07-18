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
    private static final String SCREEN_STATE = "screen_state";
    BroadcastReceiver mReceiver;
    IntentFilter filter;

    public UpdateService(Context applicationContext) {
        super();
        Log.i("HERE", "here I am!");
    }

    public UpdateService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        // REGISTER RECEIVER THAT HANDLES SCREEN ON AND SCREEN OFF LOGIC
        filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ScreenReceiver sr = new ScreenReceiver();
        boolean screenOn = sr.getState();
        if(!screenOn) {
            // whatever
        } else {
            // whatever
        }
        super.onStartCommand(intent, flags, startId);
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
//        unregisterReceiver(mReceiver);
        super.onDestroy();
        Log.i("EXIT", "onDestroy!");
        // restarts the service once it's destroyed
        Intent broadcastIntent = new Intent(".RestartService");
        sendBroadcast(broadcastIntent);
//        stoptimertask();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
