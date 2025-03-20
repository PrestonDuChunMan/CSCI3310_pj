package edu.cuhk.csci3310.basketball_app;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

import edu.cuhk.csci3310.basketball_app.api.ApiHandler;
import edu.cuhk.csci3310.basketball_app.gps.GpsHandler;

public class CourtFinderActivity extends AppCompatActivity {
    private MapView mapView;
    private MyLocationNewOverlay locationOverlay;
    private ApiHandler apiHandler;
    private GpsHandler gpsHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = this.getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_court_finder);
        // Add your code for court finder here (e.g., maps or location services)
        this.mapView = findViewById(R.id.map);
        this.mapView.setTileSource(TileSourceFactory.MAPNIK);

        this.locationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(ctx), this.mapView);
        this.locationOverlay.enableMyLocation();
        this.mapView.getOverlays().add(this.locationOverlay);
        this.mapView.setZoomLevel(12);

        this.mapView.setLongClickable(true);
        this.mapView.setOnLongClickListener(this::handleLongClick);

        this.apiHandler = new ApiHandler();
        this.gpsHandler = new GpsHandler(this);

        Location location = this.gpsHandler.getImmediateLocation();
        this.mapView.setExpectedCenter(new GeoPoint(location.getLatitude(), location.getLongitude()));
        this.gpsHandler.addLocationChangeListener(loc -> this.mapView.setExpectedCenter(new GeoPoint(loc.getLatitude(), loc.getLongitude())));
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

    public boolean handleLongClick(View view) {
        Log.d("map", "long clicked");
        Marker marker = new Marker(this.mapView);
        marker.setPosition(new GeoPoint(this.mapView.getMapCenter()));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        marker.setIcon(AppCompatResources.getDrawable(this.getApplicationContext(), R.drawable.basketball));
        this.mapView.getOverlays().add(marker);
        return true;
    }
}