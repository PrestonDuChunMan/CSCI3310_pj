package edu.cuhk.csci3310.basketball_app;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import edu.cuhk.csci3310.basketball_app.room.BasketballDatabase;
import edu.cuhk.csci3310.basketball_app.room.Subscription;
import edu.cuhk.csci3310.basketball_app.room.SubscriptionDao;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CourtEventReceiver extends BroadcastReceiver {
    private static final long[] OFFSETS = new long[] { 0, 1, 24, 168 };
    private static final String TAG = "court_event_receiver";
    public static final int NOTIFICATION_ID = 69420;
    public static final String CHANNEL_ID = "court_event";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "I received");

        NotificationManagerCompat compat = NotificationManagerCompat.from(context);
        compat.createNotificationChannel(new NotificationChannel(CourtEventReceiver.CHANNEL_ID, "Court Event Notification Channel", NotificationManager.IMPORTANCE_DEFAULT));

        BasketballDatabase db = BasketballDatabase.getInstance(context);
        SubscriptionDao dao = db.subscriptionDao();
        Handler mainHandler = new Handler(context.getMainLooper());

        if (intent.hasExtra("id")) {
            int id = intent.getIntExtra("id", -1);
            if (id < 0) return;
            String name = intent.getStringExtra("name");
            String time = intent.getStringExtra("time");
            long notifId = intent.getLongExtra("notifId", 0);
            long offset = intent.getLongExtra("offset", -1);
            dao.get(id).doOnSuccess(subscription -> {
                if (subscription.notifId != notifId) return;
                mainHandler.post(() -> {
                    Intent notifIntent = new Intent(context, CourtFinderActivity.class);
                    notifIntent.putExtra("lat", subscription.lat);
                    notifIntent.putExtra("lon", subscription.lon);
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addNextIntentWithParentStack(notifIntent);
                    PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                    Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                            .setSmallIcon(R.drawable.basketball_outline)
                            .setContentTitle("Event Reminder")
                            .setContentText(String.format("%s is happening on %s", name, time))
                            .setContentIntent(pendingIntent)
                            .build();

                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
                        return;
                    compat.notify(NOTIFICATION_ID, notification);
                });
                if (offset == 0)
                    dao.delete(subscription.eventId).subscribeOn(Schedulers.io()).subscribe();
            }).subscribeOn(Schedulers.io()).subscribe();
        } else {
            // we received a boot event
            dao.getAll().doOnSuccess(list -> {
                for (Subscription sub : list) {
                    mainHandler.post(() -> scheduleNotification(context, sub));
                }
                Log.d(TAG, "Processed " + list.size() + " events");
            }).subscribeOn(Schedulers.io()).subscribe();
        }
    }

    private void scheduleNotification(Context context, Subscription subscription) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (!alarmManager.canScheduleExactAlarms()) return;
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime eventTime = subscription.time;
        for (long offset : OFFSETS) {
            Intent intent = new Intent(context, CourtEventReceiver.class);
            intent.putExtra("id", subscription.eventId);
            intent.putExtra("name", subscription.name);
            intent.putExtra("time", subscription.time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            intent.putExtra("notifId", subscription.notifId);
            intent.putExtra("offset", offset);

            PendingIntent pending = PendingIntent.getBroadcast(
                    context,
                    CourtEventReceiver.NOTIFICATION_ID,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
            );

            ZonedDateTime notifTime = eventTime.minusHours(offset);
            if (notifTime.isAfter(now))
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, notifTime.toEpochSecond() * 1000, pending);
        }
    }
}
