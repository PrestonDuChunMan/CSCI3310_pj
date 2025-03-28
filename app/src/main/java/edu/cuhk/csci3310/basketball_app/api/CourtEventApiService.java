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
    @GET("api/court/{court_id}/events")
    Call<CourtEventListResponse> getCourtEvents(@Path(value = "court_id", encoded = true) int courtId);

    @GET("api/court/{court_id}/event/{event_id}")
    Call<CourtEventResponse> getCourtEvent(@Path(value = "court_id", encoded = true) int courtId, @Path(value = "event_id", encoded = true) int eventId);

    @POST("api/court/{court_id}/event")
    Call<ServerResponse<Void>> addCourtEvent(@Path(value = "court_id", encoded = true) int courtId, @Body NewCourtEvent event);
}
