package edu.cuhk.csci3310.basketball_app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.room.Room;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import edu.cuhk.csci3310.basketball_app.api.ApiHandler;
import edu.cuhk.csci3310.basketball_app.models.server.CourtEventResponse;
import edu.cuhk.csci3310.basketball_app.models.server.DetailedCourtEvent;
import edu.cuhk.csci3310.basketball_app.room.BasketballDatabase;
import edu.cuhk.csci3310.basketball_app.room.Subscription;
import edu.cuhk.csci3310.basketball_app.room.SubscriptionDao;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourtEventActivity extends AppCompatActivity {
    private static final String TAG = "court_event";
    private static final long[] OFFSETS = new long[] { 0, 1, 24, 168 };

    private TextView titleView, timeView, descriptionView;
    private Button subButton;

    private ApiHandler apiHandler;
    private SubscriptionDao subscriptionDao;

    private double mLat, mLon;
    private int mEventId;
    private boolean mSubbed;
    private DetailedCourtEvent mEvent;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_court_event);

        this.titleView = findViewById(R.id.value_title);
        this.timeView = findViewById(R.id.value_time);
        this.descriptionView = findViewById(R.id.value_description);
        this.subButton = findViewById(R.id.button_subscribe);

        this.apiHandler = ApiHandler.getInstance();
        BasketballDatabase db = BasketballDatabase.getInstance(this.getApplicationContext());
        this.subscriptionDao = db.subscriptionDao();

        Intent intent = getIntent();
        this.mLat = intent.getDoubleExtra("lat", 0);
        this.mLon = intent.getDoubleExtra("lon", 0);
        this.mEventId = intent.getIntExtra("eventId", -1);
        this.titleView.setText(intent.getStringExtra("title"));
        this.timeView.setText(intent.getStringExtra("time"));
        this.descriptionView.setText("...");

        if (mEventId >= 0) {
            // Get detailed event from server
            Call<CourtEventResponse> call = this.apiHandler.getCourtEvent(mLat, mLon, mEventId);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<CourtEventResponse> call, Response<CourtEventResponse> response) {
                    CourtEventResponse res = response.body();
                    if (res == null) {
                        failureToast();
                        return;
                    } else if (!res.isSuccess()) {
                        if (res.getError() != null) failureToast(res.getError());
                        else failureToast();
                        return;
                    } else if (res.getData() == null) {
                        failureToast();
                        return;
                    }
                    mEvent = res.getData();
                    titleView.setText(mEvent.getTitle());
                    timeView.setText(mEvent.getFormattedTime());
                    if (mEvent.getDescription().isBlank()) descriptionView.setText(R.string.court_event_no_desc);
                    else descriptionView.setText(mEvent.getDescription());

                    if (mEvent.getTime().isBefore(ZonedDateTime.now()))
                        subButton.setEnabled(false);
                }

                @Override
                public void onFailure(Call<CourtEventResponse> call, Throwable t) {
                    Log.e(TAG, "Failed to get court event", t);
                    failureToast();
                }
            });

            // Check if device is subscribed to this event
            this.mSubbed = false;
            Handler mainHandler = new Handler(this.getMainLooper());
            Maybe<Subscription> maybe = this.subscriptionDao.get(this.mEventId);
            maybe.doOnSuccess(subscription -> {
                // this only gets called when subscription exist
                mSubbed = true;
                mainHandler.post(() -> {
                    subButton.setText(R.string.court_unsubscribe);
                    if (subscription.time.isAfter(ZonedDateTime.now()))
                        subButton.setEnabled(true);
                });
            }).doOnComplete(() -> {
                mainHandler.post(() -> subButton.setEnabled(true));
            }).doOnError(err -> {
                Log.e(TAG, "Failed to check subscription", err);
                mainHandler.post(() -> {
                    Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                });
            }).subscribeOn(Schedulers.io()).subscribe();

            // Setup sub button click handler
            subButton.setOnClickListener(this::toggleSubscribe);
        }
    }

    private void failureToast() {
        this.failureToast("Failed to get court event");
    }

    private void failureToast(String reason) {
        Toast.makeText(this, reason, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("CheckResult")
    private void toggleSubscribe(View view) {
        if (this.mEventId < 0) return;
        this.subButton.setEnabled(false);
        Completable completable;
        long notifId;
        if (this.mSubbed) {
            notifId = 0;
            completable = subscriptionDao.delete(this.mEventId);
        } else {
            Subscription sub = new Subscription(this.mEventId, this.mLat, this.mLon, this.titleView.getText().toString(), this.mEvent.getTime());
            completable = subscriptionDao.insert(sub);
            notifId = sub.notifId;
        }
        Handler mainHandler = new Handler(this.getMainLooper());
        completable.doOnComplete(() -> {
            mSubbed = !mSubbed;
            mainHandler.post(() -> {
                if (mSubbed) scheduleNotification(notifId);
                subButton.setText(mSubbed ? R.string.court_unsubscribe : R.string.court_subscribe);
                Toast.makeText(this, getResources().getString(mSubbed ? R.string.court_subscribed : R.string.court_unsubscribed), Toast.LENGTH_SHORT).show();
            });
        }).doOnError(err -> {
            Log.e(TAG, "Failed to toggle subscription", err);
            mainHandler.post(() -> {
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            });
        }).doFinally(() -> {
            mainHandler.post(() -> subButton.setEnabled(true));
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    private void scheduleNotification(long notifId) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.POST_NOTIFICATIONS}, 0);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECEIVE_BOOT_COMPLETED}, 0);

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        if (!alarmManager.canScheduleExactAlarms()) return;
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime eventTime = this.mEvent.getTime();
        for (long offset : OFFSETS) {
            Intent intent = new Intent(this.getApplicationContext(), CourtEventReceiver.class);
            intent.putExtra("id", this.mEventId);
            intent.putExtra("name", this.mEvent.getTitle());
            intent.putExtra("time", this.mEvent.getFormattedTime());
            intent.putExtra("notifId", notifId);
            intent.putExtra("offset", offset);

            PendingIntent pending = PendingIntent.getBroadcast(
                    this.getApplicationContext(),
                    CourtEventReceiver.NOTIFICATION_ID,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
            );

            ZonedDateTime notifTime = eventTime.minusHours(offset);
            if (notifTime.isAfter(now))
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, notifTime.toEpochSecond() * 1000, pending);
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.court_event_notif_title)
                .setMessage(R.string.court_event_notif_desc)
                .setPositiveButton(R.string.ok, (dialog, i) -> dialog.dismiss())
                .show();
    }
}
