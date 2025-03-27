package edu.cuhk.csci3310.basketball_app.models.server;

import androidx.annotation.Nullable;

import java.util.List;

public class CourtEventListResponse extends ServerResponse<List<SimpleCourtEvent>> {
    public CourtEventListResponse(boolean success, @Nullable String error, @Nullable List<SimpleCourtEvent> data) {
        super(success, error, data);
    }
}
