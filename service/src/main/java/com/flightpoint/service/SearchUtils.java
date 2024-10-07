package com.flightpoint.service;

public class SearchUtils {

    public SearchUtils() {}

    public SearchOutput search() {
        BoundingBox searchBox = defineBoundingBox();

        return new SearchOutput();
    }

    private static BoundingBox defineBoundingBox() {
        /*
         * Create bounding box from device coordinates and heading
         */

        return null;
    }

    private String searchBoundingBox(BoundingBox searchBox, double deviceHeading, double deviceAzimuth) {

        // Call the opensky api to get flight data for the area
        String openSkyResponse = "";

        // Parse the opensky response to get flight data

        // Search for best candidate plane based on inputs

        // Translate data from best match plane into json and return
        
        return null;
    }
}
