package dmnguyen.cs499;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.widget.TextView;

public class CounterService extends IntentService {

    public CounterService() {
        super("CounterService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String dataString = intent.getDataString();
    }


}
