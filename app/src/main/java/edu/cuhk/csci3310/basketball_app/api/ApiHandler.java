package edu.cuhk.csci3310.basketball_app.api;

import androidx.annotation.NonNull;

import org.osmdroid.util.BoundingBox;

import java.util.List;
import java.util.Locale;

import edu.cuhk.csci3310.basketball_app.models.BasketballCourtData;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiHandler {
    private final Retrofit retrofit;
    private final BasketballCourtApiService basketballCourt;

    public ApiHandler() {
        this.retrofit = new Retrofit.Builder()
                .baseUrl("https://api.csdi.gov.hk/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.basketballCourt = this.retrofit.create(BasketballCourtApiService.class);
    }

    public Call<BasketballCourtData> getBasketballCourts(BoundingBox boundingBox, int limit, int offset) {
        return this.basketballCourt.getBasketballCourts(boundingBox.toString(), limit, offset);
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
