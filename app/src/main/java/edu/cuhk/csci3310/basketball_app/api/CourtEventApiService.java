package edu.cuhk.csci3310.basketball_app.api;

import edu.cuhk.csci3310.basketball_app.models.server.CourtEventListResponse;
import edu.cuhk.csci3310.basketball_app.models.server.CourtEventResponse;
import edu.cuhk.csci3310.basketball_app.models.server.NewCourtEvent;
import edu.cuhk.csci3310.basketball_app.models.server.ServerResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

// This uses a homemade server
public interface CourtEventApiService {
    @GET("api/court/{lat}/{lon}/events")
    Call<CourtEventListResponse> getCourtEvents(@Path(value = "lat", encoded = true) String lat, @Path(value = "lon", encoded = true) String lon);

    @GET("api/court/{lat}/{lon}/event/{event_id}")
    Call<CourtEventResponse> getCourtEvent(@Path(value = "lat", encoded = true) String lat, @Path(value = "lon", encoded = true) String lon, @Path(value = "event_id", encoded = true) int eventId);

    @POST("api/court/{lat}/{lon}/event")
    Call<ServerResponse<Void>> addCourtEvent(@Path(value = "lat", encoded = true) String lat, @Path(value = "lon", encoded = true) String lon, @Body NewCourtEvent event);
}
