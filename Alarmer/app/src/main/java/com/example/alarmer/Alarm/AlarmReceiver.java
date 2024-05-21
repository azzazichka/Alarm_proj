package com.example.alarmer.Alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {




    @Override
    public void onReceive(Context context, Intent intent) {
//        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Alarm_app:alarm_activity_started");
//        wl.acquire(10*60*1000L /*10 minutes*/);




        Intent alarm_activity_intent = new Intent(context, AlarmActivity.class);
        alarm_activity_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        alarm_activity_intent.putExtra("alarm_index", intent.getIntExtra("alarm_index", -1));
        alarm_activity_intent.putExtra("question", intent.getStringExtra("question"));

        context.startActivity(alarm_activity_intent);

        Log.i("azzazichka", "alarm_started");
//        wl.release();
    }
}
