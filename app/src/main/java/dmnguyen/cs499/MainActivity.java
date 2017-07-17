package dmnguyen.cs499;

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

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // INITIALIZE RECEIVER
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);
        // Rest of code

    }

    @Override
    protected void onPause() {
        // WHEN THE SCREEN IS ABOUT TO TURN OFF
        if(ScreenReceiver.wasScreenOn) {
            // THIS IS THE CASE WHEN ONPAUSE() IS CALLED BY THE SYSTEM DUE TO A SCREEN STATE CHANGE
            System.out.println("SCREEN TURNED OFF");
        } else {
            // THIS IS WHEN ONPAUSE() IS CALLED WHEN THE SCREEN S TATE HAS NOT CHANGED
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        // ONLY WHEN THE SCREEN TURNS ON
        if(!ScreenReceiver.wasScreenOn) {
            // THIS IS WHEN ONRESUME90 IS CALLED DUE TO A SCREEN STATE CHANGE
            System.out.println("SCREEN TURNED ON");
        } else {
            // THIS IS WHEN ONRESUME() IS CALLED WHEN THE SCREEN STATE HAS NOT CHANGED
        }
        super.onResume();
    }

    //    @SuppressWarnings("depreciation")
//    public boolean screenOn(PowerManager pm) {
//        boolean result = false;
//
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
//            result = pm.isInteractive();
//        } else {
//            result = pm.isScreenOn();
//        }
//
//        return result;
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
