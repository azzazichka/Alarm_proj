package com.example.alarmer.ListAlarms;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.alarmer.R;
import com.example.alarmer.Ui.MainActivity;

public class ListAdapter extends ArrayAdapter<Alarm> {
    public ListAdapter(@NonNull Context context, ArrayList<Alarm> dataArrayList) {
        super(context, R.layout.alarm_item, dataArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
//        Toast.makeText(getContext().getApplicationContext(), "position " + position, Toast.LENGTH_SHORT).show();
        Alarm listData = getItem(position);

        if (view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.alarm_item, parent, false);
        }

        TextView time_of_alarm = view.findViewById(R.id.time_of_alarm);
        TextView ring_in = view.findViewById(R.id.ring_in);

        assert listData != null;
        time_of_alarm.setText(listData.time);
        ring_in.setText("Ring in");

        return view;
    }
}