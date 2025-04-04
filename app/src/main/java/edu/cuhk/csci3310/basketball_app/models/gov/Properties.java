package edu.cuhk.csci3310.basketball_app.models.gov;

import android.content.Intent;

public class Properties {
    public final String ADDRESS_EN, NAME_EN, NSEARCH01_EN, NSEARCH02_EN, NSEARCH03_EN, NSEARCH04_EN, NSEARCH05_EN, NSEARCH06_EN;
    public final double latitude_lands, longitude_lands;

    private Properties(double lat, double lon, String address, String name, String nSearch1, String nSearch2, String nSearch3, String nSearch4, String nSearch5, String nSearch6) {
        this.latitude_lands = lat;
        this.longitude_lands = lon;
        this.ADDRESS_EN = address;
        this.NAME_EN = name;
        this.NSEARCH01_EN = nSearch1;
        this.NSEARCH02_EN = nSearch2;
        this.NSEARCH03_EN = nSearch3;
        this.NSEARCH04_EN = nSearch4;
        this.NSEARCH05_EN = nSearch5;
        this.NSEARCH06_EN = nSearch6;
    }

    public void addToIntent(Intent intent) {
        intent.putExtra("lat", latitude_lands);
        intent.putExtra("lon", longitude_lands);
        intent.putExtra("address", ADDRESS_EN);
        intent.putExtra("name", NAME_EN);
        intent.putExtra("nsearch1", NSEARCH01_EN);
        intent.putExtra("nsearch2", NSEARCH02_EN);
        intent.putExtra("nsearch3", NSEARCH03_EN);
        intent.putExtra("nsearch4", NSEARCH04_EN);
        intent.putExtra("nsearch5", NSEARCH05_EN);
        intent.putExtra("nsearch6", NSEARCH06_EN);
    }

    public static Properties fromIntent(Intent intent) {
        double lat = intent.getDoubleExtra("lat", 0);
        double lon = intent.getDoubleExtra("lon", 0);
        String address = intent.getStringExtra("address");
        String name = intent.getStringExtra("name");
        String[] nSearches = new String[6];
        for (int ii = 0; ii < nSearches.length; ii++) {
            nSearches[ii] = intent.getStringExtra("nsearch"+(ii+1));
        }
        return new Properties(lat, lon, address, name, nSearches[0], nSearches[1], nSearches[2], nSearches[3], nSearches[4], nSearches[5]);
    }
}
