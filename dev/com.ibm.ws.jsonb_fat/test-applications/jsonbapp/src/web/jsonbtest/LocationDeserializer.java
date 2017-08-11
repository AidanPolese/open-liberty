/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package web.jsonbtest;

import java.lang.reflect.Type;
import java.util.Map;

import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;

/**
 * Deserializes Location (such as ReservableRoom/Pod) as correct subclass based on the contents of the JSON.
 */
public class LocationDeserializer implements JsonbDeserializer<Location> {
    @Override
    public Location deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        Map<?, ?> map = ctx.deserialize(Map.class, parser);
        Location l;
        String podNumber = (String) map.get("podNumber");
        if (podNumber != null) { // must be Pod
            Pod pod = new Pod();
            pod.setPodNumber(podNumber);
            l = pod;
        } else { // must be ReservableRoom
            ReservableRoom rr = new ReservableRoom();

            rr.setCapacity(((Number) map.get("capacity")).shortValue());
            rr.setRoomName((String) map.get("roomName"));
            rr.setRoomNumber((String) map.get("roomNumber"));
            l = rr;
        }
        l.setBuilding((String) map.get("building"));
        l.setCity((String) map.get("city"));
        l.setFloor(((Number) map.get("floor")).shortValue());
        l.setState((String) map.get("state"));
        l.setStreetAddress((String) map.get("address"));
        l.setZipCode(((Number) map.get("zipCode")).intValue());
        return l;
    }
}
