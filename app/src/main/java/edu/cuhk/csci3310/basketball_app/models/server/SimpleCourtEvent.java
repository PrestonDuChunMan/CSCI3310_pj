package edu.cuhk.csci3310.basketball_app.models.server;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class SimpleCourtEvent {
    protected final int id;
    protected final String title;
    protected final int time;

    public SimpleCourtEvent(int id, String title, int time) {
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

    public LocalDateTime getTime() {
        return LocalDateTime.ofEpochSecond(this.time, 0, ZoneOffset.UTC);
    }

    public String getFormattedTime() {
        return this.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
