package edu.cuhk.csci3310.basketball_app.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Random;

@Entity
@TypeConverters(Subscription.ZonedDateTimeConverter.class)
public class Subscription {
    private static final Random RNG = new Random();

    @PrimaryKey
    public int eventId;
    public int courtId;
    public long notifId;
    public String name;
    public ZonedDateTime time;

    public Subscription(int eventId, int courtId, String name, ZonedDateTime time) {
        this.eventId = eventId;
        this.courtId = courtId;
        this.notifId = RNG.nextLong();
        this.name = name;
        this.time = time;
    }

    static class ZonedDateTimeConverter {
        @TypeConverter
        public static ZonedDateTime toZonedDateTime(Long seconds) {
            return seconds == null ? null : ZonedDateTime.ofInstant(Instant.ofEpochSecond(seconds), ZoneId.systemDefault());
        }

        @TypeConverter
        public static Long fromZonedDateTime(ZonedDateTime dateTime) {
            return dateTime == null ? null : dateTime.toEpochSecond();
        }
    }
}
