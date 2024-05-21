package com.example.alarmer.Ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.alarmer.Alarm.AlarmReceiver;
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

    FloatingActionButton add, add_alarm, delete_mode_btn;

    Animation fabOpen, fabClose, rotateForward, rotateBackward;

    boolean isOpen = false;

    public static boolean delete_mode = false;

    int alpha;

    FrameLayout mFrameLayout;

    Handler mHandler;


    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

    ActivityMainBinding binding;
    ListAdapter listAdapter;

    public static ArrayList<Alarm> all_alarms = new ArrayList<>();
    public static int idx_turnOff = -1;


    String path2allAlarmsFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "AlarmApp";

    File all_alarms_file;

    ColorDrawable[] colors_alarm;

    BroadcastReceiver alarm_receiver = new AlarmReceiver();

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("azzazichka", idx_turnOff + " index to turn off");

        if (idx_turnOff != -1) {
            idx_turnOff = get_alarm_by_id(idx_turnOff);
            Alarm current_alarm = all_alarms.get(idx_turnOff);
            current_alarm.setWaiting(false);
            current_alarm.setColor("green");

            all_alarms.set(idx_turnOff, current_alarm);
            listAdapter.notifyDataSetChanged();

            idx_turnOff = -1;
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
        try {
            listAdapter.notifyDataSetChanged();
        } catch (NullPointerException e) {
            Log.i("azzazichka", "it's clear");
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        colors_alarm = new ColorDrawable[]{new ColorDrawable(Color.GRAY), new ColorDrawable(Color.GREEN)};


        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());


        registerReceiver(alarm_receiver, new IntentFilter(
                "com.example.alarmer.RELOAD_PAGE"), Context.RECEIVER_EXPORTED);



        mFrameLayout = (FrameLayout) binding.flForDark;
        if (mFrameLayout.getForeground() != null) {
            mFrameLayout.getForeground().setAlpha(0);
        }


        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 1) {
                    mFrameLayout.getForeground().setAlpha((int) msg.obj);
                }
            }
        };


        add = (FloatingActionButton) binding.add;
        add_alarm = (FloatingActionButton) binding.addAlarm;
        delete_mode_btn = (FloatingActionButton) binding.deleteMode;

        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        rotateForward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);

        add.setOnClickListener(v -> animateFab());




        delete_mode_btn.setOnClickListener(v -> {
            delete_mode = !delete_mode;
            listAdapter.notifyDataSetChanged();

            animateFab();
        });


        add_alarm.setOnClickListener(v -> {
            animateFab();

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
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (delete_mode) {
                            cancel_alarm(all_alarms.get(i).getId());
                            all_alarms.remove(i);
                            listAdapter.notifyDataSetChanged();


                        } else {
                            Alarm current_alarm = all_alarms.get(i);


                            Log.i("azzazichka", "index = " + i);

                            int hours = Integer.parseInt(current_alarm.getTime().split(":")[0]);
                            int minutes = Integer.parseInt(current_alarm.getTime().split(":")[1]);

                            if (current_alarm.getWaiting()) {
                                current_alarm.setColor("grey");
                                current_alarm.setWaiting(false);
                                Toast.makeText(MainActivity.this, "cancel alarm", Toast.LENGTH_SHORT).show();

                                TransitionDrawable mTransition = new TransitionDrawable(new ColorDrawable[]{colors_alarm[1], colors_alarm[0]});
                                view.findViewById(R.id.back_itm_alrm).setBackground(mTransition);
                                mTransition.startTransition(250);

                                cancel_alarm(current_alarm.getId());

                            } else {
                                current_alarm.setColor("green");
                                current_alarm.setWaiting(true);

                                TransitionDrawable mTransition = new TransitionDrawable(colors_alarm);
                                view.findViewById(R.id.back_itm_alrm).setBackground(mTransition);
                                mTransition.startTransition(250);

                                start_alarm(hours, minutes, false, i);
                            }

                            all_alarms.set(i, current_alarm);
                            Log.i("azzazichka", String.valueOf(all_alarms.size()));
                        }
                    }
                });

            });

            materialTimePicker.show(getSupportFragmentManager(), "tag_picker");



        });


    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("azzazichka", "stopped");

    }

    private PendingIntent getAlarmInfoPendingIntent() {
        Intent alarmInfoIntent = new Intent(this, MainActivity.class);
        alarmInfoIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        final int intent_id = (int) System.currentTimeMillis();

        return PendingIntent.getActivity(this, intent_id, alarmInfoIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private PendingIntent getAlarmActionPendingIntent(Date time, boolean create_new, int idx) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        final int intent_id = (int) System.currentTimeMillis();

        if (idx == -1) {
            idx = all_alarms.size();
        }

        if (create_new) {
            all_alarms.add(new Alarm(sdf.format(time), true, intent_id, "gray"));
        } else {
            Alarm cur_alarm = all_alarms.get(idx);
            cur_alarm.setId(intent_id);
            all_alarms.set(idx, cur_alarm);
        }


        intent.putExtra("alarm_index", all_alarms.get(idx).getId());



        return PendingIntent.getBroadcast(this, intent_id, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    public void cancel_alarm(int intent_id) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, intent_id, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

    }



    public void start_alarm(int hours, int minutes, boolean create_new, int idx) {
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

    private void animateFab() {
        if (isOpen) {
            add.startAnimation(rotateBackward);
            add_alarm.startAnimation(fabClose);
            delete_mode_btn.startAnimation(fabClose);
            add_alarm.setClickable(false);
            delete_mode_btn.setClickable(false);
            isOpen = false;

            alpha = 200;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (alpha > 0) {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Message msg = mHandler.obtainMessage();
                        msg.what = 1;
                        alpha -= 1;
                        msg.obj = alpha;
                        mHandler.sendMessage(msg);
                    }
                }
            }).start();


        } else {
            add.startAnimation(rotateForward);
            add_alarm.startAnimation(fabOpen);
            delete_mode_btn.startAnimation(fabOpen);
            add_alarm.setClickable(true);
            delete_mode_btn.setClickable(true);
            isOpen = true;

            alpha = 0;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (alpha < 200) {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Message msg = mHandler.obtainMessage();
                        msg.what = 1;
                        alpha += 1; //add 1 every time, gradually darken
                        msg.obj = alpha;
                        mHandler.sendMessage(msg);
                    }
                }
            }).start();

        }

    }

    public int get_alarm_by_id(int id) {
        for (int position = 0; position < all_alarms.size(); position++) {
            if (all_alarms.get(position).getId() == id) {
                return position;
            }
        }
        return -1;
    }

}