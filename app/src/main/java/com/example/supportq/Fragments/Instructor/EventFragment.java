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
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.supportq.Models.Event;
import com.example.supportq.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.util.Calendar;

public class EventFragment extends Fragment {
    private Button btnDatePicker;
    private Button btnStartTime;
    private Button btnEndTime;
    private Button btnSubmit;
    private TextView tvEndTime;
    private TextView tvStartTime;
    private TextView tvDate;
    private DialogFragment timePicker;
    private EditText etAdditionalInfo;
    private Event event;
    private String eventType;

    public EventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        radioButtonListener(view);
        return view;
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

        setUpSubmitButtonForWhenClicked();
    }

    public void radioButtonListener(View view) {
        RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.workshop:
                        eventType = getString(R.string.workshop);
                        break;
                    case R.id.office_hour:
                        eventType = getString(R.string.office_hour);
                        break;
                }
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
        btnSubmit = views.findViewById(R.id.btnSubmit);
        etAdditionalInfo = views.findViewById(R.id.etAdditionalInfo);
    }

    public void setUpSubmitButtonForWhenClicked() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveEvent();
            }
        });
    }

    private void saveEvent() {
        event = new Event();
        verifyInput();
        event.setStartDate(tvDate.getText().toString());
        event.setEndTime(tvEndTime.getText().toString());
        event.setStartTime(tvStartTime.getText().toString());
        event.setUser(ParseUser.getCurrentUser());
        if (!etAdditionalInfo.getText().toString().isEmpty()) {
            event.setAdditionalInfo(etAdditionalInfo.getText().toString());
        }
        ParseFile parseFile = ParseUser.getCurrentUser().getParseFile("profilePicture");
        if (parseFile != null) {
            event.setImage(parseFile);
        }
        if (eventType != null) {
            event.setEvent(eventType);
            event.saveInBackground();
            Toast.makeText(getContext(), getContext().getString(R.string.success_message), Toast.LENGTH_SHORT).show();
        }
    }

    public void verifyInput() {
        if (tvDate.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), getContext().getString(R.string.date_error), Toast.LENGTH_SHORT).show();
            return;
        } else if (tvStartTime.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), getContext().getString(R.string.start_error), Toast.LENGTH_SHORT).show();
            return;
        } else if (tvEndTime.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), getContext().getString(R.string.end_error), Toast.LENGTH_SHORT).show();
            return;
        } else if (eventType == null) {
            Toast.makeText(getContext(), getContext().getString(R.string.event_type_error), Toast.LENGTH_SHORT).show();
            return;
        }
    }
}