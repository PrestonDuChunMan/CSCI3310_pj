package edu.cuhk.csci3310.basketball_app.models.server;

public class DetailedCourtEvent extends SimpleCourtEvent {
    private final int courtId;
    private final String description;

    public DetailedCourtEvent(int id, int courtId, String title, String description, int time) {
        super(id, title, time);
        this.courtId = courtId;
        this.description = description;
    }

    public int getCourtId() {
        return courtId;
    }

    public String getDescription() {
        return description;
    }
}
