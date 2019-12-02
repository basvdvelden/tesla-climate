package nl.vandervelden.teslaclimate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class AppBootReceiver extends BroadcastReceiver {
    private static final String TAG = "AppBootReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "In on receive of app boot receiver");
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.d(TAG, "In if statement on receive of app boot receiver");
            SharedPreferences sp = context.getSharedPreferences(MainActivity.SP, Context.MODE_PRIVATE);
            Long time = StaticClass.getNextAlarmTime(sp);
            Alarm alarm = new Alarm();
            alarm.setAlarm(context, time);
        }
    }
}
