package com.flightpoint.service;

//import org.springframework.web.util.UriUtils;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.springframework.util.comparator.Comparators;
import org.springframework.web.client.RestClient;

public class SearchUtils {

    public static SearchOutput search(double brng, double azmth, double lat, double lng, double alt) {
        // Get bounding box
        BoundingBox searchBox = defineBoundingBox(brng, azmth, lat, lng);

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
            SearchablePlaneState item = new SearchablePlaneState(state, lat, lng, alt, brng);
            searchableList.add(item);
        }

        Collections.sort(searchableList);
        
        return new SearchOutput();
    }

    private static BoundingBox defineBoundingBox(double brng, double azmth, double lat, double lng) {
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

    private static class SearchablePlaneState implements Comparable<SearchablePlaneState> {
        private State state;
        private double[] normalPlaneCoords = new double[2];
        private double[] planeCoords = new double[2];
        private double[] deviceCoords = new double[2];
        private double deviceBrng;
        private double deviceAlt;
        private double planeAlt;
        private double planeBrng;
        private double brngDiff;
        private double azmth;

        public SearchablePlaneState(State state, double lat, double lng, double alt, double deviceBrng) {
            if(state!=null) {
                this.state = state;
                this.normalPlaneCoords[0] = state.getLatitude() - lat;
                this.normalPlaneCoords[1] = state.getLongitude() - lng;
                this.planeCoords[0] = state.getLatitude();
                this.planeCoords[1] = state.getLongitude();
                this.deviceCoords[0] = lat;
                this.deviceCoords[1] = lng;
                this.planeAlt = state.getGeoAltitude();
                this.deviceAlt = alt;
                this.deviceBrng = deviceBrng;
                this.planeBrng = calculateBearing();
                this.brngDiff = calculateBearingDiff();
                this.azmth = calculateAzimuth();
            } 
        }

        @Override
        public int compareTo(SearchablePlaneState o) {
            if (this.brngDiff > o.getBearingDiff()) {
                return 1;
            } else if (this.brngDiff < o.getBearingDiff()) {
                return -1;
            } else {
                return 0;
            }
        }

        // Based on the algorithms laid out at: https://www.movable-type.co.uk/scripts/latlong.html
        // θ = atan2( sin Δλ ⋅ cos φ2 , cos φ1 ⋅ sin φ2 − sin φ1 ⋅ cos φ2 ⋅ cos Δλ )
        // where:	φ1,λ1 is the start point, φ2,λ2 the end point (Δλ is the difference in longitude)
        private double calculateBearing() {
            double planeLat = Math.toRadians(normalPlaneCoords[0]);
            double planeLng = Math.toRadians(normalPlaneCoords[1]);
            // Because the plane's coordinates are normalized, both of these values can be zero.
            double deviceLat = 0;
            double deviceLng = 0;
            double y = Math.sin(planeLng - deviceLng) * Math.cos(deviceLat);
            double x = Math.cos(deviceLat) * Math.sin(planeLat) - 
                Math.sin(deviceLat) * Math.cos(planeLat) * Math.cos(planeLng - deviceLng);
            double theta = Math.atan2(y, x);
            double brng = (theta * Math.toRadians(180)/Math.PI + Math.toRadians(360)) % Math.toRadians(360); //In degrees.
            return Math.toDegrees(brng);
        }

        // Calculate difference between the device and plane bearings so it's easier to sort the planes
        private double calculateBearingDiff() {
            return Math.min((Math.abs(planeBrng - deviceBrng)), 360 - Math.abs(planeBrng - deviceBrng));
        }

        // Calculate the azimuth to the plane from the device.
        // This one's gonna be nasty. I even wrote my own vector library.
        // https://space.stackexchange.com/questions/52123/how-to-calculate-degrees-above-below-horizon-for-kinet-x-rocket-getting-to-an
        // https://medium.com/@manishsingh7163/converting-from-latitude-longitude-to-cartesian-coordinates-9ddf30841c45
        private double calculateAzimuth() {

            // Define the WGS-84 Earth Ellipsoid
            // Semi-major axes in meters
            double a = 6378137.0;
            // Semi-minor axis in meters
            double b = 6356752.314245;
            
            // Coordinates of the plane
            double pLat = Math.toRadians(planeCoords[0]);
            double pLng = Math.toRadians((planeCoords[1] + 360) % 360);

            // Scalar for the plane's cartesian vector
            double pR = computeScalar(a, b, pLat, pLng, planeAlt);

            // The plane's cartesian vector
            Vector pCart = computeCartesian(pR, pLat, pLng);


            // Coordinates of the device
            double dLat = Math.toRadians(deviceCoords[0]);
            double dLng = Math.toRadians((deviceCoords[1] + 360) % 360);

            // Scalar for the device's cartesian vector
            double dR = computeScalar(a, b, dLat, dLng, deviceAlt);

            // The device's cartesian vector
            Vector dCart = computeCartesian(dR, dLat, dLng);

            Vector vOne = Vector.product(new Vector(new double[]{
                dCart.get(0) / Math.pow(a, 2),
                dCart.get(1) / Math.pow(a, 2),
                dCart.get(2) / Math.pow(b, 2)
            }), 2 );

            Vector vTwo = new Vector(new double[]{
                pCart.get(0) - dCart.get(0),
                pCart.get(1) - dCart.get(1),
                pCart.get(2) - dCart.get(2)
            });

            return 90 - Math.toDegrees(Math.acos(Vector.dotProduct(vOne, vTwo) / (vOne.magnitude() * vTwo.magnitude())));
        }

        private double computeScalar(double a, double b, double lat, double lng, double alt) {
            // Compute R
            return ((a * a * b) 
                / Math.sqrt(
                    Math.pow(b, 2)
                    * (
                        Math.pow(a, 2)
                        * Math.pow(Math.cos(lng), 2)
                        + Math.pow(a, 2)
                        * Math.pow(Math.sin(lng), 2)
                    ) 
                    * Math.pow(Math.cos(lat), 2) 
                    + Math.pow(a, 2) 
                    * Math.pow(a, 2) 
                    * Math.pow(Math.sin(lat), 2)
                )) + alt;
        }

        private Vector computeCartesian(double scalar, double lat, double lng) {
            Vector u = new Vector(new double[]{
                Math.cos(lat) * Math.cos(lng),
                Math.cos(lat) * Math.sin(lng),
                Math.sin(lat)
            });
            return Vector.product(u, scalar);
        }

        public State getState() {
            return this.state;
        }

        public double getBearingDiff() {
            return this.brngDiff;
        }

        public double getAzimuth() {
            return this.azmth;
        }
    }
}