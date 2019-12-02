package nl.vandervelden.teslaclimate;

import android.content.SharedPreferences;
import android.util.Log;

import java.util.Calendar;

public class StaticClass {
    private static final String TAG = "StaticClass";
    public static String nextKey;

    public static Long getNextAlarmTime(SharedPreferences sp) {
        Long time1 = sp.getLong("time1", 1L);
        Long time2 = sp.getLong("time2", 1L);

        Calendar date1 = Calendar.getInstance();
        Calendar date2 = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        int day = now.get(Calendar.DAY_OF_YEAR);
        date1.setTimeInMillis(time1);
        date1.set(Calendar.DAY_OF_YEAR, day);
        date2.setTimeInMillis(time2);
        date2.set(Calendar.DAY_OF_YEAR, day);

        Long date1Diff = date1.getTimeInMillis() - now.getTimeInMillis();
        Long date2Diff = date2.getTimeInMillis() - now.getTimeInMillis();
        if (date1Diff > 1000000000L || date1Diff < -1000000000L) {
            int year = now.get(Calendar.YEAR);
            date1.set(Calendar.YEAR, year);
        }
        if (date2Diff > 1000000000L || date2Diff < -1000000000L) {
            int year = now.get(Calendar.YEAR);
            date2.set(Calendar.YEAR, year);
        }

        Log.i(TAG, now.getTime().toLocaleString());
        Log.i(TAG, date1.getTime().toLocaleString());
        Log.i(TAG, date2.getTime().toLocaleString());
        Log.i(TAG, Boolean.toString((time1 > time2)));
        if (date1.after(now) && date2.after(now)) {
            Log.d(TAG, "in before dates");
            if (date1.after(date2.getTimeInMillis())) {
                Log.d(TAG, "date 2 earlier");
                setCurrentKey(sp, "time2");
                nextKey = "time1";
                return time2;
            }
            Log.d(TAG, "date 1 earlier");
            setCurrentKey(sp, "time1");
            nextKey = "time2";
            return time1;
        } else if (date1.before(now) && date2.after(now) || date1.after(now) && date2.before(now)) {
            Log.d(TAG, "in between dates");
            if (date1.before(now)) {
                Log.d(TAG, "date 2 earlier");
                setCurrentKey(sp, "time2");
                nextKey = "time1";
                return time2;
            }
            Log.d(TAG, "date 1 earlier");
            setCurrentKey(sp, "time1");
            nextKey = "time2";
            return time1;
        } else if (date1.before(now) && date2.before(now)) {
            Log.d(TAG, "in after dates");
            date1.add(Calendar.DATE, 1);
            date2.add(Calendar.DATE, 1);
            if (date1.before(date2)) {
                Log.d(TAG, "date 1 earlier");
                setCurrentKey(sp, "time1");
                nextKey = "time2";
                return time1;
            }
            Log.d(TAG, "date 2 earlier");
            setCurrentKey(sp, "time2");
            nextKey = "time1";
            return time2;
        }
        throw new RuntimeException("Get next alarm should have returned in if statement");
    }

    public static void setCurrentKey(SharedPreferences sp) {
        Log.d(TAG, "Current key = " + nextKey);
        sp.edit().putString("currentKey", nextKey).apply();
    }

    private static void setCurrentKey(SharedPreferences sp, String key) {
        Log.d(TAG, "Current key = " + key);
        sp.edit().putString("currentKey", key).apply();
    }
}
