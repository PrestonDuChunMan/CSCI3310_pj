package edu.cuhk.csci3310.basketball_app.api;

import androidx.annotation.NonNull;

import java.util.Locale;

import edu.cuhk.csci3310.basketball_app.models.gov.BasketballCourtData;
import edu.cuhk.csci3310.basketball_app.models.server.CourtEventListResponse;
import edu.cuhk.csci3310.basketball_app.models.server.CourtEventResponse;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiHandler {
    private static ApiHandler INSTANCE;

    private final BasketballCourtApiService basketballCourt;
    private final CourtEventApiService courtEvent;

    private ApiHandler() {
        Retrofit gov = new Retrofit.Builder()
                .baseUrl("https://api.csdi.gov.hk/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.basketballCourt = gov.create(BasketballCourtApiService.class);
        Retrofit server = new Retrofit.Builder()
                .baseUrl("http://localhost:3000/") // change later
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.courtEvent = server.create(CourtEventApiService.class);
    }

    public static ApiHandler getInstance() {
        if (INSTANCE == null) INSTANCE = new ApiHandler();
        return INSTANCE;
    }

    public Call<BasketballCourtData> getBasketballCourts(BoundingBox boundingBox, int limit, int offset) {
        return this.basketballCourt.getBasketballCourts(boundingBox.toString(), limit, offset);
    }

    public Call<CourtEventListResponse> getCourtEvents(int courtId) {
        return this.courtEvent.getCourtEvents(courtId);
    }

    public Call<CourtEventResponse> getCourtEvent(int courtId, int eventId) {
        return this.courtEvent.getCourtEvent(courtId, eventId);
    }

    public static class BoundingBox {
        public final double latStart, lonStart, latEnd, lonEnd;

        public BoundingBox(double latStart, double latEnd, double lonStart, double lonEnd) {
            this.latStart = Math.min(latStart, latEnd);
            this.latEnd = Math.max(latStart, latEnd);
            this.lonStart = Math.min(lonStart, lonEnd);
            this.lonEnd = Math.max(lonStart, lonEnd);
        }

        @NonNull
        @Override
        public String toString() {
            return String.format(Locale.ENGLISH, "%.4f,%.4f,%.4f,%.4f", this.lonStart, this.latStart, this.lonEnd, this.latEnd);
        }
    }
}
