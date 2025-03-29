package edu.cuhk.csci3310.basketball_app;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import edu.cuhk.csci3310.basketball_app.room.BasketballDatabase;
import edu.cuhk.csci3310.basketball_app.room.Subscription;
import edu.cuhk.csci3310.basketball_app.room.SubscriptionDao;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CourtEventReceiver extends BroadcastReceiver {
    private static final String TAG = "court_event_receiver";
    public static final int NOTIFICATION_ID = 69420;
    public static final String CHANNEL_ID = "court_event";

    @Override
    public void onReceive(Context context, Intent parent) {
        NotificationManagerCompat compat = NotificationManagerCompat.from(context);
        compat.createNotificationChannel(new NotificationChannel(CourtEventReceiver.CHANNEL_ID, "Court Event Notification Channel", NotificationManager.IMPORTANCE_DEFAULT));

        BasketballDatabase db = BasketballDatabase.getInstance(context);
        SubscriptionDao dao = db.subscriptionDao();
        Handler mainHandler = new Handler(context.getMainLooper());

        if (parent.hasExtra("id")) {
            int id = parent.getIntExtra("id", -1);
            if (id < 0) return;
            String name = parent.getStringExtra("name");
            String time = parent.getStringExtra("time");
            long notifId = parent.getLongExtra("notifId", 0);
            dao.get(id).doOnSuccess(subscription -> {
                if (subscription.notifId != notifId) return;
                mainHandler.post(() -> {
                    Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                            .setSmallIcon(R.drawable.basketball_outline)
                            .setContentTitle("Event Reminder")
                            .setContentText(String.format("%s is happening on %s", name, time))
                            .build();

                    if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
                        return;
                    compat.notify(NOTIFICATION_ID, notification);
                });
            }).subscribeOn(Schedulers.io()).subscribe();
        } else {
            // we received a boot event
            dao.getAll().doOnSuccess(list -> {
                for (Subscription sub : list) {
                    mainHandler.post(() -> scheduleNotification(context, sub));
                }
            }).subscribeOn(Schedulers.io()).subscribe();
        }
    }

    private void scheduleNotification(Context context, Subscription subscription) {
        Intent intent = new Intent(context, CourtEventReceiver.class);
        intent.putExtra("id", subscription.eventId);
        intent.putExtra("name", subscription.name);
        intent.putExtra("time", subscription.time);
        intent.putExtra("notifId", subscription.notifId);

        PendingIntent pending = PendingIntent.getBroadcast(
                context,
                CourtEventReceiver.NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager.canScheduleExactAlarms()) {
            Log.d(TAG, "scheduling notification");
            long now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            // set 1 week
            long offset = subscription.time.toEpochSecond(ZoneOffset.UTC) - (60 * 60 * 24 * 7) - now;
            if (offset > 0)
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, offset * 1000, pending);
            // set 1 day
            offset = subscription.time.toEpochSecond(ZoneOffset.UTC) - (60 * 60 * 24) - now;
            if (offset > 0)
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, offset * 1000, pending);
            // set 1 hour
            offset = subscription.time.toEpochSecond(ZoneOffset.UTC) - (60 * 60) - now;
            if (offset > 0)
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, offset * 1000, pending);
            // set exact
            offset = subscription.time.toEpochSecond(ZoneOffset.UTC) - now;
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, offset * 1000, pending);
        }
    }
}
