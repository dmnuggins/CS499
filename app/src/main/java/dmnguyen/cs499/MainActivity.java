package dmnguyen.cs499;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Build;

import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    String initVal = "0";
    PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_locked);
        TextView text = (TextView) findViewById(R.id.checkCount);
        text.setText(initVal);
    }

    @SuppressWarnings("depreciation")
    public boolean screenOn(PowerManager pm) {
        boolean result = false;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            result = pm.isInteractive();
        } else {
            result = pm.isScreenOn();
        }

        return result;
    }
}
