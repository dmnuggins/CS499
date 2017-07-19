package dmnguyen.cs499;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
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
    private static final String CURRENT_STATE = "cuStateKey";
    private static final String TOGGLE_BUTTON_STATE = "toggleStateKey";

    SharedPreferences pref;
    BroadcastReceiver mReceiver;
    IntentFilter filter;

    public UpdateService(Context appContext) {
        super();
    }

    public UpdateService(){}

    @Override
    public void onCreate() {
        super.onCreate();
        // REGISTER RECEIVER THAT HANDLES SCREEN ON AND SCREEN OFF LOGIC
        filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);
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
        Log.i("SERVICE", "onStartCommand Called");
        super.onStartCommand(intent, flags, startId);

        ScreenReceiver sr = new ScreenReceiver();
        boolean screenOn = sr.getState();
        boolean previousState = pref.getBoolean(CURRENT_STATE,false);

        Log.i("SERVICE.oSC", "SCREEN_ON:" + screenOn);
        int updateCount = pref.getInt(COUNT,0);
        int updateTotal = pref.getInt(TOTAL,0);

        notifyUser(0);

        /**
         * When service restarts, this checks if the previous state of the screen. If screen
         * was still on when service restarts, count will not increase. If screen was off before
         * service restarts, count will update.
         */
        if(screenOn && !previousState) {
            updateCount += 1;
            updateTotal += 1;

            pref.edit().putInt(COUNT, updateCount).apply();
            Log.i("SERVICE.oSC", "COUNT + 1");
            pref.edit().putInt(TOTAL, updateTotal).apply();
            Log.i("SERVICE.oSC", "TOTAL + 1");

            Log.i("Count", Integer.toString(updateCount));
            Log.i("TOTAL", Integer.toString(updateTotal));

        }
        pref.edit().putBoolean(CURRENT_STATE,screenOn).apply();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        Log.i("SERVICE.oD", "Service DESTROYED");
        unregisterReceiver(mReceiver);

        // restarts the service once it's destroyed
        boolean toggle = pref.getBoolean(TOGGLE_BUTTON_STATE,true);
        if(toggle) {
            Intent broadcastIntent = new Intent(".RestartService");
            sendBroadcast(broadcastIntent);
        } else {
            // cancels ACTIVE notification when toggle is switched off
            cancelNotification(getBaseContext(),0);
        }
        // NEED A BOOLEAN TO FLAG THAT SERVICE WAS RESTARTED
        Log.i("SERVICE.oD", "Broadcast SENT");
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
//            checkDailyRecord();
//            notifyUser(1);
            pref.edit().putInt(COUNT,0).apply();
            calculateAverage();

            Log.i("Timer","COUNT RESET FOR THE DAY");
            Log.i("Count",Integer.toString(pref.getInt(COUNT,0)));

        }
    }

    // Sends a notification lets user know how many times they've check their phone
    private void notifyUser(int flag) {

        String note = numberNotification(flag);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.locked_in_launcher_round))
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setNumber(flag)
                        .setSmallIcon(R.mipmap.locked_in_launcher_white)
                        .setContentTitle("LockedIN")
                        .setContentText(note);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        if(flag == 0) {
            mBuilder.setOngoing(true);
        }
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // mNotificationId is a unique integer your app uses to identify the
        // notification. For example, to cancel the notification, you can pass its ID
        // number to NotificationManager.cancel().
        mNotificationManager.notify(0, mBuilder.build());
    }

    // gives unique text to notifications
    public String numberNotification(int flag) {
        switch (flag) {
            case 0 :
                return "ACTIVE";
            case 1 :
                return "Yesterday, you checked your phone "
                        + Integer.toString(pref.getInt(COUNT,0))
                        + " times!";
            case 2 :
                return "You've broken your daily record...";
            default :
                break;
        }
        return null;
    }

    // clears a notification based on its unique id
    public void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager manager = (NotificationManager) ctx.getSystemService(ns);
        manager.cancel(notifyId);
    }

//    public void checkDailyRecord() {
//        int count = pref.getInt(COUNT,0);
//        float daily = pref.getFloat(AVERAGE,0);
//        if(count > daily && daily != 0.0) {
//            notifyUser(2);
//        }
//    }
}
