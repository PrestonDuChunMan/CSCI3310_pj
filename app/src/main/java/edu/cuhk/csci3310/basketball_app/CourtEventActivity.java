package edu.cuhk.csci3310.basketball_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

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

    private TextView titleView, timeView, descriptionView;
    private Button subButton;

    private ApiHandler apiHandler;
    private SubscriptionDao subscriptionDao;

    private int mCourtId, mEventId;
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
        BasketballDatabase db = Room.databaseBuilder(this.getApplicationContext(), BasketballDatabase.class, "basketball").build();
        this.subscriptionDao = db.subscriptionDao();

        Intent intent = getIntent();
        this.mCourtId = intent.getIntExtra("courtId", -1);
        this.mEventId = intent.getIntExtra("eventId", -1);
        this.titleView.setText(intent.getStringExtra("title"));
        this.timeView.setText(intent.getStringExtra("time"));
        this.descriptionView.setText("...");

        if (mCourtId >= 0 && mEventId >= 0) {
            // Get detailed event from server
            Call<CourtEventResponse> call = this.apiHandler.getCourtEvent(mCourtId, mEventId);
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
                    descriptionView.setText(mEvent.getDescription());
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
        if (this.mEventId < 0 || this.mCourtId < 0) return;
        this.subButton.setEnabled(false);
        Completable completable;
        if (this.mSubbed) completable = subscriptionDao.delete(this.mEventId);
        else {
            Subscription sub = new Subscription(this.mEventId, this.mCourtId, this.mEvent.getTime());
            completable = subscriptionDao.insert(sub);
        }
        Handler mainHandler = new Handler(this.getMainLooper());
        completable.doOnComplete(() -> {
            mSubbed = !mSubbed;
            mainHandler.post(() -> {
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
}
