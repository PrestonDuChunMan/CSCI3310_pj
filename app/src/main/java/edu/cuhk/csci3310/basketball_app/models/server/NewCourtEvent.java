package edu.cuhk.csci3310.basketball_app.models.server;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class NewCourtEvent {
    public final String title, description;
    public final long time;

    public NewCourtEvent(String title, String description, ZonedDateTime dateTime) {
        this.title = title;
        this.description = description;
        this.time = dateTime.toEpochSecond();
    }
}
