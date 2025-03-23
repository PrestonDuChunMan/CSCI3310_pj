package edu.cuhk.csci3310.basketball_app;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import edu.cuhk.csci3310.basketball_app.api.ApiHandler;
import edu.cuhk.csci3310.basketball_app.court_finder.MapViewConfig;
import edu.cuhk.csci3310.basketball_app.gps.GpsHandler;
import edu.cuhk.csci3310.basketball_app.models.BasketballCourtData;
import edu.cuhk.csci3310.basketball_app.models.Feature;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourtFinderActivity extends AppCompatActivity {
    private MapView mapView;
    private MyLocationNewOverlay locationOverlay;
    private ApiHandler apiHandler;
    private GpsHandler gpsHandler;

    private boolean followGps = true, fetching = false;
    private MapViewConfig mapViewConfig = null;
    private Timer courtUpdateTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("map", "created map activity");

        Context ctx = this.getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_court_finder);
        // Add your code for court finder here (e.g., maps or location services)
        this.mapView = findViewById(R.id.map);
        this.mapView.setTileSource(TileSourceFactory.MAPNIK);

        this.locationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(ctx), this.mapView);
        this.locationOverlay.enableMyLocation();
        this.mapView.getOverlays().add(this.locationOverlay);
        this.mapView.setZoomLevel(18);

        this.mapView.setLongClickable(true);
        this.mapView.setOnLongClickListener(this::handleLongClick);
        this.mapView.setOnTouchListener((view, motionEvent) -> followGps = false);

        this.apiHandler = new ApiHandler();
        this.gpsHandler = new GpsHandler(this);

        this.gpsHandler.addLocationChangeListener(this::handleLocationChange);

        this.courtUpdateTimer = new Timer();
        this.courtUpdateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                refreshNearbyCourts();
            }
        }, 1000, 5000);
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
        this.courtUpdateTimer.cancel();
    }

    public boolean handleLongClick(View view) {
        Marker marker = new Marker(this.mapView);
        marker.setPosition(new GeoPoint(this.mapView.getMapCenter()));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        marker.setIcon(AppCompatResources.getDrawable(this.getApplicationContext(), R.drawable.basketball));
        this.mapView.getOverlays().add(marker);
        return true;
    }

    private void handleLocationChange(Location loc) {
        if (this.followGps)
            this.mapView.setExpectedCenter(new GeoPoint(loc.getLatitude(), loc.getLongitude()));
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
                for (Feature feature : response.body().getFeatures()) {
                    Marker marker = new Marker(mapView);
                    double[] pos = feature.getGeometry().getCoordinates();
                    marker.setPosition(new GeoPoint(pos[1], pos[0]));
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                    marker.setIcon(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.basketball));
                    overlays.add(marker);
                }
            }

            @Override
            public void onFailure(Call<BasketballCourtData> call, Throwable t) {
                Log.e("map", "Failed to get nearby courts", t);
                fetching = false;
            }
        });
    }
}