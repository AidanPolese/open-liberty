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

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.json.bind.adapter.JsonbAdapter;

/**
 * Converts Location (such as ReservableRoom/Pod) to/from types that JSON-B can handle.
 */
public class LocationAdapter implements JsonbAdapter<Location, Map<String, ?>> {
    @Override
    public Location adaptFromJson(Map<String, ?> map) throws Exception {
        Location l;
        String podNumber = (String) map.get("podNumber");
        if (podNumber != null) { // must be Pod
            Pod pod = new Pod();
            pod.setPodNumber(podNumber);
            l = pod;
        } else { // must be ReservableRoom
            ReservableRoom rr = new ReservableRoom();
            rr.setCapacity(((BigDecimal) map.get("capacity")).shortValue());
            rr.setRoomName((String) map.get("roomName"));
            rr.setRoomNumber((String) map.get("roomNumber"));
            l = rr;
        }
        l.setBuilding((String) map.get("building"));
        l.setCity((String) map.get("city"));
        l.setFloor(((BigDecimal) map.get("floor")).shortValue());
        l.setState((String) map.get("state"));
        l.setStreetAddress((String) map.get("address"));
        l.setZipCode(((BigDecimal) map.get("zipCode")).intValue());
        return l;
    }

    @Override
    public Map<String, ?> adaptToJson(Location l) throws Exception {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("building", l.getBuilding());
        map.put("floor", l.getFloor());
        if (l instanceof Pod) {
            map.put("podNumber", ((Pod) l).getPodNumber());
        } else { // ReservableRoom
            map.put("roomNumber", ((ReservableRoom) l).getRoomNumber());
            map.put("roomName", ((ReservableRoom) l).getRoomName());
            map.put("capacity", ((ReservableRoom) l).getCapacity());
        }
        map.put("address", l.getStreetAddress());
        map.put("city", l.getCity());
        map.put("state", l.getState());
        map.put("zipCode", l.getZipCode());
        return map;
    }
}