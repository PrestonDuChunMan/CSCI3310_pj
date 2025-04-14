package edu.cuhk.csci3310.basketball_app.gps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;

import java.util.ArrayList;
import java.util.List;

// Referred to https://www.geeksforgeeks.org/how-to-get-current-location-in-android/
public class GpsHandler implements IMyLocationProvider, LocationListener {
    private Activity activity;
    private Location currentLocation;
    private final LocationManager locationManager;
    private final boolean hasGps;
    private final boolean hasNetwork;
    private final List<LocationListener> listeners;

    public GpsHandler(Activity activity) {
        this.activity = activity;
        this.locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        this.hasGps = this.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        this.hasNetwork = this.locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        this.listeners = new ArrayList<>();
    }

    private boolean askForPermissions(Activity activity, List<String> permissions) {
        List<String> needGrant = new ArrayList<>();
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED)
                needGrant.add(permission);
        }
        if (needGrant.isEmpty()) return false;
        ActivityCompat.requestPermissions(activity, needGrant.toArray(new String[0]), 0);
        return true;
    }

    public void addLocationListener(LocationListener listener) {
        this.listeners.add(listener);
    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean startLocationProvider(IMyLocationConsumer myLocationConsumer) {
        boolean granted = askForPermissions(this.activity, List.of(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE));
        if (!granted) return false;

        if (this.hasGps)
            this.locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000,
                    0,
                    this
            );
        if (this.hasNetwork)
            this.locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    1000,
                    0,
                    this
            );
        return false;
    }

    @Override
    public void stopLocationProvider() {
        this.locationManager.removeUpdates(this);
    }

    @Override
    public Location getLastKnownLocation() {
        return this.currentLocation;
    }

    @Override
    public void destroy() {

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        currentLocation = location;
        listeners.forEach(li -> li.onLocationChanged(location));
    }
}
