package edu.cuhk.csci3310.basketball_app.models.osm;

public class Place {
    private long place_id, osm_id, place_rank;
    private double importance;
    private String licence, osm_type, category, type, addresstype;
    private String[] boundingbox;
    // need to convert these
    private String lat, lon;
    // IMPORTANT!
    private String name, display_name;

    public double latitude() {
        return Double.parseDouble(lat);
    }

    public double longitude() {
        return Double.parseDouble(lon);
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return display_name;
    }
}
