package edu.cuhk.csci3310.basketball_app.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;

// referred to an android studio guide
// https://developer.android.com/develop/ui/views/components/pickers
public class TimePickerFragment extends DialogFragment {
    private final ZonedDateTime dateTime;
    private final TimePickerDialog.OnTimeSetListener handler;

    public TimePickerFragment(@Nullable ZonedDateTime dateTime, TimePickerDialog.OnTimeSetListener handler) {
        this.dateTime = dateTime;
        this.handler = handler;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int hour = this.dateTime != null ? this.dateTime.getHour() : calendar.get(Calendar.HOUR_OF_DAY);
        int minute = this.dateTime != null ? this.dateTime.getMinute() : calendar.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(), this.handler, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }
}
