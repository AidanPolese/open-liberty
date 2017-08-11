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

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

/**
 * Application class that can be marshalled/unmarshalled to/from JSON.
 */
@JsonbPropertyOrder({ "building", "floor", "podNumber", "streetAddress",
                      "address", // temporary workaround for Johnzon
                      "city", "state", "zipCode" })
public class Pod implements Location {
    private String building;
    private String city;
    private short floor;
    private String pod;
    private String state;
    private String streetAddress;
    private int zip;

    @Override
    public String getBuilding() {
        return building;
    }

    @Override
    public String getCity() {
        return city;
    }

    @Override
    public short getFloor() {
        return floor;
    }

    public String getPodNumber() {
        return pod;
    }

    @Override
    public String getState() {
        return state;
    }

    @JsonbProperty("address")
    @Override
    public String getStreetAddress() {
        return streetAddress;
    }

    @Override
    public int getZipCode() {
        return zip;
    }

    @Override
    public void setBuilding(String b) {
        building = b;
    }

    @Override
    public void setCity(String c) {
        city = c;
    }

    @Override
    public void setFloor(short f) {
        floor = f;
    }

    public void setPodNumber(String p) {
        pod = p;
    }

    @Override
    public void setState(String s) {
        state = s;
    }

    @JsonbProperty("address")
    @Override
    public void setStreetAddress(String s) {
        streetAddress = s;
    }

    @Override
    public void setZipCode(int z) {
        zip = z;
    }

    @Override
    public String toString() {
        return building + '-' + floor + ' ' + pod + " | " + streetAddress + " | " + city + ", " + state + ' ' + zip;
    }
}