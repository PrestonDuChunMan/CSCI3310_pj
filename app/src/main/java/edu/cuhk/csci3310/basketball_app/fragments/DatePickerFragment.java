package edu.cuhk.csci3310.basketball_app.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.time.ZonedDateTime;
import java.util.Calendar;

// referred to an android studio guide
// https://developer.android.com/develop/ui/views/components/pickers
public class DatePickerFragment extends DialogFragment {
    private final ZonedDateTime dateTime;
    private final DatePickerDialog.OnDateSetListener handler;

    public DatePickerFragment(@Nullable ZonedDateTime dateTime, DatePickerDialog.OnDateSetListener handler) {
        this.dateTime = dateTime;
        this.handler = handler;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int year = dateTime != null ? dateTime.getYear() : calendar.get(Calendar.YEAR);
        int month = dateTime != null ? dateTime.getMonthValue() : calendar.get(Calendar.MONTH);
        int day = dateTime != null ? dateTime.getDayOfMonth() : calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(requireContext(), this.handler, year, month - 1, day);
    }
}
