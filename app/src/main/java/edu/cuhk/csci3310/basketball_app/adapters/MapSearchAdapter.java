package edu.cuhk.csci3310.basketball_app.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.List;

import edu.cuhk.csci3310.basketball_app.R;
import edu.cuhk.csci3310.basketball_app.api.ApiHandler;
import edu.cuhk.csci3310.basketball_app.models.osm.Place;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapSearchAdapter extends RecyclerView.Adapter<MapSearchAdapter.MapSearchViewHolder> {
    private static final String TAG = "map_search_adapter";

    private final MapView mapView;
    private final SearchView searchView;
    private final RecyclerView recyclerView;
    private final ApiHandler apiHandler;
    private List<Place> data = new ArrayList<>();

    public MapSearchAdapter(MapView mapView, SearchView searchView, RecyclerView recyclerView) {
        this.mapView = mapView;
        this.searchView = searchView;
        this.recyclerView = recyclerView;
        this.apiHandler = ApiHandler.getInstance();
    }

    @NonNull
    @Override
    public MapSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_court_search, parent, false);
        return new MapSearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MapSearchViewHolder holder, int position) {
        Log.d(TAG, "Binding " + position);
        Place place = this.data.get(position);
        holder.activate(place.getDisplayName(), view -> {
            mapView.setExpectedCenter(new GeoPoint(place.latitude(), place.longitude()));
            recyclerView.setVisibility(View.INVISIBLE);
        });
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public void changeQuery(String query) {
        this.changeQuery(query, 0);
    }

    public void changeQuery(String query, long retry) {
        try {
            Thread.sleep(retry);
            Call<List<Place>> call = this.apiHandler.searchPlaces(query);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<List<Place>> call, Response<List<Place>> response) {
                    if (response.isSuccessful()) {
                        MapSearchAdapter.this.data = response.body();
                        Log.d(TAG, "Received response with size " + MapSearchAdapter.this.data.size());
                        MapSearchAdapter.this.notifyDataSetChanged();
                        recyclerView.setVisibility(View.VISIBLE);
                        searchView.setEnabled(true);
                    } else changeQuery(query, retry + 1000);
                }

                @Override
                public void onFailure(Call<List<Place>> call, Throwable t) {
                    Log.e(TAG, "Failed to search for places", t);
                    changeQuery(query, retry + 1000);
                }
            });
        } catch (InterruptedException e) { }
    }

    public void clear() {
        this.data = new ArrayList<>();
        notifyDataSetChanged();
    }

    public static class MapSearchViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public MapSearchViewHolder(@NonNull View view) {
            super(view);
            this.textView = view.findViewById(R.id.value_result);
        }

        public void activate(String text, View.OnClickListener listener) {
            this.textView.setText(text);
            this.textView.setOnClickListener(listener);
        }
    }
}
