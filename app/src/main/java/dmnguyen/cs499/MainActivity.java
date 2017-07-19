package dmnguyen.cs499;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Intent mServiceIntent;
    UpdateService mUpdateService;
    SharedPreferences pref;


    private Button resetCounterButton;
    private TextView counterTextView;
    private TextView totalTextView;
    private TextView averageTextView;
    private TextView yesterdayTextView;
    private ToggleButton toggleButton;

    private static Bundle bundle = new Bundle();

    private static final String TRACKING_VALUES = "trackingValues";
    private static final String COUNT = "countKey";
    private static final String TOTAL = "totalKey";
    private static final String AVERAGE = "averageKey";
    private static final String YESTERDAY = "yesterdayKey";
    private static final String INITIAL = "initialKey";
    private static final String CAN_RUN = "canRunKey";
    private static final String TOGGLE_BUTTON_STATE = "toggleStateKey";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        Log.i("MAIN", "onCreate");
        super.onCreate(savedInstanceState);
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
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        // sharedPreferences initialization
        pref = getSharedPreferences(TRACKING_VALUES, Context.MODE_PRIVATE);

        boolean initial = pref.getBoolean(INITIAL,true);
        boolean checkedStatus =  pref.getBoolean(TOGGLE_BUTTON_STATE,true);
        toggleButton.setChecked(checkedStatus);




        // sets the initial count when first opening the app
        if(initial) {
            pref.edit().putInt(TOTAL,1).apply();
            pref.edit().putInt(COUNT,1).apply();
            pref.edit().putBoolean(INITIAL,false).apply();
        }

        // For disabling/enabling the service
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    Log.i("MAIN, runnable", true + "");
                    pref.edit().putBoolean(TOGGLE_BUTTON_STATE, true).apply();
                } else {
                    Log.i("MAIN, runnable", false + "");
                    pref.edit().putBoolean(TOGGLE_BUTTON_STATE, false).apply();
                }
            }
        });

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
                        Thread.sleep(100);
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
        super.onDestroy();

        Log.i("MAIN", "onDestroy, SERVICE STOPPED");
        stopService(mServiceIntent);
        Log.i("MainActivity", "onDestroy!");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("ToggleButtonState", toggleButton.isChecked());
    }

//    // saves button state
//    public void saveButtonState(boolean pressed) {
//        pref.edit().putBoolean(TOGGLE_BUTTON_STATE, pressed).apply();
//    }

    private void updateTextView() {
        int count = pref.getInt(COUNT,0);
        int total = pref.getInt(TOTAL,0);
        float avg = pref.getFloat(AVERAGE,0);
        int yesterday = pref.getInt(YESTERDAY,0);
        counterTextView.setText(String.format(Locale.getDefault(),"%d", count));
        totalTextView.setText(String.format(Locale.getDefault(), "Total: %d", total));
        averageTextView.setText(String.format(Locale.getDefault(),"Daily Average: %.2f", avg));
        yesterdayTextView.setText(String.format(Locale.getDefault(), "Yesterday: %d", yesterday));

    }

}
