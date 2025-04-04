package edu.cuhk.csci3310.basketball_app.models.gov;

public class Geometry {
    private final double[] coordinates;
    private final String type;

    public Geometry(double[] coordinates, String type) {
        this.coordinates = coordinates;
        this.type = type;
    }

    // Note: These coordinates are in [lon, lat]
    public double[] getCoordinates() {
        return coordinates;
    }

    public String getType() {
        return type;
    }
}
