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

public class MainActivity extends AppCompatActivity {

    Intent mServiceIntent;
    private UpdateService mUpdateService;

    Context ctx;

    public Context getCtx() { return ctx; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        setContentView(R.layout.activity_main);
        mUpdateService = new UpdateService(getCtx());
        mServiceIntent = new Intent(getCtx(), mUpdateService.getClass());
        if(!isMyServiceRunning(mUpdateService.getClass())) {
            startService(mServiceIntent);
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
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();
    }

//    @Override
//    protected void onPause() {
//        // WHEN THE SCREEN IS ABOUT TO TURN OFF
//        if(ScreenReceiver.screenOn) {
//            // THIS IS THE CASE WHEN ONPAUSE() IS CALLED BY THE SYSTEM DUE TO A SCREEN STATE CHANGE
//            System.out.println("SCREEN TURNED OFF");
//        } else {
//            // THIS IS WHEN ONPAUSE() IS CALLED WHEN THE SCREEN S TATE HAS NOT CHANGED
//        }
//        super.onPause();
//    }

//    @Override
//    protected void onResume() {
//        // ONLY WHEN THE SCREEN TURNS ON
//        if(!ScreenReceiver.screenOn) {
//            // THIS IS WHEN ONRESUME90 IS CALLED DUE TO A SCREEN STATE CHANGE
//            System.out.println("SCREEN TURNED ON");
//        } else {
//            // THIS IS WHEN ONRESUME() IS CALLED WHEN THE SCREEN STATE HAS NOT CHANGED
//        }
//        super.onResume();
//    }


}
