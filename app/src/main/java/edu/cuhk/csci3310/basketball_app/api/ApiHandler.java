package edu.cuhk.csci3310.basketball_app.api;

import java.util.List;

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

    public Call<BasketballCourtData> getBasketballCourts(List<Double> boundingBox, int limit, int offset) {
        return this.basketballCourt.getBasketballCourts(boundingBox, limit, offset);
    }
}
