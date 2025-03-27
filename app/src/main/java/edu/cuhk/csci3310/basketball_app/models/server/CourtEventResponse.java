package edu.cuhk.csci3310.basketball_app.models.server;

import androidx.annotation.Nullable;

public class CourtEventResponse extends ServerResponse<DetailedCourtEvent> {
    public CourtEventResponse(boolean success, @Nullable String error, @Nullable DetailedCourtEvent data) {
        super(success, error, data);
    }
}
