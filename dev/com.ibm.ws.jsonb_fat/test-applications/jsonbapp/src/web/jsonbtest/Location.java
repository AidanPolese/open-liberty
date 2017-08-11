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

/**
 * Interface for application class that can be marshalled/unmarshalled to/from JSON.
 */
public interface Location {
    String getBuilding();

    String getCity();

    short getFloor();

    String getState();

    String getStreetAddress();

    int getZipCode();

    void setBuilding(String b);

    void setCity(String c);

    void setFloor(short f);

    void setState(String s);

    void setStreetAddress(String s);

    void setZipCode(int z);
}
