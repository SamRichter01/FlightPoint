package com.flightpoint.service;

import org.springframework.web.util.UriUtils;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
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
         * Normalize state vectors to the bounding box coordinates
         *      //Find conversion from lat and long to euclidean coordinates
         *      Convert longitude and latitude to meters
         *      Use WGS-84 altitude of device and geometric altitude of plane
         * Create rotation vector that describes device orientation
         *      Begin with unit vector
         *      Rotate in x axis according to Azimuth     
         *      Rotate in y axis according to heading
         * Create rotation vectors that describe direction from device to each plane
         * Identify vector(s) closest to device orientation vector
         * Return info
         */

        return new SearchOutput();
    }

    private static BoundingBox defineBoundingBox(double hdng, double azmth, double lat, double lng) {
        /*
         * Create bounding box from device coordinates, azimuth, and heading.
         * For now I'm gonna calculate a default bounding box 2deg (~100mi) square centered on Minneapolis
         * 44.9778° N, 93.2650° W
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
}