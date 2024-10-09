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

    public static SearchOutput search(double hdng, double azmth, double lat, double lng) {
        BoundingBox searchBox = defineBoundingBox(hdng, azmth, lat, lng);

        State[] states = getFlights(searchBox);
        // get flights in bounding box

        // search returned flights
        
        // build search output

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

    private static State[] getFlights(BoundingBox searchBox) {

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
    private static State[] parseResponse(String response) {

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("FlightStateDeserializer", new Version(1, 0, 0, null, null, null));
        module.addDeserializer(State.class, new FlightStateDeserializer());
        mapper.registerModule(module);
        //mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
        
        try {
            JsonNode responseNode = mapper.readTree(response).get("states");
            String newResponse = mapper.writeValueAsString(responseNode);
            //ArrayList<ArrayList<Object>> resp = mapper.readValue(response, StateResponse.class).states();
            ArrayList<State> states = mapper.readValue(newResponse, new TypeReference<ArrayList<State>>(){});
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}