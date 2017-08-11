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

import java.util.Date;

import javax.json.bind.annotation.JsonbTypeAdapter;

/**
 * Application class that can be marshalled/unmarshalled to/from JSON.
 */
public class Meeting {
    public Date start, end;
    @JsonbTypeAdapter(LocationAdapter.class) // JsonbAdapter disambiguates which Location subclass to use
    public Location location;
    public String title;

    @Override
    public String toString() {
        return title + ": " + start + " - " + end + " @" + location;
    }
}
