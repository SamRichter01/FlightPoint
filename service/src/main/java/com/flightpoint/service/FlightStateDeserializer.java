package com.flightpoint.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.*;
import java.util.Iterator;
import java.lang.reflect.Field;

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

    /**
     * This is the ugliest piece of code I have ever written.
     */
    @Override 
    public State deserialize(JsonParser parser, DeserializationContext deserializer) {
        ObjectCodec codec = parser.getCodec();

        // Parsing the array and assigning the values to State objects
        // Used this answer here: https://stackoverflow.com/questions/48642450/how-to-iterate-all-subnodes-of-a-json-object
        // and this one for hash maps: https://stackoverflow.com/questions/24094871/set-field-value-with-reflection
        try {
            //Get the current json node tree from the response.
            JsonNode node = codec.readTree(parser);

            //Get the fields of the State class with Reflect so that we can assign things to them.
            Field[] fields = State.class.getFields();

            int counter = 0;
            State state = new State("", "", "", 0, 0, 0, 0, 0, false, 0, 0, 0, new int[0], 0, "", false, 0, 0);
        
            // Replace Reflect with Spring's PropertyAccessor as described below
            //  https://stackoverflow.com/questions/10009052/invoking-setter-method-using-java-reflection
            if (node.isArray()) {
                ArrayNode arrayNode = (ArrayNode) node;
                Iterator<JsonNode> iterator = arrayNode.elements();
                while (iterator.hasNext()) {
                    JsonNode val = iterator.next();
                    //Check the class name agianst some hardcoded ones, change the output based on that.
                    switch (val.getClass().getName()) {
                        case nodeNames.TEXTNODE: 
                            fields[counter].set(state, val.asText());
                            break;
                        case nodeNames.BOOLEANNODE:
                            fields[counter].set(state, val.asBoolean());
                            break;
                        case nodeNames.DOUBLENODE:
                            fields[counter].set(state, val.asDouble());
                            break;
                        case nodeNames.INTNODE:
                            fields[counter].set(state, val.asInt());
                            break;
                        case nodeNames.NULLNODE:
                            fields[counter].set(state, null);
                            break;
                        default:
                            fields[counter].set(state, null);
                            break;
                    }
                    counter ++;
                }
            }

            return state;
        } catch (Exception e) {
            System.out.println("Reading parser tree failed");
            return null;
        }
    }

    private static class nodeNames {
        static final String TEXTNODE = "com.fasterxml.jackson.databind.node.TextNode";
        static final String BOOLEANNODE = "com.fasterxml.jackson.databind.node.BooleanNode";
        static final String INTNODE = "com.fasterxml.jackson.databind.node.IntNode";
        static final String DOUBLENODE = "com.fasterxml.jackson.databind.node.DoubleNode";
        static final String NULLNODE = "com.fasterxml.jackson.databind.node.NullNode";
    }
}