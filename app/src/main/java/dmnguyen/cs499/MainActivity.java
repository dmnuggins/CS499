package dmnguyen.cs499;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Intent mServiceIntent;
    UpdateService mUpdateService;

    private Button resetCounterButton;
    private TextView counterTextView;

    private static final String TRACKING_VALUES = "trackingValues";
    private static final String COUNT = "countKey";
    SharedPreferences pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // restarts service if it is not running
        setContentView(R.layout.activity_main);
        mUpdateService = new UpdateService(getBaseContext());
        mServiceIntent = new Intent(getBaseContext(), mUpdateService.getClass());

        // UI stuffs
        resetCounterButton = (Button) findViewById(R.id.buttonReset);
        resetCounterButton.setOnClickListener(this);
        counterTextView = (TextView) findViewById(R.id.textViewCount);

        // sharedPreferences
        pref = getSharedPreferences(TRACKING_VALUES, Context.MODE_PRIVATE);

        if(!isMyServiceRunning(mUpdateService.getClass())) {
            startService(mServiceIntent);
            int countShift = pref.getInt(COUNT,0);
            if(countShift > 0) {
                countShift -= 1;
            }
            pref.edit().putInt(COUNT,countShift).apply();
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
                } catch (InterruptedException e) {}
            }
        };
        t.start();

    }

    @Override
    public void onClick(View v) {
        if(v == resetCounterButton) {
            pref.edit().clear().apply();
            Log.i("Feedback Message", "COUNT RESET");
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
        stopService(mServiceIntent);
        Log.i("MainActivity", "onDestroy!");
        super.onDestroy();
    }

    private void updateTextView() {
        String string = Integer.toString(pref.getInt(COUNT,0));
        counterTextView.setText(string);
    }

}
