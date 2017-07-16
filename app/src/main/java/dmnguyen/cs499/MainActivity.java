package dmnguyen.cs499;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    String msg = "Android : ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(msg, "The onCreate() event");
    }

    public void startService(View view) {
        startService(new Intent(getBaseContext(), CounterService.class));
    }

    public void stopService(View view) {
        stopService(new Intent(getBaseContext(), CounterService.class));
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
}
