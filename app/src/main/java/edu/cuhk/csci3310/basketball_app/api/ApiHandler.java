package edu.cuhk.csci3310.basketball_app.api;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Locale;

import edu.cuhk.csci3310.basketball_app.models.gov.BasketballCourtData;
import edu.cuhk.csci3310.basketball_app.models.osm.Place;
import edu.cuhk.csci3310.basketball_app.models.server.CourtEventListResponse;
import edu.cuhk.csci3310.basketball_app.models.server.CourtEventResponse;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiHandler {
    private static ApiHandler INSTANCE;

    private final BasketballCourtApiService basketballCourt;
    private final CourtEventApiService courtEvent;
    private final OsmNominatimApiService osmNominatim;

    private ApiHandler() {
        Retrofit.Builder builder = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create());
        this.basketballCourt = builder
                .baseUrl("https://api.csdi.gov.hk/")
                .build()
                .create(BasketballCourtApiService.class);
        this.courtEvent = builder
                .baseUrl("http://localhost:3000/") // change later
                .build().create(CourtEventApiService.class);
        this.osmNominatim = builder
                .baseUrl("https://nominatim.openstreetmap.org/")
                .build()
                .create(OsmNominatimApiService.class);
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

    public Call<List<Place>> searchPlaces(String query) {
        return this.osmNominatim.searchPlaces(query);
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
