package nl.vandervelden.teslaclimate;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;


public class Alarm extends BroadcastReceiver {
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Toast.makeText(context.getApplicationContext(), "Turning on climate", Toast.LENGTH_LONG).show();
        if (intent.getAction() != null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            TeslaDataSource dataSource = new TeslaDataSource(
                    context.getSharedPreferences(MainActivity.SP, Context.MODE_PRIVATE));
            ClimateTask task = new ClimateTask();
            task.execute(dataSource);
        } else {
            TeslaDataSource dataSource = new TeslaDataSource(
                    context.getSharedPreferences(MainActivity.SP, Context.MODE_PRIVATE));
            ClimateTask task = new ClimateTask();
            task.execute(dataSource);
        }
    }

    public void setAlarm(Context context, Long time) {
        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Alarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);

        Long countDown = time - System.currentTimeMillis();
        if (countDown < -10000) {
            time += 86400000L;
        }
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(time);
        Log.w("Alarm", new Date(date.getTimeInMillis()).toLocaleString());
        Log.w("Alarm", countDown.toString());
        am.setRepeating(AlarmManager.RTC_WAKEUP, time, AlarmManager.INTERVAL_DAY, pi);
    }

    private class ClimateTask extends AsyncTask<TeslaDataSource, Void, Void> {

        @Override
        protected Void doInBackground(TeslaDataSource... teslaDataSources) {
            SharedPreferences sp = context.getSharedPreferences(MainActivity.SP, Context.MODE_PRIVATE);

            String currentKey = sp.getString("currentKey", null);
            sp.edit().putLong(currentKey, sp.getLong(currentKey, 0L) + 86400000L).apply();
            StaticClass.setCurrentKey(sp);

            teslaDataSources[0].turnOnClimate();
            Boolean on = Boolean.valueOf(sp.getString("on", null));
            if (on) {
                Long time = StaticClass.getNextAlarmTime(sp);
                Alarm alarm = new Alarm();
                alarm.setAlarm(context, time);
            }
            return null;
        }
    }

}

