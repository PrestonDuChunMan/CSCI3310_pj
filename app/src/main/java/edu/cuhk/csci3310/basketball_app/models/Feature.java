package edu.cuhk.csci3310.basketball_app.models;

public class Feature {
    private final Geometry geometry;
    private final String type;
    private final Properties properties;

    public Feature(Geometry geometry, String type, Properties properties) {
        this.geometry = geometry;
        this.type = type;
        this.properties = properties;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public String getType() {
        return type;
    }

    public Properties getProperties() {
        return properties;
    }
}
