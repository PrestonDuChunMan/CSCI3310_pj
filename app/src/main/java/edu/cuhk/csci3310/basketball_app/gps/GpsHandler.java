package edu.cuhk.csci3310.basketball_app.gps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

// Referred to https://www.geeksforgeeks.org/how-to-get-current-location-in-android/
public class GpsHandler {
    private static GpsHandler INSTANCE;

    private Location currentLocation;
    private final LocationManager locationManager;
    private final boolean hasGps;
    private final boolean hasNetwork;
    private final List<OnLocationChange> listeners;

    @SuppressLint("MissingPermission")
    private GpsHandler(Activity activity) {
        askForPermissions(activity, List.of(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE));

        this.locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        this.hasGps = this.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        this.hasNetwork = this.locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        this.listeners = new ArrayList<>();

        LocationListener listener = location -> {
            currentLocation = location;
            listeners.forEach(li -> li.onCurrentLocationChange(location));
        };

        if (this.hasGps)
            this.locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000,
                    0,
                    listener
            );
        if (this.hasNetwork)
            this.locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    5000,
                    0,
                    listener
            );
    }

    public static GpsHandler getInstance(Activity activity) {
        if (INSTANCE == null) INSTANCE = new GpsHandler(activity);
        return INSTANCE;
    }

    private void askForPermissions(Activity activity, List<String> permissions) {
        List<String> needGrant = new ArrayList<>();
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED)
                needGrant.add(permission);
        }
        ActivityCompat.requestPermissions(activity, needGrant.toArray(new String[0]), 0);
    }

    public void addLocationChangeListener(OnLocationChange listener) {
        this.listeners.add(listener);
    }

    public Location getCurrentLocation() {
        return this.currentLocation;
    }

    @SuppressLint("MissingPermission")
    public Location getImmediateLocation() {
        Location gps = null, network = null;
        if (this.hasGps) gps = this.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (this.hasNetwork) network = this.locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (gps != null && network != null) {
            if (gps.getAccuracy() > network.getAccuracy()) return gps;
            else return network;
        } else if (gps != null) return gps;
        else if (network != null) return network;
        else return this.currentLocation;
    }

    public interface OnLocationChange {
        void onCurrentLocationChange(Location location);
    }
}
