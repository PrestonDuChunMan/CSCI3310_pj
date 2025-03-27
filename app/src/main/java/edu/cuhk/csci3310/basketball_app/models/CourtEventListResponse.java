package edu.cuhk.csci3310.basketball_app.models;

import androidx.annotation.Nullable;

import java.util.List;

public class CourtEventListResponse {
    private final boolean success;
    @Nullable
    private final String error;
    @Nullable
    private final List<SimpleCourtEvent> data;

    public CourtEventListResponse(boolean success, @Nullable String error, @Nullable List<SimpleCourtEvent> data) {
        this.success = success;
        this.error = error;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    @Nullable
    public String getError() {
        return error;
    }

    @Nullable
    public List<SimpleCourtEvent> getData() {
        return data;
    }
}
