package edu.cuhk.csci3310.basketball_app.models.gov;

import java.util.List;

public class BasketballCourtData {
    private final String timeStamp;
    private final List<Feature> features;

    public BasketballCourtData(String timeStamp, List<Feature> features) {
        this.timeStamp = timeStamp;
        this.features = features;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public List<Feature> getFeatures() {
        return features;
    }
}
