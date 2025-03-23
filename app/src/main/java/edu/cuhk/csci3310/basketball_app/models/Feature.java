package edu.cuhk.csci3310.basketball_app.models;

public class Feature {
    private final Geometry geometry;
    private final String type;

    public Feature(Geometry geometry, String type) {
        this.geometry = geometry;
        this.type = type;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public String getType() {
        return type;
    }
}
