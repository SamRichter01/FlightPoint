package com.flightpoint.service;

//import org.springframework.web.util.UriUtils;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.util.ArrayList;

import org.springframework.web.client.RestClient;

public class SearchUtils {

    public static SearchOutput search(double hdng, double azmth, double lat, double lng, double alt) {
        // Get bounding box
        BoundingBox searchBox = defineBoundingBox(hdng, azmth, lat, lng);

        // Get flights in bounding box
        ArrayList<State> states = getFlights(searchBox);

         /**
         * SEARCH FOR PLANE THAT BEST FITS DEVICE ORIENTATION
         * 
         * 1. Normalize plane coordinates to center on the device at 0, 0
         * Calculate bearing to each plane from the device
         * Find plane(s) with bearing closest to device bearing
         * 
         * If multiple planes within a margin of error:
         *      Get distances from planes to device in an absolute straight line
         *      Get altitudes of planes
         *      Find the plane whose angle most closely matches the device azimuth
         */
        ArrayList<SearchablePlaneState> searchableList = new ArrayList<SearchablePlaneState>();
        for (State state : states) {
            SearchablePlaneState item = new SearchablePlaneState(state, lat, lng, alt);
            searchableList.add(item);
        }
        
        return new SearchOutput();
    }

    private static BoundingBox defineBoundingBox(double hdng, double azmth, double lat, double lng) {
        /*
         * Create bounding box from device coordinates, azimuth, and heading.
         * For now I'm gonna calculate a default bounding box 2deg (~100mi) square centered on Minneapolis
         * 44.9778°, -93.2650°
         */

        double laMin = lat - 1;
        double loMin = lng - 1;
        double laMax = lat + 1;
        double loMax = lng + 1;

        return new BoundingBox(loMin, laMin, loMax, laMax);
    }

    private static ArrayList<State> getFlights(BoundingBox searchBox) {

        // Build the request uri. Hard coding it like I did below is not a good solution.
        double laMin = searchBox.laMin();
        double loMin = searchBox.loMin();
        double laMax = searchBox.laMax();
        double loMax = searchBox.loMax();

        // Call the opensky api to get flight data for the area
        RestClient client = RestClient.create();
        String response = client.get()
            .uri("https://opensky-network.org/api/states/all?lamin={laMin}&lomin={loMin}&lamax={laMax}&lomax={loMax}", laMin, loMin, laMax, loMax)
            .retrieve()
            .body(String.class);

        return parseResponse(response);
    }

    /*
     * Reference: https://www.baeldung.com/jackson-object-mapper-tutorial
     */
    private static ArrayList<State> parseResponse(String response) {

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("FlightStateDeserializer", new Version(1, 0, 0, null, null, null));
        module.addDeserializer(State.class, new FlightStateDeserializer());
        mapper.registerModule(module);
        //mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
        
        try {
            JsonNode responseNode = mapper.readTree(response).get("states");
            String newResponse = mapper.writeValueAsString(responseNode);
            //ArrayList<ArrayList<Object>> resp = mapper.readValue(response, StateResponse.class).states();
            return mapper.readValue(newResponse, new TypeReference<ArrayList<State>>(){});
        } catch (Exception e) {
            return new ArrayList<State>();
        }
    }

    private static class SearchablePlaneState {
        private State state;
        private double[] planeCoords;
        private double alt;
        private double brng;
        private double azmth;

        public SearchablePlaneState(State state, double lat, double lng, double alt) {
            this.state = state;
            this.planeCoords = new double[2];
            this.planeCoords[0] = state.getLatitude() - lat;
            this.planeCoords[1] = state.getLongitude() - lng;
            this.alt = state.getGeoAltitude();
            this.brng = calculateBearing();
            this.azmth = calculateAzimuth();
        }

        // Based on the algorithms laid out at: https://www.movable-type.co.uk/scripts/latlong.html
        // θ = atan2( sin Δλ ⋅ cos φ2 , cos φ1 ⋅ sin φ2 − sin φ1 ⋅ cos φ2 ⋅ cos Δλ )
        // where:	φ1,λ1 is the start point, φ2,λ2 the end point (Δλ is the difference in longitude)
        private double calculateBearing() {
            double planeLat = planeCoords[0];
            double planeLng = planeCoords[1];
            // Because the plane's coordinates are normalized, both of these values can be zero.
            double deviceLat = 0;
            double deviceLng = 0;
            double y = Math.sin(planeLng - deviceLng) * Math.cos(deviceLat);
            double x = Math.cos(deviceLat) * Math.sin(planeLat) - 
                Math.sin(deviceLat) * Math.cos(planeLat) * Math.cos(planeLng - deviceLng);
            double theta = Math.atan2(y, x);
            double brng = (theta * 180/Math.PI + 360) % 360; //In degrees.
            return brng;
        }

        // Calculate the azimuth to the plane from the device.
        private double calculateAzimuth() {
            return 0;
        }

        public double getBearing() {
            return this.brng;
        }

        public double getAzimuth() {
            return this.azmth;
        }
    }
}