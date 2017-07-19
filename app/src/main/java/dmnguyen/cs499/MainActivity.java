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
    SharedPreferences pref;

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




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("MAIN", "onCreate");
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
        boolean initial = pref.getBoolean(INITIAL,true);

        // sets the initial count when first opening the app
        if(initial) {
            pref.edit().putInt(TOTAL,1).apply();
            pref.edit().putInt(COUNT,1).apply();
            pref.edit().putBoolean(INITIAL,false).apply();
        }

        if(!isMyServiceRunning(mUpdateService.getClass())) {
            Log.i("MAIN.onCreate", "START SERVICE");
            startService(mServiceIntent);
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
        Log.i("MAIN", "isMyServiceRunning?" + false + "");
        return false;
    }

    @Override
    protected void onDestroy() {
        Log.i("MAIN", "onDestroy, SERVICE STOPPED");
        stopService(mServiceIntent);
        Log.i("MainActivity", "onDestroy!");
        super.onDestroy();
    }

    private void updateTextView() {
        String count = Integer.toString(pref.getInt(COUNT,0));
        String total = "Total: " + Integer.toString(pref.getInt(TOTAL,0));
        String average = "Daily Average: " + String.valueOf(pref.getFloat(AVERAGE,0));
        String yesterday = "Yesterday: " + Integer.toString(pref.getInt(YESTERDAY,0));
        counterTextView.setText(count);
        totalTextView.setText(total);
        averageTextView.setText(average);
        yesterdayTextView.setText(yesterday);

    }

}
