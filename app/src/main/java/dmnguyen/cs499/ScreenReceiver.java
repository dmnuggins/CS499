package dmnguyen.cs499;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Dylan Nguyen on 7/16/2017.
 */

public class ScreenReceiver extends BroadcastReceiver {
    public static boolean wasScreenOn = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            // do whatever to be done
            wasScreenOn = false;
        } else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            // do whatever to be done
            wasScreenOn = true;
        }
    }
}
