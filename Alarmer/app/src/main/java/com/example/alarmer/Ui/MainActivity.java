package com.example.alarmer.Ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.alarmer.Alarm.AlarmActivity;
import com.example.alarmer.ListAlarms.Alarm;
import com.example.alarmer.ListAlarms.ListAdapter;
import com.example.alarmer.R;
import com.example.alarmer.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

    FloatingActionButton set_alarm_btn;
    ActivityMainBinding binding;
    ListAdapter listAdapter;

    ArrayList<Alarm> all_alarms = new ArrayList<>();

    public static int idx_turnOff = -1;

    String path2allAlarmsFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "AlarmApp";

    File all_alarms_file;

    ColorDrawable[] colors_alarm;


    @Override
    protected void onStart() {
        super.onStart();
        Log.i("azzazichka", idx_turnOff + " index to turn off");

        if (idx_turnOff != -1){
            Alarm current_alarm = all_alarms.get(idx_turnOff);
            current_alarm.setWaiting(false);

            all_alarms.set(idx_turnOff, current_alarm);
            idx_turnOff = -1;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        colors_alarm = new ColorDrawable[]{new ColorDrawable(Color.GRAY), new ColorDrawable(Color.GREEN)};



        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());


        set_alarm_btn = binding.setAlarm;


        set_alarm_btn.setOnClickListener(v -> {

            MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(12)
                    .setMinute(0)
                    .setTitleText("Выберите время для будильника")
                    .build();

            materialTimePicker.addOnPositiveButtonClickListener(view -> {

                start_alarm(materialTimePicker.getHour(), materialTimePicker.getMinute(), true, -1);





                listAdapter = new ListAdapter(MainActivity.this, all_alarms);
                binding.listview.setAdapter(listAdapter);
                binding.listview.setClickable(true);



                binding.listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {;
                        Alarm current_alarm = all_alarms.get(i);


                        Log.i("azzazichka", "index = " + i);

                        int hours = Integer.parseInt(current_alarm.getTime().split(":")[0]);
                        int minutes = Integer.parseInt(current_alarm.getTime().split(":")[1]);

                        if (current_alarm.getWaiting()){
                            current_alarm.setWaiting(false);
                            Toast.makeText(MainActivity.this, "cancel alarm", Toast.LENGTH_SHORT).show();

                            TransitionDrawable mTransition = new TransitionDrawable(new ColorDrawable[]{colors_alarm[1], colors_alarm[0]});
                            view.findViewById(R.id.back_itm_alrm).setBackground(mTransition);
                            mTransition.startTransition(500);


                            cancel_alarm(current_alarm.getId());


                        } else{
                            current_alarm.setWaiting(true);

                            TransitionDrawable mTransition = new TransitionDrawable(colors_alarm);
                            view.findViewById(R.id.back_itm_alrm).setBackground(mTransition);
                            mTransition.startTransition(500);

                            start_alarm(hours, minutes, false, i);
                        }

                        all_alarms.set(i, current_alarm);
                        Log.i("azzazichka", String.valueOf(all_alarms.size()));

                    }
                });

            });

            materialTimePicker.show(getSupportFragmentManager(), "tag_picker");


        });




    }

    private PendingIntent getAlarmInfoPendingIntent() {
        Intent alarmInfoIntent = new Intent(this, MainActivity.class);
        alarmInfoIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        final int intent_id = (int) System.currentTimeMillis();

        return PendingIntent.getActivity(this, intent_id, alarmInfoIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private PendingIntent getAlarmActionPendingIntent(Date time, boolean create_new, int idx) {
        Intent intent = new Intent(this, AlarmActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        final int intent_id = (int) System.currentTimeMillis();

        if (idx == -1){
            idx = all_alarms.size();
        }

        if (create_new){
            all_alarms.add(new Alarm(sdf.format(time), true, intent_id));
        } else {
            Alarm cur_alarm = all_alarms.get(idx);
            cur_alarm.setId(intent_id);
            all_alarms.set(idx, cur_alarm);
        }

        for (Alarm alarmik : all_alarms){
            Log.i("azzazichka", alarmik.getTime());
        }

        intent.putExtra("alarm_index", idx);

        return PendingIntent.getActivity(this, intent_id, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    public void cancel_alarm(int intent_id){
        Intent intent = new Intent(this, AlarmActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, intent_id, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

    }

    public void start_alarm(int hours, int minutes, boolean create_new, int idx){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.HOUR_OF_DAY, hours);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), getAlarmInfoPendingIntent());

        alarmManager.setAlarmClock(alarmClockInfo, getAlarmActionPendingIntent(calendar.getTime(), create_new, idx));

        Toast.makeText(this, "Будильник установлен на " + sdf.format(calendar.getTime()), Toast.LENGTH_SHORT).show();
    }


}