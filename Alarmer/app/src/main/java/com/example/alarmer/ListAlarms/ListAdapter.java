package com.example.alarmer.ListAlarms;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.alarmer.R;
import com.example.alarmer.Ui.MainActivity;

public class ListAdapter extends ArrayAdapter<Alarm> {
    ArrayList<Alarm> all_alarms;
    ColorDrawable[] colors_alarm = new ColorDrawable[]{new ColorDrawable(Color.GRAY), new ColorDrawable(Color.GREEN)};

    public ListAdapter(@NonNull Context context, ArrayList<Alarm> dataArrayList) {
        super(context, R.layout.alarm_item, dataArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
//        Toast.makeText(getContext().getApplicationContext(), "position " + position, Toast.LENGTH_SHORT).show();
        Alarm listData = getItem(position);

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.alarm_item, parent, false);
        }

        TextView time_of_alarm = view.findViewById(R.id.time_of_alarm);
        TextView ring_in = view.findViewById(R.id.ring_in);

        assert listData != null;
        time_of_alarm.setText(listData.time);
        ring_in.setText("");

        all_alarms = MainActivity.all_alarms;
        Alarm current_alarm = all_alarms.get(position);

        if (current_alarm.getWaiting()) {
            if (Objects.equals(current_alarm.getColor(), "gray")) {
                TransitionDrawable mTransition = new TransitionDrawable(colors_alarm);
                view.findViewById(R.id.back_itm_alrm).setBackground(mTransition);
                mTransition.startTransition(500);
                current_alarm.setColor("green");
            } else {
                view.findViewById(R.id.back_itm_alrm).setBackground(new ColorDrawable(Color.GREEN));

            }
            MainActivity.all_alarms.set(position, current_alarm);
        } else {
            if (Objects.equals(all_alarms.get(position).getColor(), "green")) {
                TransitionDrawable mTransition = new TransitionDrawable(new ColorDrawable[]{colors_alarm[1], colors_alarm[0]});
                view.findViewById(R.id.back_itm_alrm).setBackground(mTransition);
                mTransition.startTransition(500);
                current_alarm.setColor("gray");
                MainActivity.all_alarms.set(position, current_alarm);
            } else {
                view.findViewById(R.id.back_itm_alrm).setBackground(new ColorDrawable(Color.GRAY));

            }
        }

        return view;
    }
}