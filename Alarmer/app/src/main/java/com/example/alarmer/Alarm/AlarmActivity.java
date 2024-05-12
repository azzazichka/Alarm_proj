package com.example.alarmer.Alarm;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
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



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        binding = ActivityAlarmBinding.inflate(getLayoutInflater());

        setContentView(R.layout.activity_alarm);



        cancel_btn = findViewById(R.id.check_answer);

        cancel_btn.setOnClickListener(view -> {
            Toast.makeText(this, "cancel btn clicked " + ringtone.isPlaying(), Toast.LENGTH_SHORT).show();
            if (ringtone != null && ringtone.isPlaying()){
                ringtone.stop();
            }
            v.cancel();

            Intent main_activity_intent = new Intent(this, MainActivity.class);
            main_activity_intent.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT | FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);
//            int idx_alarmOff = getIntent().getIntExtra("alarm_index", -1);
//            main_activity_intent.putExtra("alarm_index2turnOff", idx_alarmOff);

//            Log.i("azzazichka", String.valueOf(idx_alarmOff));
            MainActivity.idx_turnOff = getIntent().getIntExtra("alarm_index", -1);



            startActivity(main_activity_intent);
        });

        Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(this, notificationUri);

        if (ringtone == null){
            notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            ringtone = RingtoneManager.getRingtone(this, notificationUri);
        }

        if (ringtone != null){
            ringtone.play();
        }


        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        v.vibrate(pattern, 0);

//        startLockTask();

    }

    @Override
    protected void onDestroy() {

        if (ringtone != null && ringtone.isPlaying()){
            ringtone.stop();
            v.cancel();
        }
        super.onDestroy();


    }
}
