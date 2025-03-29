package edu.cuhk.csci3310.basketball_app.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Random;

@Entity
@TypeConverters(Subscription.LocalDateTimeConverter.class)
public class Subscription {
    private static final Random RNG = new Random();

    @PrimaryKey
    public int eventId;
    public int courtId;
    public long notifId;
    public String name;
    public LocalDateTime time;

    public Subscription(int eventId, int courtId, String name, LocalDateTime time) {
        this.eventId = eventId;
        this.courtId = courtId;
        this.notifId = RNG.nextLong();
        this.name = name;
        this.time = time;
    }

    static class LocalDateTimeConverter {
        @TypeConverter
        public static LocalDateTime toLocalDateTime(Long seconds) {
            return seconds == null ? null : LocalDateTime.ofEpochSecond(seconds, 0, ZoneOffset.UTC);
        }

        @TypeConverter
        public static Long fromLocalDateTime(LocalDateTime dateTime) {
            return dateTime == null ? null : dateTime.toEpochSecond(ZoneOffset.UTC);
        }
    }
}
