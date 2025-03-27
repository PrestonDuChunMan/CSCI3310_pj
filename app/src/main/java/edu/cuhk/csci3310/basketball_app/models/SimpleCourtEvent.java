package edu.cuhk.csci3310.basketball_app.models;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class SimpleCourtEvent {
    private final int id;
    private final String title;
    private final int time;

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
}
