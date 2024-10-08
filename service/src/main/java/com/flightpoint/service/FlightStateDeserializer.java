package com.flightpoint.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * Custom JSON serializer for the jackson library.
 * Implemented by following this tutorial: https://www.baeldung.com/jackson-object-mapper-tutorial
 */
public class FlightStateDeserializer extends StdDeserializer<State> {

    public FlightStateDeserializer() {
        this(null);
    }

    public FlightStateDeserializer(Class<State> t) {
        super(t);
    }

    @Override 
    public State deserialize(JsonParser parser, DeserializationContext deserializer) {
        ObjectCodec codec = parser.getCodec();
        State state = new State();

        try {
            JsonNode node = codec.readTree(parser);

            JsonNode originCountryNode = node.get("origin_country");
            String originCountry = originCountryNode.asText();
            state.setOriginCountry(originCountry);

            JsonNode timePositionNode = node.get("time_position");
            int timePosition = timePositionNode.asInt();
            state.setTimePosition(timePosition);

            JsonNode lastContactNode = node.get("last_contact");
            int lastContact = lastContactNode.asInt();
            state.setLastContact(lastContact);

            JsonNode baroAltitudeNode = node.get("baro_altitude");
            double baroAltitude = baroAltitudeNode.asDouble();
            state.setBaroAltitude(baroAltitude);

            JsonNode onGroundNode = node.get("on_ground");
            boolean onGround = onGroundNode.asBoolean();
            state.setOnGround(onGround);

            JsonNode trueTrackNode = node.get("true_track");
            double trueTrack = trueTrackNode.asDouble();
            state.setTrueTrack(trueTrack);

            JsonNode verticalRateNode = node.get("vertical_rate");
            double verticalRate = verticalRateNode.asDouble();
            state.setVerticalRate(verticalRate);

            JsonNode geoAltitudeNode = node.get("geo_altitude");
            double geoAltitude = geoAltitudeNode.asDouble();
            state.setGeoAltitude(geoAltitude);

            JsonNode positionSourceNode = node.get("position_source");
            int positionSource = positionSourceNode.asInt();
            state.setPositionSource(positionSource);

            return state;
        } catch (Exception e) {
            System.out.println("Reading parser tree failed");
            return null;
        }

    }
}