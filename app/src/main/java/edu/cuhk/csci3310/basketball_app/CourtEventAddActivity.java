package edu.cuhk.csci3310.basketball_app;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import edu.cuhk.csci3310.basketball_app.api.ApiHandler;
import edu.cuhk.csci3310.basketball_app.fragments.DatePickerFragment;
import edu.cuhk.csci3310.basketball_app.fragments.TimePickerFragment;
import edu.cuhk.csci3310.basketball_app.models.server.NewCourtEvent;
import edu.cuhk.csci3310.basketball_app.models.server.ServerResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourtEventAddActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    public static final String TAG = "court_event_add";
    private EditText nameView, descriptionView;
    private TextView timeView;

    private ApiHandler apiHandler;

    private ZonedDateTime mDateTime;
    private double mLat, mLon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_court_event_add);

        this.nameView = findViewById(R.id.value_name);
        this.descriptionView = findViewById(R.id.value_description);
        this.timeView = findViewById(R.id.value_time);
        this.apiHandler = ApiHandler.getInstance();

        Intent intent = getIntent();
        this.mLat = intent.getDoubleExtra("lat", 0);
        this.mLon = intent.getDoubleExtra("lon", 0);
        this.mDateTime = ZonedDateTime.now().withSecond(0).withNano(0);

        this.setTimeViewText();

        findViewById(R.id.button_time).setOnClickListener(view -> {
            DatePickerFragment fragment = new DatePickerFragment(this.mDateTime, this);
            fragment.show(getSupportFragmentManager(), "datePicker");
        });
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        this.mDateTime = this.mDateTime.withYear(year).withMonth(month).withDayOfMonth(day);
        this.setTimeViewText();

        TimePickerFragment fragment = new TimePickerFragment(this.mDateTime, this);
        fragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        this.mDateTime = this.mDateTime.withHour(hour).withMinute(minute);
        this.setTimeViewText();
    }

    private void setTimeViewText() {
        this.timeView.setText(this.mDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }

    public void cancel(View view) {
        finish();
    }

    public void add(View view) {
        Log.d(TAG, String.format("adding event to (%.4f, %.4f)", this.mLat, this.mLon));
        Call<ServerResponse<Void>> call = this.apiHandler.addCourtEvent(this.mLat, this.mLon, new NewCourtEvent(this.nameView.getText().toString(), this.descriptionView.getText().toString(), this.mDateTime));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ServerResponse<Void>> call, Response<ServerResponse<Void>> response) {
                if (response.isSuccessful() && response.body().isSuccess()) {
                    Toast.makeText(CourtEventAddActivity.this, "Added new event to court", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CourtEventAddActivity.this, "Failed to add new event to court", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse<Void>> call, Throwable t) {
                Toast.makeText(CourtEventAddActivity.this, "Failed to add new event to court", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to add new event to court", t);
            }
        });
    }
}
