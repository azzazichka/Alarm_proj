package com.example.alarmer.Alarm;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

import static androidx.core.app.ActivityCompat.startActivityForResult;
import static androidx.core.content.ContextCompat.startActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.alarmer.Ui.MainActivity;

public class Alarm_controller extends BroadcastReceiver {




    @Override
    public void onReceive(Context context, Intent intent) {
//        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Alarm_app:alarm_activity_started");
//        wl.acquire(10*60*1000L /*10 minutes*/);






        Intent alarm_activity_intent = new Intent(context, AlarmActivity.class);
        alarm_activity_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        alarm_activity_intent.putExtra("alarm_index", intent.getIntExtra("alarm_index", -1));
        alarm_activity_intent.putExtra("array_size", intent.getIntExtra("array_size", -1));

        context.startActivity(alarm_activity_intent);

        Log.i("azzazichka", "alarm_started");
//        wl.release();
    }
}
