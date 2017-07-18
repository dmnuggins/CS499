package dmnguyen.cs499;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.util.Calendar;


public class UpdateService extends Service {


    private static final String TRACKING_VALUES = "trackingValues";
    private static final String COUNT = "countKey";
    private static final String TOTAL = "totalKey";
    private static final String AVERAGE = "averageKey";
    private static final String YESTERDAY = "yesterdayKey";
    private static final String DAY_COUNT = "dayCountKey";

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
                } catch (InterruptedException e) {
                    getStackTrace();
                }
            }
        };
        t.start();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("onStartCommand", "has been called");
        super.onStartCommand(intent, flags, startId);

        ScreenReceiver sr = new ScreenReceiver();
        boolean screenOn = sr.getState();
        int updateCount = pref.getInt(COUNT,0);
        int updateTotal = pref.getInt(TOTAL,0);

        if(screenOn) {
            updateCount += 1;
            updateTotal += 1;
            pref.edit().putInt(COUNT, updateCount).apply();
            pref.edit().putInt(TOTAL, updateTotal).apply();
        }
        Log.i("UpdateService.Count", Integer.toString(updateCount));
        Log.i("UpdateService.TOTAL", Integer.toString(updateTotal));
        return START_STICKY;
    }


    @Override
    public void onDestroy() {

        super.onDestroy();
        Log.i("EXIT", "onDestroy!");
        unregisterReceiver(mReceiver);
        int updateCount = pref.getInt(COUNT,0);
        int updateTotal = pref.getInt(TOTAL,0);
        updateCount -= 2;
        updateTotal -= 2;
        pref.edit().putInt(COUNT, updateCount).apply();
        pref.edit().putInt(TOTAL, updateTotal).apply();

        // restarts the service once it's destroyed
        Intent broadcastIntent = new Intent(".RestartService");
        sendBroadcast(broadcastIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void calculateAverage() {
        int numDays = pref.getInt(DAY_COUNT,0);
        int totalCount = pref.getInt(TOTAL,0);
        float average = (float)totalCount/(float)numDays;
        pref.edit().putFloat(AVERAGE, average).apply();
    }

    private void resetCounter() {
        // TIME CONDITIONS
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY); //Current second
        int currentMinute = Calendar.getInstance().get(Calendar.MINUTE);
        int currentSecond = Calendar.getInstance().get(Calendar.SECOND);
        int dayCount = pref.getInt(DAY_COUNT,0) + 1;
        int dayTotal = pref.getInt(COUNT,0);

        if(currentHour == 0 && currentMinute == 0 && currentSecond == 0) {

            pref.edit().putInt(YESTERDAY,dayTotal).apply();
            pref.edit().putInt(DAY_COUNT, dayCount).apply();
            notifyUser();
            pref.edit().putInt(COUNT,0).apply();
            calculateAverage();

            Log.i("Timer","COUNT RESET FOR THE DAY");
            Log.i("Count",Integer.toString(pref.getInt(COUNT,0)));

        }
    }

    // Sends a notification lets user know how many times they've check their phone
    private void notifyUser() {

        String notification = "Yesterday, you checked your phone "
                + Integer.toString(pref.getInt(COUNT,0))
                + " times!";

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.locked_in_launcher_round)
                        .setContentTitle("LockedIN")
                        .setContentText(notification);
        Intent resultIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(0, mBuilder.build());
    }
}
