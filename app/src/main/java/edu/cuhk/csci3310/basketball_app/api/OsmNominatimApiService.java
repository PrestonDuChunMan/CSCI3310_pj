package edu.cuhk.csci3310.basketball_app.api;

import java.util.List;

import edu.cuhk.csci3310.basketball_app.models.osm.Place;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OsmNominatimApiService {
    // https://nominatim.openstreetmap.org/search.php?q=tung+cheong&accept-language=en-US%2Cen&format=jsonv2
    @GET("search.php?accept-language=en-US%2Cen&format=jsonv2")
    Call<List<Place>> searchPlaces(@Query("q") String query);

}
