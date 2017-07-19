package dmnguyen.cs499;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenReceiver extends BroadcastReceiver {
    public static boolean screenOn;

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            // do whatever to be done
            screenOn = false;
        } else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            // do whatever to be done
            screenOn = true;
        }

        Intent i = new Intent(context, UpdateService.class);
        context.startService(i);
    }

    public boolean getState() {
        return screenOn;
    }
}
