package dmnguyen.cs499;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Dylan Nguyen on 7/16/2017.
 */

public class ScreenReceiver extends BroadcastReceiver {
//    public static boolean screenOn;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(ScreenReceiver.class.getSimpleName(), "Service Stops! OH NOOOOOOOOOO!!!");

        context.startService(new Intent(context, UpdateService.class));

//        if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
//            // do whatever to be done
//            screenOn = false;
//        } else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
//            // do whatever to be done
//            screenOn = true;
//        }
//        Intent i = new Intent(context, UpdateService.class);
//        i.putExtra("screen_state", screenOn);
//        context.startService(i);
    }
}
