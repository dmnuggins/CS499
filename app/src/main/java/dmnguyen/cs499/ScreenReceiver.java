package dmnguyen.cs499;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class ScreenReceiver extends BroadcastReceiver {
    public static boolean screenOn = true;
    private static final String SCREEN_STATE = "screen_state";



    @Override
    public void onReceive(Context context, Intent intent) {


        if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            // do whatever to be done
            screenOn = false;
            Log.i(ScreenReceiver.class.getSimpleName(), "Screen OFF");
        } else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            // do whatever to be done
            screenOn = true;
            Log.i(ScreenReceiver.class.getSimpleName(), "Screen ON");
        }


        Intent i = new Intent(context, UpdateService.class);
//        i.putExtra(SCREEN_STATE, screenOn);
        context.startService(i);
    }

    public boolean getState() {
        return screenOn;
    }
}
