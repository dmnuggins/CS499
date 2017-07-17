package dmnguyen.cs499;


import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class CounterService extends IntentService {

    SharedPreferences sharedpreferences;
    int total;
    int average;
    int max;

    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
    boolean isScreenOn = pm.isInteractive();

    public CounterService() {
        super("CounterService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }
}
