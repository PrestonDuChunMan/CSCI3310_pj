package edu.cuhk.csci3310.basketball_app.court_finder;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

public class MapViewConfig {
    private IGeoPoint point;
    private double zoom;

    public MapViewConfig(MapView mapView) {
        this(mapView.getMapCenter(), mapView.getZoomLevelDouble());
    }

    private MapViewConfig(IGeoPoint point, double zoom) {
        this.point = new GeoPoint(point);
        this.zoom = zoom;
    }

    public void setPoint(IGeoPoint point) {
        this.point = new GeoPoint(point);
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
    }

    public boolean compare(MapView mapView) {
        IGeoPoint center = mapView.getMapCenter();
        return center.getLatitude() == this.point.getLatitude() &&
                center.getLongitude() == this.point.getLongitude() &&
                mapView.getZoomLevelDouble() == this.zoom;
    }

    public void update(MapView mapView) {
        this.point = new GeoPoint(mapView.getMapCenter());
        this.zoom = mapView.getZoomLevelDouble();
    }
}
