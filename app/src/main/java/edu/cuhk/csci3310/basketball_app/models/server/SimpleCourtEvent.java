package edu.cuhk.csci3310.basketball_app.models.server;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class SimpleCourtEvent {
    protected final int id;
    protected final String title;
    protected final long time;

    public SimpleCourtEvent(int id, String title, long time) {
        this.id = id;
        this.title = title;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public ZonedDateTime getTime() {
        return ZonedDateTime.ofInstant(Instant.ofEpochSecond(this.time), ZoneId.systemDefault());
    }

    public String getFormattedTime() {
        return this.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
