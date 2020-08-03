package com.example.supportq.Fragments.Instructor;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.supportq.R;

import java.text.DateFormat;
import java.util.Calendar;

public class EventFragment extends Fragment {
    private Button btnDatePicker;
    private Button btnStartTime;
    private Button btnEndTime;
    private TextView tvEndTime;
    private TextView tvStartTime;
    private TextView tvDate;
    private DialogFragment timePicker;

    public EventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setViews(view);
        final DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
                tvDate.setText(currentDate);
            }
        };
        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment(mDateSetListener);
                datePicker.show(getFragmentManager(), "date picker");
            }
        });

        final TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                    tvStartTime.setText(hour + ":" + minute);
            }
        };

       final TimePickerDialog.OnTimeSetListener onEndTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
           @Override
           public void onTimeSet(TimePicker timePicker, int hour, int minute) {
               tvEndTime.setText(hour + ":" + minute);
           }
       };
        btnStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker = new TimePickerFragment(onTimeSetListener);
                timePicker.show(getFragmentManager(), "time picker");
            }
        });

        btnEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker = new TimePickerFragment(onEndTimeSetListener);
                timePicker.show(getFragmentManager(), "time picker");
            }
        });
    }

    public void setViews(View views) {
        btnDatePicker = views.findViewById(R.id.btnDatePicker);
        btnEndTime = views.findViewById(R.id.btnEndTime);
        tvDate = views.findViewById(R.id.tvDate);
        tvStartTime = views.findViewById(R.id.tvStartTime);
        tvEndTime = views.findViewById(R.id.tvEndTime);
        btnStartTime = views.findViewById(R.id.btnStartTime);
    }
}