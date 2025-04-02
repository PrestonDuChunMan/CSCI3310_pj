package edu.cuhk.csci3310.basketball_app.api;

import edu.cuhk.csci3310.basketball_app.models.gov.BasketballCourtData;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BasketballCourtApiService {
    // https://api.csdi.gov.hk/apim/dataquery/api/?id=lcsd_rcd_1629267205215_38105&layer=geodatastore&bbox-crs=WGS84&bbox=113.8,22.1,114.7,23.0&limit=10&offset=0
    @GET("apim/dataquery/api/?id=lcsd_rcd_1629267205215_38105&layer=geodatastore&bbox-crs=WGS84")
    Call<BasketballCourtData> getBasketballCourts(@Query("bbox") String boundingBox, @Query("limit") int limit, @Query("offset") int offset);
}
