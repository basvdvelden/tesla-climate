package nl.vandervelden.teslaclimate;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static final String SP = "tesla_climate";
    private static final String TAG = "MainActivity";
    private SharedPreferences sharedPref;
    private boolean canBackSpace = false;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setListeners();
        sharedPref = getApplicationContext()
                .getSharedPreferences(SP, Context.MODE_PRIVATE);
        if (sharedPref.getString("accessToken", null) == null) {
            sharedPref.edit().putString("accessToken", "15ea7aece57354c3abfb2d464c58915028461b40fb614f8f4a274cdc84994d05")
                    .apply();
        }
        if (sharedPref.getString("refreshToken", null) == null) {
            sharedPref.edit().putString("refreshToken", "9d1242ae00718bf5a4083e9869ea21bba0e86e47eef57e2a636c105b07953309")
                    .apply();
        }
        long time1 = sharedPref.getLong("time1", 0L);
        long time2 = sharedPref.getLong("time2", 0L);
        String on = sharedPref.getString("on", null);
        if (time1 != 0L) {
            final EditText time1Text = findViewById(R.id.time1);
            time1Text.setText(msToHoursAndMinutesString(time1));
        }
        if (time2 != 0L) {
            final EditText time2Text = findViewById(R.id.time2);
            time2Text.setText(msToHoursAndMinutesString(time2));
        }
        if (on != null) {
            final Switch switch1 = findViewById(R.id.switch1);
            switch1.setChecked(Boolean.valueOf(on));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private String msToHoursAndMinutesString(long ms) {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(ms);
        long hours = date.get(Calendar.HOUR_OF_DAY);
        long minutes = date.get(Calendar.MINUTE);
        String hourString = Long.toString(hours);
        String minuteString = Long.toString(minutes);
        if (hourString.length() < 2) {
            hourString = "0" + hourString;
        }
        if (minuteString.length() < 2) {
            minuteString = "0" + minuteString;
        }
        return String.format(Locale.forLanguageTag("nl_NL"),"%s:%s", hourString, minuteString);
    }

    private void setListeners() {
        final EditText time1 = findViewById(R.id.time1);
        final EditText time2 = findViewById(R.id.time2);
        final Switch onOff = findViewById(R.id.switch1);
        time1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i(TAG, "text changed");
                String timeText = time1.getText().toString();
                if (timeText.length() == 1) {
                    canBackSpace = false;
                } else if (!canBackSpace && timeText.length() == 2) {
                    s.append(':');
                    canBackSpace = true;
                } else if (timeText.length() == 5) {
                    Calendar today = Calendar.getInstance();
                    String[] parts = timeText.split(":");
                    today.set(Calendar.HOUR_OF_DAY, Integer.valueOf(parts[0]));
                    today.set(Calendar.MINUTE, Integer.valueOf(parts[1]));
                    Log.w(TAG, today.getTime().toLocaleString());
                    sharedPref.edit().putLong("time1", today.getTimeInMillis()).apply();

                }
            }
        });
        time2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i(TAG, "text changed");
                String timeText = time2.getText().toString();
                if (timeText.length() == 1) {
                    canBackSpace = false;
                } else if (!canBackSpace && timeText.length() == 2) {
                    s.append(':');
                    canBackSpace = true;
                } else if (timeText.length() == 5) {
                    Calendar today = Calendar.getInstance();
                    String[] parts = timeText.split(":");
                    today.set(Calendar.HOUR_OF_DAY, Integer.valueOf(parts[0]));
                    today.set(Calendar.MINUTE, Integer.valueOf(parts[1]));
                    Log.w(TAG, today.getTime().toLocaleString());

                    sharedPref.edit().putLong("time2", today.getTimeInMillis()).apply();

                }
            }
        });

        onOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i(TAG, "switch switched");
                sharedPref.edit().putString("on", Boolean.toString(isChecked)).apply();
                if (isChecked) {
                    Long time = StaticClass.getNextAlarmTime(sharedPref);
                    Log.d(TAG, "setting alarms...");
                    Alarm alarm = new Alarm();
                    alarm.setAlarm(MainActivity.this, time);
                    Log.d(TAG, "alarms set");
                }
            }
        });
    }


    public void makeToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private class ClimateTask extends AsyncTask<TeslaDataSource, Void, Integer> {

        @Override
        protected Integer doInBackground(TeslaDataSource... teslaDataSources) {
            return teslaDataSources[0].turnOnClimate();
        }

        @Override
        protected void onPostExecute(Integer int1) {
            makeToast(String.format("task finished %d", int1));
        }
    }
}
