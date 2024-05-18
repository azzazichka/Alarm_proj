package com.example.alarmer.Alarm;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.alarmer.ListAlarms.Alarm;
import com.example.alarmer.R;
import com.example.alarmer.Ui.MainActivity;
import com.example.alarmer.databinding.ActivityAlarmBinding;

import java.util.ArrayList;

public class AlarmActivity extends AppCompatActivity {

    Ringtone ringtone;

    Vibrator v;
    long[] pattern = {0, 500, 500};

    Button cancel_btn;

    boolean answered = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_alarm);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            keyguardManager.requestDismissKeyguard(this, null);

        } else {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }


        cancel_btn = findViewById(R.id.check_answer);

        cancel_btn.setOnClickListener(view -> {
            answered = true;
            if (ringtone != null && ringtone.isPlaying()) {
                ringtone.stop();
            }
            v.cancel();

            Intent main_activity_intent = new Intent(this, MainActivity.class);
            main_activity_intent.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT | FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);

            MainActivity.idx_turnOff = getIntent().getIntExtra("alarm_index", -1);

            Log.i("azzazichka", "alarm_index = " + MainActivity.idx_turnOff + " alarm_activity");

            startActivity(main_activity_intent);
        });

        Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(this, notificationUri);

        if (ringtone == null) {
            notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            ringtone = RingtoneManager.getRingtone(this, notificationUri);
        }

        if (ringtone != null && !ringtone.isPlaying()) {
            ringtone.play();
        }


        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        v.vibrate(pattern, 0);

//////        startLockTask();


    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("azzazichka", "stopped alarm activity");

        if (ringtone != null && ringtone.isPlaying()){
            ringtone.stop();
        }
        v.cancel();

        if (!answered) {

            Intent alarm_intent = new Intent();
            alarm_intent.setAction("com.example.alarmer.RELOAD_PAGE");
            alarm_intent.putExtra("alarm_index", getIntent().getIntExtra("alarm_index", -1));
            sendBroadcast(alarm_intent);
        }
    }

    @Override
    public void onBackPressed() {}
}
