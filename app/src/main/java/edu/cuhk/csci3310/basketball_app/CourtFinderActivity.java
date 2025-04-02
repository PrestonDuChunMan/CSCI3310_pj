package edu.cuhk.csci3310.basketball_app;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import edu.cuhk.csci3310.basketball_app.adapters.MapSearchAdapter;
import edu.cuhk.csci3310.basketball_app.api.ApiHandler;
import edu.cuhk.csci3310.basketball_app.court_finder.MapViewConfig;
import edu.cuhk.csci3310.basketball_app.gps.GpsHandler;
import edu.cuhk.csci3310.basketball_app.models.gov.BasketballCourtData;
import edu.cuhk.csci3310.basketball_app.models.gov.Feature;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourtFinderActivity extends AppCompatActivity {
    private static final String TAG = "court_finder";

    private MapView mapView;
    private FloatingActionButton gpsButton;
    private SearchView searchView;
    private RecyclerView searchResultView;
    private MapSearchAdapter mapSearchAdapter;

    private MyLocationNewOverlay locationOverlay;
    private GpsMyLocationProvider locationProvider;
    private ApiHandler apiHandler;

    private boolean followGps = true, fetching = false;
    private MapViewConfig mapViewConfig = null;
    private Timer timer;
    private List<Feature> courts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "created map activity");

        Context ctx = this.getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        askForPermissions(List.of(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE));

        setContentView(R.layout.activity_court_finder);

        // setup mapview
        this.mapView = findViewById(R.id.map);
        this.mapView.setTileSource(TileSourceFactory.MAPNIK);
        // setup mapview properties
        this.locationProvider = new GpsMyLocationProvider(ctx);
        this.locationOverlay = new MyLocationNewOverlay(this.locationProvider, this.mapView);
        this.locationOverlay.enableMyLocation();
        this.mapView.getOverlays().add(this.locationOverlay);
        this.mapView.setZoomLevel(18);
        this.mapView.setOnTouchListener((view, motionEvent) -> {
            this.stopFollowingGps();
            return false;
        });
        // setup gps button and properties
        this.gpsButton = findViewById(R.id.button_gps);
        this.gpsButton.setOnClickListener(this::handleGpsButtonClick);
        // setup search
        this.searchView = findViewById(R.id.location_search);
        this.searchResultView = findViewById(R.id.list_search);
        this.searchResultView.setVisibility(View.INVISIBLE);
        this.mapSearchAdapter = new MapSearchAdapter(this.mapView, this.searchView, this.searchResultView);
        this.searchResultView.setLayoutManager(new LinearLayoutManager(this));
        this.searchResultView.setAdapter(mapSearchAdapter);
        this.searchView.setOnClickListener(view -> searchResultView.setVisibility(View.VISIBLE));
        this.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d(TAG, "Query submitted: " + s);
                searchView.setEnabled(false);
                mapSearchAdapter.changeQuery(s);
                stopFollowingGps();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.isEmpty()) mapSearchAdapter.clear();
                return false;
            }
        });

        this.apiHandler = ApiHandler.getInstance();

        Handler mainHandler = new Handler(this.getMainLooper());
        this.timer = new Timer();
        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                refreshNearbyCourts();
            }
        }, 1000, 1000);
        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                refreshLocation(mainHandler);
            }
        }, 0, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.mapView.onResume();
        this.locationOverlay.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.mapView.onPause();
        this.locationOverlay.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.timer.cancel();
    }

    private void stopFollowingGps() {
        this.followGps = false;
        this.gpsButton.setImageDrawable(AppCompatResources.getDrawable(this.getApplicationContext(), R.drawable.baseline_gps_not_fixed_24));
    }

    private void handleGpsButtonClick(View view) {
        if (this.followGps) return;
        this.gpsButton.setImageDrawable(AppCompatResources.getDrawable(this.getApplicationContext(), R.drawable.baseline_gps_fixed_24));
        this.followGps = true;
        Location location = this.locationProvider.getLastKnownLocation();
        if (location != null) this.mapView.setExpectedCenter(new GeoPoint(location.getLatitude(), location.getLongitude()));
    }

    private void refreshNearbyCourts() {
        if (this.fetching) return;
        IGeoPoint point = this.mapView.getMapCenter();
        double lat = point.getLatitude();
        double lon = point.getLongitude();
        if (this.mapViewConfig != null) {
            if (this.mapViewConfig.compare(this.mapView)) return;
            this.mapViewConfig.update(this.mapView);
        } else this.mapViewConfig = new MapViewConfig(this.mapView);
        this.fetching = true;
        ApiHandler.BoundingBox box = new ApiHandler.BoundingBox(lat - 0.01, lat + 0.01, lon - 0.01, lon + 0.01);
        Call<BasketballCourtData> call = this.apiHandler.getBasketballCourts(box, 100, 0);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<BasketballCourtData> call, Response<BasketballCourtData> response) {
                fetching = false;
                if (!response.isSuccessful() || response.body() == null) return;
                List<Overlay> overlays = mapView.getOverlays();
                // remove old markers
                overlays.clear();
                // add region for debug
                /*List<GeoPoint> points = new ArrayList<>();
                Polygon polygon = new Polygon();
                points.add(new GeoPoint(box.latStart, box.lonStart));
                points.add(new GeoPoint(box.latStart, box.lonEnd));
                points.add(new GeoPoint(box.latEnd, box.lonEnd));
                points.add(new GeoPoint(box.latEnd, box.lonStart));
                points.add(points.get(0));
                polygon.getFillPaint().setColor(Color.parseColor("#1EFFE70E"));
                polygon.setPoints(points);
                polygon.setTitle("Debug region");
                overlays.add(polygon);*/
                // add back self position
                overlays.add(locationOverlay);
                // add all court markers
                courts = response.body().getFeatures();
                for (int ii = 0; ii < courts.size(); ii++) {
                    Feature feature = courts.get(ii);
                    Marker marker = new Marker(mapView);
                    double[] pos = feature.getGeometry().getCoordinates();
                    marker.setPosition(new GeoPoint(pos[1], pos[0]));
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                    marker.setIcon(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.basketball));
                    int ii1 = ii;
                    marker.setOnMarkerClickListener((mark, view) -> onMarkerClick(ii1));
                    overlays.add(marker);
                }
            }

            @Override
            public void onFailure(Call<BasketballCourtData> call, Throwable t) {
                Log.e(TAG, "Failed to get nearby courts", t);
                fetching = false;
            }
        });
    }

    private boolean onMarkerClick(int index) {
        Intent intent = new Intent(this.getApplicationContext(), CourtDetailActivity.class);
        this.courts.get(index).getProperties().addToIntent(intent);
        startActivity(intent);
        return false;
    }

    private void refreshLocation(Handler mainHandler) {
        Location loc = this.locationProvider.getLastKnownLocation();
        if (loc != null && this.followGps)
            mainHandler.post(() -> this.mapView.setExpectedCenter(new GeoPoint(loc.getLatitude(), loc.getLongitude())));
    }

    private void askForPermissions(List<String> permissions) {
        List<String> needGrant = new ArrayList<>();
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this.getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED)
                needGrant.add(permission);
        }
        ActivityCompat.requestPermissions(this, needGrant.toArray(new String[0]), 0);
    }
}