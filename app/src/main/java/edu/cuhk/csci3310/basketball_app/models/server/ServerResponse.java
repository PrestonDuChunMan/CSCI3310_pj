package edu.cuhk.csci3310.basketball_app.models.server;

import androidx.annotation.Nullable;

public class ServerResponse<DATA> {
    protected final boolean success;
    @Nullable
    protected final String error;
    @Nullable
    protected final DATA data;

    public ServerResponse(boolean success, @Nullable String error, @Nullable DATA data) {
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
    public DATA getData() {
        return data;
    }
}
