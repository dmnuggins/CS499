package dmnguyen.cs499;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Intent mServiceIntent;
    UpdateService mUpdateService;
    private Button resetCounterButton;
    private TextView counterTextView;
    private TextView totalTextView;
    private TextView averageTextView;
    private TextView yesterdayTextView;

    private static final String TRACKING_VALUES = "trackingValues";
    private static final String COUNT = "countKey";
    private static final String TOTAL = "totalKey";
    private static final String AVERAGE = "averageKey";
    private static final String YESTERDAY = "yesterdayKey";
    private static final String INITIAL = "initialKey";

    public boolean wasDestroyed;


    SharedPreferences pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean initial;

        Log.i("MainActivity", "ON_CREATE");
        super.onCreate(savedInstanceState);
        // restarts service if it is not running
        setContentView(R.layout.activity_main);
        mUpdateService = new UpdateService(getBaseContext());
        mServiceIntent = new Intent(getBaseContext(), mUpdateService.getClass());

        // UI stuffs
        resetCounterButton = (Button) findViewById(R.id.buttonReset);
        resetCounterButton.setOnClickListener(this);
        counterTextView = (TextView) findViewById(R.id.textViewCount);
        totalTextView = (TextView) findViewById(R.id.textViewTotal);
        averageTextView = (TextView) findViewById(R.id.textViewAverage);
        yesterdayTextView = (TextView) findViewById(R.id.textViewYesterday);

        // sharedPreferences
        pref = getSharedPreferences(TRACKING_VALUES, Context.MODE_PRIVATE);

        // initial opening of the app
        do{

            pref.edit().putInt(TOTAL,1).apply();
            pref.edit().putInt(COUNT,1).apply();
            pref.edit().putBoolean(INITIAL,false).apply();
            initial = pref.getBoolean(INITIAL,false);
        } while(initial);


        if(!isMyServiceRunning(mUpdateService.getClass())) {
            int countShift = pref.getInt(COUNT,0);
            int totalShift = pref.getInt(TOTAL,0);
            startService(mServiceIntent);
            Log.i("MainAct", "SERVICE STARTED");
            // shift accounts for when service is restarted when app is reopened
            if(countShift > countShift + 1) {
                wasDestroyed = false;
                countShift -= 1;
                totalShift -= 1;
            }
            pref.edit().putInt(COUNT,countShift).apply();
            pref.edit().putInt(TOTAL,totalShift).apply();
        }

        // Used to constantly update counter text view
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(500);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateTextView();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    getStackTrace();
                }
            }
        };
        t.start();
    }

    @Override
    public void onClick(View v) {
        if(v == resetCounterButton) {
            pref.edit().clear().apply();
            Log.i("Feedback Message", "ALL VALUES RESET");
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if(serviceClass.getName().equals(service.service.getClass())) {
                Log.i("isMyServiceRunning?", true + "");
                return true;
            }
        }
        Log.i("isMyServiceRunning?", false + "");
        return false;
    }

    @Override
    protected void onDestroy() {
        wasDestroyed = true;
        stopService(mServiceIntent);
        Log.i("MainActivity.onDestroy", "ON_DESTROY!");
        Log.i("MainActivity.Count", Integer.toString(pref.getInt(COUNT,0)));
        Log.i("UpdateService.TOTAL", Integer.toString(pref.getInt(TOTAL,0)));
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        wasDestroyed = true;
        stopService(mServiceIntent);
        Log.i("MainActivity.onPause", "ON_PAUSE!");
        Log.i("MainActivity.Count", Integer.toString(pref.getInt(COUNT,0)));
        Log.i("MainActivity.TOTAL", Integer.toString(pref.getInt(TOTAL,0)));
        super.onPause();
    }

    private void updateTextView() {
        String count = Integer.toString(pref.getInt(COUNT,0));
        String total = "Total: " + Integer.toString(pref.getInt(TOTAL,0));
        String average = "Daily Average: " + String.format("%.2f",pref.getFloat(AVERAGE,0));
        String yesterday = "Yesterday: " + Integer.toString(pref.getInt(YESTERDAY,0));
        counterTextView.setText(count);
        totalTextView.setText(total);
        averageTextView.setText(average);
        yesterdayTextView.setText(yesterday);

    }

    public boolean getDestroyedState() {
        return wasDestroyed;
    }

}
